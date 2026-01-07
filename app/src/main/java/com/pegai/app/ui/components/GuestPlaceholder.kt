package com.pegai.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun GuestPlaceholder(
    title: String,
    subtitle: String,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Definição do Degradê (O mesmo usado no Header e BottomBar)
    val mainGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A5C8A), // Azul escuro
            Color(0xFF0E8FC6), // Azul médio
            Color(0xFF2ED1B2)  // Verde água
        )
    )

    // Cor sólida para o texto do botão de criar conta (para garantir leitura)
    val mainColor = Color(0xFF0E8FC6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- BOTÃO ENTRAR (COM DEGRADÊ NO FUNDO) ---
        Button(
            onClick = onLoginClick,
            // 1. Removemos a cor padrão e o padding interno do botão
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            // 2. Criamos uma Box interna com o Background Degradê
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(mainGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Entrar",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTÃO CRIAR CONTA (COM DEGRADÊ NA BORDA) ---
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            // Passamos o Brush (degradê) para o BorderStroke
            border = BorderStroke(2.dp, mainGradient),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Criar Conta",
                color = mainColor, // Texto azul sólido
                fontWeight = FontWeight.Bold
            )
        }
    }
}