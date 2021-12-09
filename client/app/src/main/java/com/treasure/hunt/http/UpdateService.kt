package com.treasure.hunt.http

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.treasure.hunt.data.Treasure
import java.io.Serializable

class UpdateService : Service(), Serializable {
    private val binder: IBinder = UpdateManuallyBinder()
    private var httpGetter: HttpGetter? = null
    private var thread: Thread? = null
    private var arrayTreasures: ArrayList<Treasure>? = null

    inner class UpdateManuallyBinder : Binder() {
        val service: UpdateService
            get() = this@UpdateService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        arrayTreasures = ArrayList()
        httpGetter = HttpGetter(urlString)
        thread = Thread(httpGetter)
        try {
            thread!!.start()
            thread!!.join()
            arrayTreasures = httpGetter!!.listePartiesFromJSON
            startIntentBroadcast()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun startIntentBroadcast() {
        val intent = Intent()
        intent.action = QuestBroadcastReceiver.AQUIRE_QUESTS
        intent.putExtra("arrayTreasures", arrayTreasures)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val urlString = "http://10.0.2.2:3000/treasures"
    }
}