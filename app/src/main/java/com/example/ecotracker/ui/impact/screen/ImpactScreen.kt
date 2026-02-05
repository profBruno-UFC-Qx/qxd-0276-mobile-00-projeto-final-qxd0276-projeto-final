package com.example.ecotracker.ui.impact.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.R
import com.example.ecotracker.ui.impact.components.ImpactCard
import com.example.ecotracker.ui.impact.viewmodel.ImpactViewModel
import com.example.ecotracker.ui.impact.viewmodel.ImpactViewModelFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactScreen(
    viewModel: ImpactViewModel = viewModel(factory = ImpactViewModelFactory),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(uiState.habitsLocations){
        val initial = uiState.habitsLocations
            .firstOrNull() ?.latLng
            ?: LatLng(-23.5505, -46.6333)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(initial, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impacto Social") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ImpactCard(
                title = "Hábitos Concluídos",
                value = "${uiState.totalHabitsCompleted}",
                subtitle = "Ações sustentáveis realizadas"
            )

            ImpactCard(
                title = "Pontuação",
                value = uiState.points.toString(),
                subtitle = "Pontos por impacto positivo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Área para mapa / GPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        val context = LocalContext.current
                        uiState.habitsLocations.forEach{ habit ->

                            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.global_location)
                            val icon = BitmapDescriptorFactory.fromBitmap(bitmap)

                            Marker(
                                state = MarkerState(habit.latLng),
                                title = habit.name,
                                icon = icon
                            )
                        }
                    }
                }
            }
        }
    }
}
