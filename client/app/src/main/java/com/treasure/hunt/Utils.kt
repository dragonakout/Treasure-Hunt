package com.treasure.hunt

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.model.LatLng
import java.lang.Math.PI
import kotlin.math.pow
import kotlin.math.sqrt

val EARTH_RADIUS = 6371000

object Utils {
    fun distanceCoordsToM(coords1: LatLng, coords2: LatLng): Double {
        val lat = coords1.latitude - coords2.latitude
        val long = coords1.longitude - coords2.longitude
        val lat_in_meters = 2 * PI * EARTH_RADIUS * lat / 360
        val long_in_meters = 2 * PI * EARTH_RADIUS * long / 360
        return sqrt(lat_in_meters.pow(2) + long_in_meters.pow(2)) // Enclidian distance
    }

    fun metersToAngle(meters: Int) : Double {
        return (meters * 360) /( 2 * PI * EARTH_RADIUS)
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
            // notificationId is a unique int for each notification that you must define
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
}