package com.example.bookkeeper.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.* // <-- Importante: Imports do Lottie
import com.example.bookkeeper.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen() {
    // --- Configuração da Animação Lottie ---
    // 1. Carrega o arquivo json da pasta raw
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.book_loading_anim))

    // 2. Controla o progresso da animação (loop infinito)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Faz ficar repetindo para sempre
    )

    // --- Configuração das frases ---
    val messages = listOf(
        "Abrindo a estante...",
        "Catalogando livro...",
        "Organizando páginas...",
        "Preparando o café..."
    )
    var currentMessageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200) // Aumentei um pouco o tempo para dar tempo de ler
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Fundo Creme/Café
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // --- ONDE A MÁGICA ACONTECE ---
            // Substituímos o CircularProgressIndicator por isso:
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(180.dp) // Ajuste o tamanho conforme necessário
                    .padding(bottom = 16.dp)
            )

            // O texto charmoso embaixo
            Text(
                text = messages[currentMessageIndex],
                style = MaterialTheme.typography.headlineMedium, // Usa a fonte Bellefair
                color = MaterialTheme.colorScheme.primary, // Cor Bronze/Dourada
                fontWeight = FontWeight.Bold
            )
        }
    }
}