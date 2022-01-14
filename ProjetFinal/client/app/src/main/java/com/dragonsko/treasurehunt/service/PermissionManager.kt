package com.dragonsko.treasurehunt.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.dragonsko.treasurehunt.Utils.ACTION_PERMISSIONS_DENIED
import com.dragonsko.treasurehunt.Utils.ACTION_PERMISSIONS_GRANTED
import com.dragonsko.treasurehunt.Utils.PERMISSIONS_KEY


class PermissionManager : BroadcastReceiver() {

    private lateinit var callback : () -> (Unit)

    fun ask(context: Context, permissions : MutableList<String>, callbackFun : () -> (Unit)) {
        callback = callbackFun
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_PERMISSIONS_GRANTED)
        intentFilter.addAction(ACTION_PERMISSIONS_DENIED)
        context.registerReceiver(this, intentFilter)
        val intent = Intent(context, AskPermissionActivity::class.java)
        intent.putExtra(PERMISSIONS_KEY, permissions.toTypedArray())
        context.startActivity(intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when {
            intent.action == ACTION_PERMISSIONS_GRANTED -> {
                context.unregisterReceiver(this)
                onPermissionsGranted()
            }
            intent.action == ACTION_PERMISSIONS_DENIED -> {
                context.unregisterReceiver(this)
                onPermissionsDenied()
            }
        }
    }

    private fun onPermissionsGranted() {
        callback.invoke()
    }

    private fun onPermissionsDenied() {
        // ...
    }
}