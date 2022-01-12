package com.dragonsko.treasurehunt

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.dragonsko.treasurehunt.data.Treasure
import com.dragonsko.treasurehunt.data.Quest
import com.dragonsko.treasurehunt.databinding.ActivityMainBinding
import com.dragonsko.treasurehunt.service.LocationService
import com.dragonsko.treasurehunt.ui.quests.QuestsFragment
import kotlinx.coroutines.launch


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
    var last_position: LatLng? = null

    lateinit var quests : MutableList<Quest>
    lateinit var collectedTreasures : MutableList<Treasure>
    var service : LocationService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addTreasure()


        requestLocationPermission()

        quests = mutableListOf()
        collectedTreasures = mutableListOf()

        if(Utils.getUserId(this).isNullOrBlank()) {
            createIdAlert()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navBar

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_booty_list, R.id.navigation_treasure_map, R.id.navigation_aquired_booties))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun addTreasure() {
        val quest = Quest(0, "Gros trésor maudit",10000,12000, 45.3757154,-71.8995804, true)
        Utils.DBinsert(quest, applicationContext)
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
            InitializeLocationService()
        }
        // If we already have permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            InitializeLocationService()
            return
        } else {
            val title = "Autorisation d'utiliser les données de localisation"
            val text = "Treasure Hunt utilise les données de positionnement afin de vous attribuer des trésors. Afin que l'application puisse bien fonctionner, vous devez autoriser la localisation en tout temps."
            Utils.createButtonedDialog(this, title,text , fun() {}, false)
        }
        val permissions = mutableListOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        locationPermissionRequest.launch(permissions.toTypedArray())
        InitializeLocationService()
    }

    private fun InitializeLocationService() {
        val activity = this
        (applicationContext as MainApplication).applicationScope.launch {
            service = LocationService.getLocationService(applicationContext, activity)
            runOnUiThread {
                service?.initializeLocationManager()
            }
            updateData()
        }
    }

    override fun onResume() {
        super.onResume()
        if(service == null) {  InitializeLocationService() }
        service?.isActivityActive = true
        service?.quests = null
    }

    override fun onPause() {
        super.onPause()
        service?.isActivityActive = false
        service?.quests = quests
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.reset()
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
            })

        alert.show()
    }

    fun updateData() {
        val activity = this
        (applicationContext as MainApplication).applicationScope.launch {
                val data = Utils.DBgetAll(applicationContext)
                quests = data.first.toMutableList()
                collectedTreasures = data.second.toMutableList()

                val navHostFragment = activity.supportFragmentManager
                    ?.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment?
                val f = navHostFragment?.childFragmentManager?.fragments?.get(0)
                if (f is QuestsFragment) {
                    val questsFragment: QuestsFragment = f
                    questsFragment.recyclerViewAdapter.data.clear()
                    questsFragment.recyclerViewAdapter.data.addAll(quests)
                    runOnUiThread{
                        questsFragment.recyclerViewAdapter.notifyDataSetChanged()
                        questsFragment.updateNoQuestUI(quests.size <= 0)
                    }

                }
        }
    }
}