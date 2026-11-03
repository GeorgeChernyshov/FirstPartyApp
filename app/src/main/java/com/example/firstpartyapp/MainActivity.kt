package com.example.firstpartyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firstpartyapp.ui.navigation.Screen
import com.example.firstpartyapp.ui.screen.CompanionAppScreen
import com.example.firstpartyapp.ui.screen.HomeScreen
import com.example.firstpartyapp.ui.theme.FirstPartyAppTheme
import com.example.firstpartyapp.util.CompanionAppManager

class MainActivity : ComponentActivity() {

    private lateinit var companionAppManager: CompanionAppManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }

        companionAppManager = CompanionAppManager(this)
        companionAppManager.installCompanion()
    }

    @Composable
    fun App() {
        val navController = rememberNavController()

        FirstPartyAppTheme {
            NavHost(
                navController = navController,
                startDestination = Screen.Home
            ) {
                composable<Screen.CompanionApp> {
                    CompanionAppScreen(
                        onDeleteClick = companionAppManager::deleteCompanion
                    )
                }

                composable<Screen.Home> {
                    HomeScreen(onNextClick = {
                        navController.navigate(Screen.CompanionApp)
                    })
                }
            }
        }
    }
}