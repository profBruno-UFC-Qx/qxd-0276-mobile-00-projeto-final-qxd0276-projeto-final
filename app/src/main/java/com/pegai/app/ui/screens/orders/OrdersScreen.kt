package com.pegai.app.ui.screens.orders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.viewmodel.AuthViewModel

@Composable
fun OrdersScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // 1. Observa o usuário
    val user by authViewModel.usuarioLogado.collectAsState()

    // 2. Verifica se está logado
    if (user == null) {
        GuestPlaceholder(
            title = "Seus Aluguéis",
            subtitle = "Faça login para acompanhar seus pedidos e devoluções.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        // 3. Conteúdo Real (Logado)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Lista de Aluguéis (Em construção)")
        }
    }
}