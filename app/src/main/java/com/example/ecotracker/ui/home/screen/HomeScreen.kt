package com.example.ecotracker.ui.home.screen

import com.example.ecotracker.ui.home.viewmodel.HomeViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.R

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        label = "TreeProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // 🌳 Árvore
        Image(
            painter = painterResource(
                id = when {
                    animatedProgress < 0.33f -> R.drawable.tree_stage_1
                    animatedProgress < 0.66f -> R.drawable.tree_stage_2
                    else -> R.drawable.tree_stage_3
                }
            ),
            contentDescription = "Árvore de progresso",
            modifier = Modifier
                .height(260.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 📊 Progresso
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 💬 Frase motivacional
        Text(
            text = uiState.motivationalPhrase,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.refreshPhrase() }
        ) {
            Text("Nova frase")
        }
    }
}
