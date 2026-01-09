package com.example.ecotracker.ui.habits.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ecotracker.ui.habits.viewmodel.HabitViewModel
import com.example.ecotracker.ui.map.screen.MapSelectionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit
) {
    // Estados do formulário
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estados para controlar os dados de localização
    var locationName by remember { mutableStateOf<String?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    var isNameError by remember { mutableStateOf(false) }
    val operationUiState by viewModel.uiState.collectAsState()

    // Estado para controlar a visibilidade da tela do mapa
    var showMapScreen by remember { mutableStateOf(false) }

    if (showMapScreen) {
        // Exibe a tela do mapa como um diálogo em tela cheia
        Dialog(onDismissRequest = { showMapScreen = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            MapSelectionScreen(
                onPlaceSelected = { place ->
                    locationName = place.name ?: place.address
                    latitude = place.latLng?.latitude
                    longitude = place.latLng?.longitude
                    showMapScreen = false // Fecha o mapa
                },
                onCancel = { showMapScreen = false }
            )
        }
    }

    // ... (Seu LaunchedEffect para reagir a operationUiState permanece o mesmo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Adicionar Novo Hábito")},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // CAMPO DE TEXTO PARA LOCALIZAÇÃO (NÃO-EDITÁVEL DIRETAMENTE)
            OutlinedTextField(
                value = locationName ?: "Nenhum local selecionado",
                onValueChange = { /* Não faz nada */ },
                label = { Text("Local do Hábito") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMapScreen = true }, // Abre o mapa ao clicar
                enabled = false, // Desabilita edição direta
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            // ... (Botão de Salvar, Indicador de Carregamento e Mensagens de Erro)
            Button(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        val currentUserId = 1L
                        viewModel.addHabit(
                            name = name,
                            userId = currentUserId,
                            description = description,
                            latitude = latitude,
                            longitude = longitude,
                            locationName = locationName // Passa o nome do local
                        )
                    }
                },
                // ...
            ) {
                Text("Salvar Hábito")
            }
            //...
        }
    }
}
