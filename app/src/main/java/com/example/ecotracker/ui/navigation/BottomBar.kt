package com.example.ecotracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun EcoTrackerBottomBar(navController: NavHostController) {

    val items = listOf(
        Routes.HOME,
        Routes.HABITS,
        Routes.IMPACT,
        Routes.PROFILE
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { route ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (route) {
                            Routes.HOME -> Icons.Default.Home
                            Routes.HABITS -> Icons.Default.List
                            Routes.IMPACT -> Icons.Default.Public
                            else -> Icons.Default.Person
                        },
                        contentDescription = route
                    )
                },
                label = { Text(route.uppercase()) }
            )
        }
    }
}
