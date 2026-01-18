package com.example.ecotracker.ui.home.screen

import com.example.ecotracker.ui.home.viewmodel.HomeViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.home.components.AnimatedTree
import com.example.ecotracker.ui.home.components.MotivationalCard
import com.example.ecotracker.ui.home.viewmodel.HomeViewModelFactory
import com.example.ecotracker.utils.calculateProgress
import com.google.maps.android.compose.Circle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = calculateProgress(uiState.points) ,
        label = "Progresso para o proximo nível"
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("EcoTracker")},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ){
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("Nível ${uiState.level}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                    Text("${uiState.points} pontos")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Árvore
            AnimatedTree(
                treeStage = uiState.treeStage,
                imageSize = 230.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progresso
            Text(text = "Progresso ", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Frase motivacional
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                MotivationalCard(uiState.motivationalPhrase)
            }
        }
    }
}
