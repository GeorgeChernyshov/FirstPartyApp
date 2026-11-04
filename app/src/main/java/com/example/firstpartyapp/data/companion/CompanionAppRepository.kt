package com.example.firstpartyapp.data.companion

import android.Manifest
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import com.example.firstpartyapp.InstallResultReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompanionAppRepository(private val context: Context) {

    private val packageManager = context.packageManager
    private val assets = context.assets

    private val _state = MutableStateFlow(CompanionAppState.VISIBLE)
    val state = _state.asStateFlow()

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

        _state.value = CompanionAppState.DELETED
    }


    fun setCompanionVisible() {
        val visible = (_state.value == CompanionAppState.HIDDEN)

        val activity = ComponentName(
            COMPANION_PACKAGE,
            "$COMPANION_PACKAGE.MainActivity"
        )

        packageManager.setComponentEnabledSetting(
            activity,
            if (visible) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            0
        )

        _state.value = if (visible)
            CompanionAppState.VISIBLE
        else CompanionAppState.HIDDEN
    }

    companion object {
        const val COMPANION_PACKAGE = "com.example.firstpartyapp.companion"
    }
}