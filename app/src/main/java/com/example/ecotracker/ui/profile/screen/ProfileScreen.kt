package com.example.ecotracker.ui.profile.screen

import ProfileViewModelFactory
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.ecotracker.ui.theme.ThemeViewModel
import com.example.ecotracker.utils.calculateLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory
    ),
    onLogout: ()->Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme
    val uiState by viewModel.uiState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val user = uiState.user
    val points = uiState.points
    val level = calculateLevel(points)

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
            TopAppBar(
                title = { Text("Perfil") },
                actions = {
                    IconButton(onClick = { themeViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Alternar tema"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
                points = points,
                bio = user?.bio ?:""
            )

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

