package com.example.firstpartyapp.ui.screen

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.firstpartyapp.R
import com.example.firstpartyapp.ui.components.AppBar
import com.example.firstpartyapp.ui.navigation.Screen

@Composable
fun CompanionAppScreen(
    onDeleteClick: () -> Unit
) {
    Scaffold(
        topBar = { AppBar(name = stringResource(Screen.CompanionApp.resourceId)) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.companion_title))

            Text(stringResource(R.string.companion_hide_text))
            Button(onClick = {}) {}

            Text(stringResource(R.string.companion_delete_text))
            Button(onClick = onDeleteClick) {
                Text(stringResource(R.string.companion_delete))
            }
        }
    }
}