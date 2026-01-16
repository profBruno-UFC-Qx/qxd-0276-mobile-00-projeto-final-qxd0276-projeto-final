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
    habitViewModel: HabitViewModel,
    habitId: Long? = null,
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(habitId) {
        if (habitId != null) {
            habitViewModel.startEditHabit(habitId)
        }
    }

    val uiState by habitViewModel.addHabitUiState.collectAsState()

    // Tela do mapa como diálogo
    if (uiState.showMapScreen) {
        Dialog(
            onDismissRequest = { habitViewModel.onShowMapChange(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            MapSelectionScreen(
            onPlaceSelected = { place ->
                habitViewModel.onLocationSelected(
                    locationName = place.name,
                    latitude = place.latLng.latitude,
                    longitude = place.latLng.longitude
                )
            },
            onCancel = { habitViewModel.onShowMapChange(false) }
        )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if(uiState.habitId == null)
                            "Adicionar Novo Hábito"
                        else
                            "Editar Hábito"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
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
                value = uiState.name,
                onValueChange = {
                    habitViewModel.onNameChange(it)
                },
                label = { Text("Nome do Hábito") },
                isError = uiState.isNameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.isNameError) {
                Text(
                    text = "Nome do hábito é obrigatório",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Descrição
            OutlinedTextField(
                value = uiState.description,
                onValueChange = {
                    habitViewModel.onDescriptionChange(it)
                },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            // Localização (clicável)
            OutlinedTextField(
                value = uiState.locationName ?: "Nenhum local selecionado",
                onValueChange = {},
                label = { Text("Local do Hábito") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { habitViewModel.onShowMapChange(true) },
                enabled = false,
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            // Botão salvar / atualizar
            Button(
                onClick = {
                    habitViewModel.submitHabit {
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if(uiState.habitId == null){
                        "Salvar Hábito"
                    } else{
                        "Atualizar Hábito"
                    }
                )
            }
        }
    }
}
