package com.treasure.hunt

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.model.LatLng
import com.treasure.hunt.http.HttpPoster
import java.lang.Math.PI
import kotlin.math.pow
import kotlin.math.sqrt


object Utils {

    val EARTH_RADIUS = 6371000
    val BASE_URL = "http://10.0.2.2:3000"

    // from https://stackoverflow.com/a/60337241/11725219
    fun formatIntString(int: Int) : String {
        return StringBuilder(int.toString().reversed()).replace("...".toRegex(), "$0 ").reversed()
    }

    fun distanceCoordsToM(coords1: LatLng, coords2: LatLng): Double {
        val lat = coords1.latitude - coords2.latitude
        val long = coords1.longitude - coords2.longitude
        return sqrt(angleToMeters(lat).pow(2) + angleToMeters(long).pow(2)) // Enclidian distance
    }

    fun metersToAngle(meters: Int) : Double {
        return (meters * 360) /( 2 * PI * EARTH_RADIUS)
    }

    fun angleToMeters(angle: Double) : Double {
        return (( 2 * PI * EARTH_RADIUS * angle) / 360)
    }

    // Based on the android docs for dialogs
    fun createConfirmationDialog(activity: Activity?, titre:String, message: String, okCallback: () -> (Unit)) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                        okCallback()
                    })
                setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            }
            // Set other dialog properties
            builder.setMessage(message)
                .setTitle(titre)

            // Create the AlertDialog
            builder.create()
        }
        alertDialog?.show()
    }

    // Based on the android docs for notifications
    fun createNotification(context: Context, title: String, short_content:String,  content: String, notifId: Int) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Treasure Hunt"
            val descriptionText = "Channel for all Treasure Hunt notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TREASURE_HUNT", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager? = getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "TREASURE_HUNT")
            .setSmallIcon(R.drawable.ic_booty)
            .setContentTitle(title)
            .setContentText(short_content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle()
            .bigText(content))

        with(NotificationManagerCompat.from(context)) {
            notify(notifId, builder.build())
        }
    }

    fun writeToSharedPrefs(key: String, value: Any, activity: Activity?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        when (value) {
            is String -> with(sharedPref.edit()) {
                putString(key, value)
                apply()
            }
            is Float -> with(sharedPref.edit()) {
                putFloat(key, value)
                apply()
            }
            is Boolean -> with(sharedPref.edit()) {
                putBoolean(key, value)
                apply()
            }
            is Int -> with(sharedPref.edit()) {
                putInt(key, value)
                apply()
            }
            is Long -> with (sharedPref.edit()) {
            putLong(key, value)
            apply()
            }
        }
    }

    fun readFloatFromSharedPrefs(key: String, activity: Activity?) : Float? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return null
        return sharedPref.getFloat(key, 0f)
    }

    fun readStringFromSharedPrefs(key: String, activity: Activity?) : String? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return null
        return sharedPref.getString(key, "")
    }

    fun readIntFromSharedPrefs(key: String, activity: Activity?) : Int? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return null
        return sharedPref.getInt(key, 0)
    }

    fun getUserId(activity: Activity?): String? {
        val userId = readStringFromSharedPrefs("user_id", activity)
        return if( userId == "" || userId == null) {
            null
        } else userId
    }

    fun post(url: String, params: Map<String, String>) {
        val httpPoster = HttpPoster(
            url
            ,
            params
        )
        val thread = Thread(httpPoster)
        try {
            thread.start()
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}