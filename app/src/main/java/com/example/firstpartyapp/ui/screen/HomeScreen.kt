package com.example.firstpartyapp.ui.screen

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.example.firstpartyapp.R
import com.example.firstpartyapp.ui.components.AppBar
import com.example.firstpartyapp.ui.navigation.Screen
import com.example.firstpartyapp.ui.theme.ThemePreviewWrapper

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
        }
    }
}

@Composable
@Preview
@PreviewWrapper(ThemePreviewWrapper::class)
fun HomeScreenPreview() {
    HomeScreen()
}