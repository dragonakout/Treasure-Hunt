package com.treasure.hunt.http

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.navigation.fragment.NavHostFragment
import com.treasure.hunt.MainActivity
import com.treasure.hunt.R
import com.treasure.hunt.data.Treasure
import com.treasure.hunt.ui.quests.QuestsFragment
import java.io.Serializable

//  https://developer.android.com/guide/components/broadcasts
class QuestBroadcastReceiver : BroadcastReceiver(), Serializable {
    var ma : MainActivity? = null

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val navHostFragment = ma?.supportFragmentManager
            ?.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val f = navHostFragment.childFragmentManager.fragments[0]
        if (f is QuestsFragment) {
            val matchListF: QuestsFragment = f
            matchListF.recyclerViewAdapter.data.clear()
            matchListF.recyclerViewAdapter.data.addAll(bundle!!.getSerializable("arrayTreasures") as MutableList<Treasure>)
            matchListF.recyclerViewAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val AQUIRE_QUESTS = "UPDATE_CURRENT_QUESTS"
    }
}