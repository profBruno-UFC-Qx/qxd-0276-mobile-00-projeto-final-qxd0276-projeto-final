package com.example.ecotracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ecotracker.ui.theme.ThemeViewModel

@Composable
fun MainScaffold(
    rootNavController: NavController,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            EcoTrackerBottomBar(navController)
        }
    ) { padding ->
        MainNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding),
            themeViewModel = themeViewModel,
            onLogout = {
                rootNavController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
            }
        )
    }
}
