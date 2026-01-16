package com.example.ecotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecotracker.ui.navigation.EcoTrackerBottomBar
import com.example.ecotracker.ui.navigation.EcoTrackerNavGraph
import com.example.ecotracker.ui.navigation.Routes
import com.example.ecotracker.ui.theme.EcoTrackerTheme
import com.example.ecotracker.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            EcoTrackerTheme(
                darkTheme = themeViewModel.isDarkTheme.value
            ) {
                EcoTrackerApp(themeViewModel)
            }
        }
    }
}

@Composable
fun EcoTrackerApp(
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.HOME,
        Routes.HABITS,
        Routes.IMPACT,
        Routes.PROFILE
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                EcoTrackerBottomBar(navController)
            }
        }
    ) { innerPadding ->

        EcoTrackerNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            themeViewModel = themeViewModel
        )
    }
}
