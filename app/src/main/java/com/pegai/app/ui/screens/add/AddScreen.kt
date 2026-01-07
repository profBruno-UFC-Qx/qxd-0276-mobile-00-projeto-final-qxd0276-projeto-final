package com.pegai.app.ui.screens.add

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
fun AddScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // 1. Verifica se tem usuário
    val user by authViewModel.usuarioLogado.collectAsState()

    if (user == null) {
        // 2. Não logado: Mostra o convite
        GuestPlaceholder(
            title = "Anuncie seu Produto",
            subtitle = "Faça login para criar anúncios e começar a ganhar dinheiro alugando seus itens.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        // 3. Logado: Mostra a tela de criação (Futuramente aqui vai o formulário)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Tela de Criar Anúncio (Em construção)")
        }
    }
}