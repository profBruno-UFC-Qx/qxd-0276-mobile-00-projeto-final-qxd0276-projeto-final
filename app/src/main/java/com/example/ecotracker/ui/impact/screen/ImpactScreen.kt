package com.example.ecotracker.ui.impact.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.impact.components.ImpactCard
import com.example.ecotracker.ui.impact.viewmodel.ImpactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpactScreen(
    viewModel: ImpactViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadImpactData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impacto Social") }
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
                title = "CO₂ Evitado",
                value = "${uiState.estimatedCo2Saved} kg",
                subtitle = "Estimativa de impacto ambiental"
            )

            ImpactCard(
                title = "Pontuação",
                value = "${uiState.points}",
                subtitle = "Pontos por impacto positivo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Área reservada para mapa / GPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Impacto baseado na sua localização")
                    Text(
                        "Em breve você verá projetos próximos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
