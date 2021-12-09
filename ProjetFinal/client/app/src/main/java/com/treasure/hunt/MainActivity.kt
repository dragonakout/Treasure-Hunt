package com.treasure.hunt

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.databinding.ActivityMainBinding
import com.treasure.hunt.http.QuestBroadcastReceiver
import com.treasure.hunt.http.UpdateService
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    /** TODO:
     Put this in about page in settings:
     Icons from:
     - bqlqn
     - Freepik
     - Muhazdinata
     - juicy_fish
     - Google Material Design
     - Google Maps
     */
    private lateinit var binding: ActivityMainBinding

    lateinit var treasures : MutableList<Treasure>
    lateinit var collectedTreasures : MutableList<Treasure>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()
        treasures = mutableListOf()
        collectedTreasures = mutableListOf()

        if(Utils.getUserId(this).isNullOrBlank()) {
            createIdAlert()
        }
        val br = QuestBroadcastReceiver()
        br.ma = this
        val filter = IntentFilter(QuestBroadcastReceiver.AQUIRE_QUESTS)
        LocalBroadcastManager.getInstance(this).registerReceiver(br, filter)

        updateData()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navBar

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_booty_list, R.id.navigation_treasure_map, R.id.navigation_aquired_booties))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    } else -> {
                }
                }
            }
        }
        // If we already have permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        val permissions = mutableListOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        locationPermissionRequest.launch(permissions.toTypedArray())
    }

    fun collectTreasure(treasure: Treasure) {
        treasures.remove(treasure)
        treasure.collected_timestamp = getCurrentDate()
        collectedTreasures.add(treasure)
        postCollectedTreasure(treasure)
        val totalBootyString = Utils.formatIntString(collectedTreasures.sumOf { t -> t.actual_value }.roundToInt())
        Utils.writeToSharedPrefs("total_booty",totalBootyString,this)
        val notifTitle = "Butin récupéré !"
        val notifShort = "Vous avez collecté le ${treasure.name.lowercase(Locale.getDefault())} !"
        val notifDesc = "En collectant le ${treasure.name.lowercase(Locale.getDefault())}, vous avez obtenu ${treasure.actual_value.toInt()} pièces"
        Utils.createNotification(this, notifTitle, notifShort, notifDesc, treasure.id )
    }

    private fun postCollectedTreasure(treasure: Treasure) {
        val userId = Utils.getUserId(this)!!
        val params = mapOf(
            Pair("treasure_id",treasure.id.toString()),
            Pair("user_id", userId),
            Pair("collected_timestamp",treasure.collected_timestamp),
        )
        val url = Utils.BASE_URL + "/$userId/treasure"
        Utils.post(url, params)
    }

// Copié depuis:
// https://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
    fun createIdAlert() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        val edittext = EditText(this)
        alert.setMessage("Veillez choisir votre nom de pirate: ")
        alert.setTitle("Bienvenue dans Treasure Hunt")

        alert.setView(edittext)

        alert.setPositiveButton("Confirmer",
            DialogInterface.OnClickListener { dialog, whichButton -> // What ever you want to do with the value
                val writtenUserId = edittext.text.toString()
                Utils.writeToSharedPrefs("user_id", writtenUserId, this)
                val params = mapOf( Pair("user_id",writtenUserId))
                Utils.post(Utils.BASE_URL + "/user", params)
            })

        alert.show()
    }

    private fun getCurrentDate() : String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)}/${calendar.get(Calendar.YEAR)}"
    }

    fun updateData() {
        val intent = Intent(this, UpdateService::class.java)
        intent.putExtra("request_string","/quests")
        intent.putExtra("user_id","${Utils.getUserId(this)}")

        val long = Utils.readFloatFromSharedPrefs("lastLocationLongitude", this)
        val lat = Utils.readFloatFromSharedPrefs("lastLocationLatitude", this)
        if (!(lat == null || long == null || (lat == 0f && long == 0f))) {
            intent.putExtra("location_longitude", long.toString())
            intent.putExtra("location_latitude", lat.toString())
        }
        this.startService(intent)
    }
}