package com.example.firstpartyapp.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import com.example.firstpartyapp.InstallResultReceiver

class CompanionAppManager(private val context: Context) {

    private val packageManager = context.packageManager
    private val assets = context.assets

    fun installCompanion() {
        val installer = packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        ).apply {
            setAppPackageName(COMPANION_PACKAGE)
            setRequireUserAction(
                PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED
            )
        }

        val sessionId = installer.createSession(params)
        installer.openSession(sessionId).use { session ->
            assets.open("companion.apk").use { input ->
                session.openWrite("base.apk", 0, -1).use { output ->
                    input.copyTo(output)
                    session.fsync(output)
                }
            }

            val callback = PendingIntent.getBroadcast(
                context,
                sessionId,
                Intent(context, InstallResultReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_MUTABLE
            )

            session.commit(callback.intentSender)
        }
    }

    @RequiresPermission(Manifest.permission.DELETE_PACKAGES)
    fun deleteCompanion() {
        val callback = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, InstallResultReceiver::class.java).apply {
                action = "${context.packageName}.COMPANION_UNINSTALL_RESULT"
            },
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_MUTABLE
        )

        packageManager.packageInstaller.uninstall(
            COMPANION_PACKAGE,
            callback.intentSender
        )
    }

    companion object {
        const val COMPANION_PACKAGE = "com.example.firstpartyapp.companion"
    }
}