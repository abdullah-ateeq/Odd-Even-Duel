package com.example.oddevenduel.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Game : Screen("game")
    object Result : Screen("result")
    object Settings : Screen("settings")
    object About : Screen("about")
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "Home", Icons.Outlined.Home),
    BottomNavItem(Screen.Settings.route, "Settings", Icons.Outlined.Settings),
    BottomNavItem(Screen.About.route, "About", Icons.Outlined.Info)
)
