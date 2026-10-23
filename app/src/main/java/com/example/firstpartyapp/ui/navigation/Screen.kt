package com.example.firstpartyapp.ui.navigation

import androidx.annotation.StringRes
import com.example.firstpartyapp.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String, @StringRes val resourceId: Int) {

    @Serializable
    data object Home : Screen(
        route = "home",
        resourceId = R.string.label_home
    )
}