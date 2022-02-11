package com.dragonsko.treasurehunt

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dragonsko.treasurehunt.data.Quest
import com.dragonsko.treasurehunt.data.Treasure
import com.dragonsko.treasurehunt.databinding.ActivityMainBinding
import com.dragonsko.treasurehunt.service.LocationService
import com.dragonsko.treasurehunt.service.PermissionManager
import com.dragonsko.treasurehunt.ui.quests.QuestsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var last_position: LatLng? = null

    lateinit var quests : MutableList<Quest>
    lateinit var collectedTreasures : MutableList<Treasure>
    var service : LocationService? = null
    val isUpdatingMtx : Mutex = Mutex(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quests = mutableListOf()
        collectedTreasures = mutableListOf()

        if(Utils.getUserId(this).isNullOrBlank()) {
            requestLocationPermission()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val navView: BottomNavigationView = binding.navBar


          val appbarConfig = AppBarConfiguration(setOf(
                R.id.navigation_quest_list, R.id.navigation_treasure_map, R.id.navigation_collected_treasures))
        setupActionBarWithNavController(navController, appbarConfig)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
        return super.onSupportNavigateUp()
    }

    private fun requestLocationPermission() {

        // If we already have permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            initializeLocationService()
            return
        } else {
            val permissions = mutableListOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }

            val title = "Autorisation d'utiliser les données de localisation"
            val text = "Treasure Hunt utilise les données de positionnement afin de vous attribuer des trésors. Afin que l'application puisse bien fonctionner, vous devez autoriser la localisation en tout temps."
            Utils.createButtonedDialog(this, title,text , fun() { PermissionManager().ask(this, permissions, ::initializeLocationService) }, false)
        }
    }

    private fun initializeLocationService() {
        val activity = this
        (applicationContext as MainApplication).applicationScope.launch {
            service = LocationService.getLocationService(applicationContext, activity)
            runOnUiThread {
                service?.initializeLocationManager()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(service == null) {  initializeLocationService() }
        service?.quests = null
    }

    override fun onPause() {
        super.onPause()
        service?.quests = quests
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.stopSelf()
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
                Utils.writeToSharedPrefs(Keys.USER_ID, writtenUserId, this)
            })

        alert.show()
    }

    fun updateData() {
        val activity = this
        (applicationContext as MainApplication).applicationScope.launch {
            isUpdatingMtx.withLock {
                activity.generateDailyQuests()
                val data = Utils.DBgetAll(applicationContext)
                quests = data.first.toMutableList().reversed().toMutableList()
                collectedTreasures = data.second.toMutableList()

                val navHostFragment = activity.supportFragmentManager
                    ?.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment?
                val f = navHostFragment?.childFragmentManager?.fragments?.get(0)
                if (f is QuestsFragment) {
                    val questsFragment: QuestsFragment = f
                    questsFragment.recyclerViewAdapter.data.clear()
                    questsFragment.recyclerViewAdapter.data.addAll(quests)
                    runOnUiThread{
                        if(questsFragment.binding != null) {
                            questsFragment.recyclerViewAdapter.notifyDataSetChanged()
                            questsFragment.updateNoQuestUI(quests.size <= 0)
                        }
                    }

                }
            }
        }
    }

    private fun generateDailyQuests() {
        val lastTimestamp = Utils.getLastDailyQuestUpdate(this)?.toLong()
        val currentTime = getCurrentDateTimestamp()
        if(lastTimestamp == null || currentTime >= (lastTimestamp + Utils.DAY_IN_MILLIS) ) {
            if(last_position != null) {
                val newQuests = mutableListOf<Quest>()

                var numberOfQuests = Utils.readIntFromSharedPrefs(Keys.NB_DAILY_QUEST,this)
                numberOfQuests = if (numberOfQuests == 0) Utils.MAX_NUMBER_OF_DAILY_QUESTS else numberOfQuests

                for(i in 0 until numberOfQuests) {
                    val quest = Utils.generateQuest(last_position!!, this)
                    newQuests.add(quest)
                }
                Utils.DBaddMultiplieQuests(applicationContext, newQuests)
                quests.addAll(newQuests)

                Utils.writeToSharedPrefs(Keys.DAILY_QUEST_TIMESTAMP, currentTime.toString(), this)
            }
        }
    }

    private fun getCurrentDateTimestamp() : Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }
}