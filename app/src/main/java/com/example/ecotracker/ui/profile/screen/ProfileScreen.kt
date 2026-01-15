package com.example.ecotracker.ui.profile.screen

import ProfileViewModelFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.profile.components.ProfileHeader
import com.example.ecotracker.ui.profile.components.ProfileInfoRow
import com.example.ecotracker.ui.profile.components.ProfileOptionItem
import com.example.ecotracker.ui.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory
    ),
    onLogout: ()->Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val user = uiState.user
    val points = uiState.points
    val level =
        when{
            points <= 50 -> 1
            points <= 100 -> 2
            points <= 200 -> 3
            else -> 0
        }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deleteState) {
        if(deleteState){
            onLogout()
        }
    }
    // Diálogo de confirmação
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmação") },
            text = { Text("Tem certeza que deseja apagar sua conta? Esta ação é irreversível.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        user?.id?.let { viewModel.deleteAccount(it) }
                        onLogout()
                        showDialog = false
                    }
                ) {
                    Text("Apagar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Perfil") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            ProfileHeader(
                name = user?.name ?: "",
                level = level,
                points = points
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(user?.bio ?: "", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoRow("Email", user?.email ?: "")
            ProfileInfoRow("Membro desde", viewModel.formatDate(user?.createdAt))

            Spacer(modifier = Modifier.height(32.dp))

            ProfileOptionItem(
                title = "Editar perfil",
                subtitle = "Atualizar informações"
            ) {
                onNavigateToEditProfile()
            }

            ProfileOptionItem(
                title = "Sair",
                subtitle = "Encerrar sessão"
            ) {
                viewModel.logout()
                onLogout()
            }
            TextButton(
                onClick = {showDialog = true},
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Apagar Conta")
            }
        }
    }
}

