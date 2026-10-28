package com.example.firstpartyapp.ui.screen

import android.app.AlarmManager
import android.content.pm.ApplicationInfo
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.example.firstpartyapp.R
import com.example.firstpartyapp.ui.components.AppBar
import com.example.firstpartyapp.ui.components.Block
import com.example.firstpartyapp.ui.navigation.Screen
import com.example.firstpartyapp.ui.theme.ThemePreviewWrapper
import kotlin.jvm.java

@Composable
fun HomeScreen() {
    val applicationInfo = LocalContext.current.applicationInfo
    val systemFlags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
    val firstParty = (applicationInfo.flags and systemFlags) != 0

    Scaffold(
        topBar = { AppBar(name = stringResource(Screen.Home.resourceId)) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(
                if (firstParty) R.string.home_1p
                else R.string.home_3p
            ))

            if (firstParty) {
                WiFiToggleBlock()
                AnimationScaleBlock()
                ChangeTimeBlock()
                RebootBlock()
            }
        }
    }
}

@Composable
@Preview
@PreviewWrapper(ThemePreviewWrapper::class)
fun WiFiToggleBlock() {
    val context = LocalContext.current
    val wifiManager = remember {
        context.getSystemService(WifiManager::class.java)
    }

    Block {
        Text(stringResource(R.string.home_wifi_toggle_text))

        Button(onClick = {
            wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
        }) {
            Text(stringResource(R.string.home_wifi_toggle_button))
        }
    }
}

@Composable
@Preview
@PreviewWrapper(ThemePreviewWrapper::class)
fun AnimationScaleBlock() {
    val context = LocalContext.current

    Block {
        Text(stringResource(R.string.home_animation_scale_text))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    listOf(
                        Settings.Global.WINDOW_ANIMATION_SCALE,
                        Settings.Global.TRANSITION_ANIMATION_SCALE,
                        Settings.Global.ANIMATOR_DURATION_SCALE
                    ).forEach { setting ->
                        Settings.Global.putFloat(
                            context.contentResolver,
                            setting,
                            1f
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.home_animation_scale_normal_button))
            }

            Button(
                onClick = {
                    listOf(
                        Settings.Global.WINDOW_ANIMATION_SCALE,
                        Settings.Global.TRANSITION_ANIMATION_SCALE,
                        Settings.Global.ANIMATOR_DURATION_SCALE
                    ).forEach { setting ->
                        Settings.Global.putFloat(
                            context.contentResolver,
                            setting,
                            5f
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.home_animation_scale_slow_button))
            }
        }
    }
}

@Composable
@Preview
@PreviewWrapper(ThemePreviewWrapper::class)
fun ChangeTimeBlock() {
    val context = LocalContext.current
    val alarmManager = remember {
        context.getSystemService(AlarmManager::class.java)
    }

    Block {
        Text(stringResource(R.string.home_change_time_text))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    alarmManager.setTime(System.currentTimeMillis() + 3_600_000L)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.home_add_hour_button))
            }

            Button(
                onClick = {
                    alarmManager.setTime(System.currentTimeMillis() - 3_600_000L)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.home_remove_hour_button))
            }
        }
    }
}

@Composable
@Preview
@PreviewWrapper(ThemePreviewWrapper::class)
fun RebootBlock() {
    val context = LocalContext.current
    val powerManager = remember {
        context.getSystemService(PowerManager::class.java)
    }

    Block {
        Text(stringResource(R.string.home_reboot_text))

        Button(onClick = {
            powerManager.reboot(null)
        }) {
            Text(stringResource(R.string.home_reboot_button))
        }
    }
}

@Composable
@Preview
@PreviewWrapper(ThemePreviewWrapper::class)
fun HomeScreenPreview() {
    HomeScreen()
}