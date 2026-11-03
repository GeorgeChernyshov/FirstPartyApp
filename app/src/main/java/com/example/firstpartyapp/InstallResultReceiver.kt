package com.example.firstpartyapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.util.Log

class InstallResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(
            PackageInstaller.EXTRA_STATUS,
            PackageInstaller.STATUS_FAILURE
        )

        Log.d(
            TAG,
            "Status: $status; " +
                    intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
        )
    }

    companion object {
        const val TAG = "CompanionInstaller"
    }
}