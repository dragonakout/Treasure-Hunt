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
        val booty = bundle!!.getSerializable("arrayTreasures")  as MutableList<Treasure>?
        val quests = bundle!!.getSerializable("arrayQuests")  as MutableList<Treasure>?
        if(booty != null) {ma?.collectedTreasures = booty}
        if(quests != null) {
            ma?.treasures = quests

            val navHostFragment = ma?.supportFragmentManager
                ?.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment?
            val f = navHostFragment?.childFragmentManager?.fragments?.get(0)
            if (f is QuestsFragment) {
                val questsFragment: QuestsFragment = f
                questsFragment.recyclerViewAdapter.data.clear()
                questsFragment.recyclerViewAdapter.data.addAll(quests)
                questsFragment.recyclerViewAdapter.notifyDataSetChanged()
                questsFragment.updateNoQuestUI(quests.size <= 0)
            }
        }

    }

    companion object {
        const val AQUIRE_QUESTS = "UPDATE_CURRENT_QUESTS"
    }
}