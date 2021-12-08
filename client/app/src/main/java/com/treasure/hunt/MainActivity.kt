package com.treasure.hunt

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.databinding.ActivityMainBinding
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    /** TODO:
     Put this in about page in settings:
     Icons from:
     - bqlqn
     - Freepik
     - Muhazdinata
     */
    private lateinit var binding: ActivityMainBinding

    lateinit var treasures : MutableList<Treasure>
    lateinit var collectedTreasures : MutableList<Treasure>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()
        treasures = createTreasures()
        collectedTreasures = mutableListOf()

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

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    fun collectTreasure(treasure: Treasure) {
        treasures.remove(treasure)
        collectedTreasures.add(treasure)
        val notifTitle = "Butin récupéré !"
        val notifShort = "Vous avez collecté le ${treasure.size.lowercase(Locale.getDefault())} ${treasure.name} ${treasure.adjective}!"
        val notifDesc = "En collectant le ${treasure.size.lowercase(Locale.getDefault())} ${treasure.name} ${treasure.adjective}, vous avez obtenu ${treasure.actual_value.toInt()} pièces"
        Utils.createNotification(this, notifTitle, notifShort, notifDesc, treasure.id )
    }

    fun createTreasures() : MutableList<Treasure>{
        val list : MutableList<Treasure> = mutableListOf()
        for(i in 0..2) {
            val booty_size = POSSIBLE_BOOTY_SIZE[Random.Default.nextInt(0,POSSIBLE_BOOTY_SIZE.size)]
            val booty_name = POSSIBLE_BOOTY_NAME[Random.Default.nextInt(0,POSSIBLE_BOOTY_NAME.size)]
            val booty_adj = POSSIBLE_BOOTY_ADJECTIVE[Random.Default.nextInt(0,POSSIBLE_BOOTY_ADJECTIVE.size)]
            val booty_value = Random.Default.nextInt(5,20) * 1000
            val booty_value_mul = 1 + (Random.Default.nextInt(1,10) / 10.0)
            val booty_lat = MINIMUM_LATTITUDE + Random.Default.nextFloat() * (MAXIMUM_LATTITUDE - MINIMUM_LATTITUDE)
            val booty_lon = MINIMUM_LONGITIDE + Random.Default.nextFloat() * (MAXIMUM_LONGITIDE - MINIMUM_LONGITIDE)
            val treasure = Treasure(i, booty_size, booty_adj, booty_name, booty_lat, booty_lon, booty_value, booty_value * booty_value_mul,"1h")
            list.add(treasure)
        }
        return list
    }

    companion object {
        val POSSIBLE_BOOTY_SIZE = listOf("Gigantesque", "Immense", "Gros", "Abondant", "Grand", "Maigre", "Petit", "Massif")
        val POSSIBLE_BOOTY_NAME = listOf("trésor", "héritage", "magot", "butin")
        val POSSIBLE_BOOTY_ADJECTIVE = listOf("maudit", "mythique", "fantastique", "légendaire", "épique", "glorieux", "oublié", "prisé", "sanglant", "royal", "scintillant", "inimaginable")

        val test_MINIMUM_LATTITUDE = 45.38457840343907
        val test_MINIMUM_LONGITIDE = -71.90472273049694
        val MINIMUM_LATTITUDE = 45.3755851874 //45.37167696186306
        val MAXIMUM_LATTITUDE = 45.3935716195 //45.429208924836395
        val MINIMUM_LONGITIDE = -71.9137159465 //-71.96296752784556
        val MAXIMUM_LONGITIDE = -71.8957295144 //-71.86304785226521
    }
}