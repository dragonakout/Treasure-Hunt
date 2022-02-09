package com.dragonsko.treasurehunt.service

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dragonsko.treasurehunt.MainActivity
import com.dragonsko.treasurehunt.MainApplication
import com.dragonsko.treasurehunt.R
import com.dragonsko.treasurehunt.Utils
import com.dragonsko.treasurehunt.data.Quest
import com.dragonsko.treasurehunt.data.Treasure
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*


class LocationService : Service(), Serializable, LocationListener {
    private var initialized: Boolean = false
    var activity: MainActivity? = null
    var quests: List<Quest>? = null
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if(action == "stop_service") {
                val serviceIntent = Intent(context, LocationService::class.java)
                context.stopService(serviceIntent)
            }
        }
    }


    var mapNotify: ((LatLng) -> Unit?)? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction("stop_service");
        registerReceiver(receiver, filter);

        val channel = NotificationChannel(
            "th_location_service",
            "Treasure Hunt Location Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val snoozeIntent = Intent("stop_service")
        val snoozePendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification: Notification =
            Notification.Builder(this, "th_location_service")
                .setContentTitle("Treasure Hunt")
                .setContentText("Treasure Hunt is currently using your location. Arrr!")
                .addAction(R.drawable.ic_coins, "Avast!",
                    snoozePendingIntent)
                .setSmallIcon(R.drawable.ic_steering_wheel)
                .build()

        startForeground(Utils.FOREGROUND_SERVICE_ID, notification)
        INSTANCE = this

        return super.onStartCommand(intent, flags, startId)
    }


    fun initializeLocationManager() {
        if( !initialized ) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                requestBackgroundPermission()
                activity?.updateData()
                val locationManager = ContextCompat.getSystemService(
                    applicationContext,
                    LocationManager::class.java
                ) as LocationManager

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    1f,
                    this
                )
                initialized = true;
            }
        }
    }

    private fun requestBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
                val bgPermission = mutableListOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                if (activity != null) {
                    PermissionManager().ask(activity!!, bgPermission, fun() {activity?.updateData()})
                }
            }
        }

    }

    override fun onLocationChanged(location: Location) {
        checkGeofences(location.latitude, location.longitude)
        activity?.last_position = LatLng(location.latitude, location.longitude)
        activity?.updateData()
        if (activity != null) {
            mapNotify?.let { it(LatLng(location.latitude, location.longitude)) }
        }
    }


    private fun checkGeofences(latitude: Double, longitude: Double) {
        if(activity == null && quests.isNullOrEmpty()) return
        val treasures = if(quests.isNullOrEmpty()) activity?.quests?.toList() else quests?.toList()
        for (treasure in treasures!!) {
            val treasureCoords = LatLng(treasure.latitude, treasure.longitude)
            if (Utils.distanceCoordsToM(
                    treasureCoords,
                    LatLng(latitude, longitude)
                ) < Utils.GEOFENCE_RADIUS_IN_METERS
            ) {
                collectTreasure(treasure)
            }
        }
    }

    fun collectTreasure(quest: Quest) {
        (applicationContext as MainApplication).applicationScope.launch {
            Utils.DBdelete(quest, applicationContext)
            val collectedTreasure = Treasure(quest, getCurrentDate())
            activity?.quests?.remove(quest)
            activity?.collectedTreasures?.add(collectedTreasure)
            Utils.DBinsert(collectedTreasure, applicationContext)

            val notifTitle = "Butin récupéré !"
            val notifShort = "Vous avez collecté le ${quest.name.lowercase(Locale.getDefault())} !"
            val notifDesc =
                "En collectant le ${quest.name.lowercase(Locale.getDefault())}, vous avez obtenu ${quest.actual_value} pièces"
            Utils.createNotification(applicationContext, notifTitle, notifShort, notifDesc, quest.id)
            if (activity != null) {
                activity?.runOnUiThread {
                    Utils.createButtonedDialog(activity, notifTitle, notifDesc, fun() {}, false)
                    activity?.updateData()
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val monthString : String = if(calendar.get(Calendar.MONTH) < 9) "0" + (calendar.get(Calendar.MONTH) + 1) else (calendar.get(Calendar.MONTH) + 1).toString()
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/$monthString/${calendar.get(Calendar.YEAR)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.service = null
        activity = null
        unregisterReceiver(receiver)
        INSTANCE = null
        initialized = false
    }

    fun reset() {
        activity = null
        initialized = false
    }


    companion object {
        private var INSTANCE: LocationService? = null

        fun getLocationService(context: Context, mainActivity: MainActivity): LocationService {
            if (INSTANCE != null) {
                INSTANCE!!.activity = mainActivity
                return INSTANCE!!
            }
            val serviceIntent = Intent(context, LocationService::class.java)
            context.startForegroundService(serviceIntent)
            while (INSTANCE == null) {}
            INSTANCE!!.activity = mainActivity
            return INSTANCE!!
        }

    }
}