package com.example.bookkeeper.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookkeeper.BabyPink
import com.example.bookkeeper.DarkGrey
import com.example.bookkeeper.LightPinkBg
import com.example.bookkeeper.SoftRose
import com.example.bookkeeper.White

// Dados de exemplo para preencher a tela
data class DummyNotification(val id: Int, val title: String, val message: String, val time: String)

val sampleNotifications = listOf(
    DummyNotification(1, "Bem-vindo!", "Obrigado por usar o BookKeeper. Comece adicionando seu primeiro livro.", "Agora"),
    DummyNotification(2, "Dica de Leitura", "Que tal atualizar o progresso da sua leitura atual?", "Há 2h"),
    DummyNotification(3, "Meta Atingida", "Parabéns! Você leu 50 páginas essa semana.", "Ontem")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificações", fontFamily = FontFamily.Serif, color = SoftRose, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", tint = BabyPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { padding ->
        if (sampleNotifications.isEmpty()) {
            // Estado Vazio
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Notifications, null, modifier = Modifier.size(60.dp), tint = BabyPink.copy(alpha = 0.4f))
                    Spacer(Modifier.height(16.dp))
                    Text("Nenhuma notificação nova.", color = Color.Gray, fontFamily = FontFamily.Serif)
                }
            }
        } else {
            // Lista de Notificações
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleNotifications) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: DummyNotification) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightPinkBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(shape = CircleShape, color = BabyPink, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Notifications, null, tint = White, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(notification.title, fontWeight = FontWeight.Bold, color = DarkGrey, fontSize = 16.sp)
                    Text(notification.time, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(notification.message, color = DarkGrey, fontSize = 14.sp)
            }
        }
    }
}