package com.example.ecotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ecotracker.ui.navigation.EcoTrackerNavGraph
import com.example.ecotracker.ui.theme.EcoTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EcoTrackerTheme {
                EcoTrackerApp()
            }
        }
    }
}

@Composable
fun EcoTrackerApp() {
    val navController = rememberNavController()

    Scaffold{ innerPadding ->
        EcoTrackerNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}