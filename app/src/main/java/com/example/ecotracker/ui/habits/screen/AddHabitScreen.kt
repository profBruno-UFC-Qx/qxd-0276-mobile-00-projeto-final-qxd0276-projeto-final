package com.example.ecotracker.ui.habits.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Estados de localização
    var locationName by remember { mutableStateOf<String?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    // Validação
    var isNameError by remember { mutableStateOf(false) }

    // Estado do mapa
    var showMapScreen by remember { mutableStateOf(false) }

    // Tela do mapa como diálogo
    if (showMapScreen) {
        Dialog(
            onDismissRequest = { showMapScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            MapSelectionScreen(
                onPlaceSelected = { place ->
                    locationName = place.name ?: place.address
                    latitude = place.latLng?.latitude
                    longitude = place.latLng?.longitude
                    showMapScreen = false
                },
                onCancel = { showMapScreen = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Novo Hábito") }
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

            // Nome do hábito
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameError = it.isBlank()
                },
                label = { Text("Nome do Hábito") },
                isError = isNameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (isNameError) {
                Text(
                    text = "Nome do hábito é obrigatório",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Descrição (opcional)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            // Localização (clicável)
            OutlinedTextField(
                value = locationName ?: "Nenhum local selecionado",
                onValueChange = {},
                label = { Text("Local do Hábito") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMapScreen = true },
                enabled = false,
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            // Botão salvar
            Button(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        val currentUserId = 1L // substituir pelo ID do usuário logado
                        viewModel.addHabit(
                            name = name,
                            userId = currentUserId,
                            description = description,
                            latitude = latitude,
                            longitude = longitude,
                            locationName = locationName
                        )
                        // Resetar campos ou navegar de volta
                        name = ""
                        description = ""
                        locationName = null
                        latitude = null
                        longitude = null
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Hábito")
            }
        }
    }
}
