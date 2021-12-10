package com.treasure.hunt.http

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.treasure.hunt.Utils
import com.treasure.hunt.data.Treasure
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable
import java.lang.Exception

class UpdateService : Service(), Serializable {
    private val binder: IBinder = UpdateManuallyBinder()
    private var httpConnecter: HttpConnecter? = null
    private var arrayQuests: ArrayList<Treasure>? = null
    private var arrayTreasures: ArrayList<Treasure>? = null
    private var requestString: String? = null
    private var queryParams: MutableMap<String, String>? = null


    inner class UpdateManuallyBinder : Binder() {
        val service: UpdateService
            get() = this@UpdateService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    private fun startIntentBroadcast() {
        val intent = Intent()
        intent.action = QuestBroadcastReceiver.AQUIRE_QUESTS
        intent.putExtra("arrayQuests", arrayQuests)
        intent.putExtra("arrayTreasures", arrayTreasures)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        requestString = intent.getStringExtra("request_string")
        val isQuests = intent.getBooleanExtra("is_quests", true)
        try {
            queryParams = mutableMapOf()
            queryParams?.put("location_longitude", intent.getStringExtra("location_longitude")!!)
            queryParams?.put("location_latitude", intent.getStringExtra("location_latitude")!!)
            queryParams?.put("user_id", intent.getStringExtra("user_id")!!)
        } catch (e: Exception) {
            print(e.stackTrace)
        }
        httpConnecter = HttpConnecter(Utils.BASE_URL + requestString, queryParams)
        arrayQuests = ArrayList()
        arrayTreasures = ArrayList()
        GlobalScope.launch {
            httpConnecter?.run();
            val parsedResponse = httpConnecter!!.listeTreasureDataFromJSON ;
            arrayQuests = parsedResponse.first
            arrayTreasures = parsedResponse.second
            startIntentBroadcast() }

        return super.onStartCommand(intent, flags, startId)
    }
}