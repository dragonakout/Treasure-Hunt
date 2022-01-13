package com.dragonsko.treasurehunt

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
import com.dragonsko.treasurehunt.data.Treasure
import com.dragonsko.treasurehunt.data.Quest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.nextUp


object Utils {

    val FOREGROUND_SERVICE_ID = 4242690
    val EARTH_RADIUS_KM = 6371
    val GEOFENCE_RADIUS_IN_METERS = 100.0
    val NUMBER_OF_DAILY_QUESTS = 2
    val AVERAGE_DISTANCE_M = 1000
    val DAY_IN_MILLIS = 86400000
    val COIN_ESTIMATE_DIVIDER = 100
    val COIN_ACTUAL_DIVIDER = 5


    // from https://stackoverflow.com/a/60337241/11725219
    fun formatIntString(int: Int) : String {
        return StringBuilder(int.toString().reversed()).replace("...".toRegex(), "$0 ").reversed()
    }

    fun distanceCoordsToM(coords1: LatLng, coords2: LatLng): Double {
        return distanceBetweenCoords(
            coords1.latitude, coords2.latitude,
            coords1.longitude, coords2.longitude)
    }

    /**
    From https://stackoverflow.com/a/3694410/11725219
     */
    /**
     * Calculate distance between two points in latitude and longitude.
     *
     * lat1, lon1 Start point lat2, lon2 End point
     * @returns Distance in Meters
     */
    private fun distanceBetweenCoords(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = (Math.pow(Math.sin(latDistance / 2), 2.0)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(lonDistance / 2), 2.0)))
        val arcLength = 2 * Math.asin(Math.sqrt(a))
        return EARTH_RADIUS_KM * arcLength * 1000 // convert to meters
    }


    private fun getRandomPositionFromCenter(pos: LatLng, distance: Double): LatLng {
        val possibleMultipliers = listOf(-1, 1)
        val surfaceAngle = Random().nextDouble()

        val latitudeOrientation = Random().nextInt(possibleMultipliers.size)
        val longitudeOrientation = Random().nextInt(possibleMultipliers.size)


        val distRad = distance / 1000 / EARTH_RADIUS_KM
        val distanceLatRad = Math.atan(Math.tan(distRad) * Math.cos(surfaceAngle))
        val distanceLongRad = Math.asin(Math.sin(distRad) * Math.sin(surfaceAngle))
        val latitude = pos.latitude + latitudeOrientation * Math.toDegrees(distanceLatRad)
        val longitude = pos.longitude + longitudeOrientation * Math.toDegrees(distanceLongRad)

        // placeholder: LatLng(45.3757154,-71.8995804)
        return LatLng(latitude, longitude)
    }

    fun createConfirmationDialog(activity: Activity?, titre:String, message: String, okCallback: () -> (Unit)) {
        createButtonedDialog(activity, titre, message, okCallback, true)
    }

    // Based on the android docs for dialogs
    fun createButtonedDialog(activity: Activity?, titre:String, message: String, okCallback: () -> (Unit), hasNegativeChoice : Boolean) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                        okCallback()
                    })
                if(hasNegativeChoice) {
                    setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
                }
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

    fun getLastDailyQuestUpdate(activity: Activity?): String? {
        val timestamp = readStringFromSharedPrefs("daily_quest_update_timestamp", activity)
        return if( timestamp.isNullOrBlank()) null else timestamp
    }

    fun DBinsert(treasure : Any, applicationContext : Context?) {
        (applicationContext as MainApplication).applicationScope.launch {
            when (treasure) {
                is Quest -> {
                    (applicationContext as MainApplication).database.QuestDao()
                        .createQuest(treasure)
                }
                is Treasure -> {
                    (applicationContext as MainApplication).database.TreasuresDao()
                        .createTreasure(treasure)
                }
            }
        }
    }

    fun DBdelete(treasure : Any, applicationContext : Context?) {
        (applicationContext as MainApplication).applicationScope.launch {
            when(treasure) {
                is Quest -> {
                    (applicationContext as MainApplication).database.QuestDao().deleteQuest(treasure)
                }
                is Treasure -> {
                    (applicationContext as MainApplication).database.TreasuresDao().deleteTreasure(treasure)
                }
            }
        }
    }

    fun DBgetAll(applicationContext : Context?) : Pair<List<Quest>, List<Treasure>> {
            val quests = (applicationContext as MainApplication).database.QuestDao().getAllQuests()
            val treasures = (applicationContext as MainApplication).database.TreasuresDao().getAllTreasure()
        return Pair(quests, treasures)
    }

    fun generateQuest(position: LatLng): Quest {
        val multiplier = (Random().nextGaussian() + 3) / 3
        val distance = AVERAGE_DISTANCE_M * multiplier
        val treasurePos = getRandomPositionFromCenter(position, distance)
        val values = getValuesFromDistance(distance)
        return Quest(0, "Gros trésor maudit",values.first,values.second, treasurePos.latitude,treasurePos.longitude, true)
    }

    private fun getValuesFromDistance(distance: Double) : Pair<Int,Int> {
        val estimate = distance - distance % COIN_ESTIMATE_DIVIDER
        val actual_tmp = Math.floor(Math.pow(estimate, 1 + (Random().nextGaussian() + 3) / 72))
        val actual = actual_tmp - actual_tmp % COIN_ACTUAL_DIVIDER

        return Pair(estimate.toInt(), actual.toInt())
    }

}