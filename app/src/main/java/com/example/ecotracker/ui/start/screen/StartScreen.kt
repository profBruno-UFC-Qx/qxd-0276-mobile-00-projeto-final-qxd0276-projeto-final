package com.example.ecotracker.ui.start.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.start.viewmodel.StartDestination
import com.example.ecotracker.ui.start.viewmodel.StartViewModel
import com.example.ecotracker.ui.start.viewmodel.StartViewModelFactory

@Composable
fun StartScreen(
    viewModel: StartViewModel = viewModel(factory = StartViewModelFactory),
    onGoToHome: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val destination by viewModel.destination.collectAsState()

    // Observa o estado de destino e decide a navegação
    LaunchedEffect(destination) {
        when (destination){
            StartDestination.Home -> onGoToHome()
            StartDestination.Login -> onGoToLogin()
            StartDestination.Loading -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (destination == StartDestination.Login) {
            CircularProgressIndicator()
        }
    }
}
