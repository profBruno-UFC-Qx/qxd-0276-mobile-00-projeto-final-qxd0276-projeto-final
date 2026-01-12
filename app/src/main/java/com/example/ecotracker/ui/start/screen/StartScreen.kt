package com.example.ecotracker.ui.start.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.start.viewmodel.StartViewModel
import com.example.ecotracker.ui.start.viewmodel.StartViewModelFactory

@Composable
fun StartScreen(
    viewModel: StartViewModel = viewModel(factory = StartViewModelFactory),
    onGoToHome: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val loading by viewModel.loading.collectAsState()

    // Flag para garantir que só navegamos uma vez
    var navigated by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            // Se não houver usuário logado e ainda não navegamos
            if (!navigated) {
                LaunchedEffect(Unit) {
                    navigated = true
                    onGoToLogin()
                }
            }
        }
    }

    // Callback para quando houver usuário logado
    LaunchedEffect(Unit) {
        viewModel.setOnUserLogged {
            if (!navigated) {
                navigated = true
                onGoToHome()
            }
        }
    }
}
