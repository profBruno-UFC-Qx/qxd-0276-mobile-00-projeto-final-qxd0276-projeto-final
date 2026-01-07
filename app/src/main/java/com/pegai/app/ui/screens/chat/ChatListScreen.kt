package com.pegai.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.viewmodel.AuthViewModel

// Modelo de dados simples para a lista (DTO)
data class ChatSummary(
    val chatId: String,
    val otherUserName: String,
    val productName: String,
    val lastMessage: String,
    val time: String,
    val isMeOwner: Boolean
)

@Composable
fun ChatListScreen(
    navController: NavController,
    authViewModel: AuthViewModel // Mudamos para receber o ViewModel
) {
    // 1. Observa o usuário
    val user by authViewModel.usuarioLogado.collectAsState()

    // 2. Proteção de Login
    if (user == null) {
        GuestPlaceholder(
            title = "Suas Conversas",
            subtitle = "Faça login para negociar aluguéis e ver seu histórico de mensagens.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        // --- CONTEÚDO REAL (Só aparece se logado) ---

        // Cores para identificar o tipo de relação
        val corSouDono = Color(0xFFE3F2FD) // Azulzinho
        val corSouLocatario = Color(0xFFE8F5E9) // Verdinho

        // --- MOCK DE DADOS ---
        val chats = listOf(
            ChatSummary("1", "Maria (Dona)", "Calculadora HP 12c", "Tudo bem, pode pegar amanhã.", "10:30", false),
            ChatSummary("2", "João (Interessado)", "Livro Cálculo I", "Ainda está disponível?", "Ontem", true),
            ChatSummary("3", "Ana (Interessada)", "Jaleco G", "Aceito sua oferta.", "Segunda", true)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Text(
                text = "Minhas Conversas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF5A5A5A)
            )

            LazyColumn {
                items(chats) { chat ->
                    ConversationItem(
                        chat = chat,
                        backgroundColor = if (chat.isMeOwner) corSouDono else corSouLocatario,
                        badgeText = if (chat.isMeOwner) "Meu Produto" else "Aluguel",
                        badgeColor = if (chat.isMeOwner) Color(0xFF2196F3) else Color(0xFF4CAF50),
                        onClick = {
                            navController.navigate("chat_detail/${chat.chatId}")
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    chat: ChatSummary,
    backgroundColor: Color,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chat.otherUserName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = chat.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = chat.productName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }

            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}