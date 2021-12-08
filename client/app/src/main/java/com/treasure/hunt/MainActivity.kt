package com.treasure.hunt

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.databinding.ActivityMainBinding
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
    lateinit var geofencingClient: GeofencingClient

    val treasures by lazy { createTreasures() }
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermission()
        initializeGeofences()
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

    fun initializeGeofences() {
        // if location permission is refused, do not initialize geofences
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) { return }

        geofencingClient = LocationServices.getGeofencingClient(this)
        val geofences = createGeofencesFromTreasures(treasures)
        geofencingClient.addGeofences(getGeofencingRequest(geofences), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.i("Geofences","Geofences successfully added.")
            }
            addOnFailureListener {
                Log.e("Geofences","Geofences unable to be added.")
            }
        }
    }

    fun createTreasures() : List<Treasure>{
        val list : MutableList<Treasure> = mutableListOf()
        for(i in 0..9) {
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

    fun createGeofencesFromTreasures(treasures: List<Treasure> ) : List<Geofence>  {
        val geofences : MutableList<Geofence> =  mutableListOf()
        for(treasure in treasures) {
            geofences.add(
                Geofence.Builder()
                    .setRequestId(treasure.id.toString())
                    .setCircularRegion(treasure.longitude, treasure.lattitude, 100f)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE) // 86400000ms for 24h of duration
                    .build()
            )
        }
        return geofences
    }

    private fun getGeofencingRequest(geofences: List<Geofence> ): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(geofences)
        }.build()
    }

    companion object {
        val POSSIBLE_BOOTY_SIZE = listOf("Gigantesque", "Immense", "Gros", "Abondant", "Grand", "Maigre", "Petit", "Massif")
        val POSSIBLE_BOOTY_NAME = listOf("trésor", "héritage", "magot", "butin")
        val POSSIBLE_BOOTY_ADJECTIVE = listOf("maudit", "mythique", "fantastique", "légendaire", "épique", "glorieux", "oublié", "prisé", "sanglant", "royal", "scintillant", "inimaginable")

        val MINIMUM_LATTITUDE = 45.37167696186306
        val MAXIMUM_LATTITUDE = 45.429208924836395
        val MINIMUM_LONGITIDE = -71.96296752784556
        val MAXIMUM_LONGITIDE = -71.86304785226521
    }
}