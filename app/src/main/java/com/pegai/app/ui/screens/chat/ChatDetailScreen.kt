package com.pegai.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

// =========================================================
// O ENUM DEVE FICAR AQUI (FORA DA FUNÇÃO COMPOSABLE)
// =========================================================
enum class RentalStatus {
    PENDING,  // Aguardando confirmação
    ACTIVE,   // Aceito / Em curso
    REJECTED  // Recusado
}

@Composable
fun ChatDetailScreen(
    navController: NavController,
    chatId: String?,
    initialStatus: String? = "pending"
) {
    // --- SIMULAÇÃO: Mude para 'true' se quiser testar como DONO ---
    val isCurrentUserOwner = true

    // Estado do aluguel
    var status by remember {
        mutableStateOf(
            when(initialStatus) {
                "active" -> RentalStatus.ACTIVE
                "rejected" -> RentalStatus.REJECTED
                else -> RentalStatus.PENDING
            }
        )
    }

    // Simulação de datas (null = ainda não definido)
    var rentalDates by remember { mutableStateOf<String?>(null) }
    var rentalTotalValue by remember { mutableStateOf<String?>(null) }

    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<MessageMock>() }
    val bgGray = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            ChatHeader(
                navController = navController,
                userName = "Edineide",
                userPhoto = "https://media-for2-2.cdn.whatsapp.net/v/t61.24694-24/537374086_697212073422555_5417296598778872192_n.jpg?ccb=11-4&oh=01_Q5Aa3QGcBA_ygZJ126lj2P2rq-cNZ3waSvCV6g22Yi0fobCGvQ&oe=694A4394&_nc_sid=5e03e0&_nc_cat=105",
                productName = "Calculadora HP 12c",
                onProfileClick = {
                    // Navega para o perfil público (usando um ID fictício)
                    navController.navigate("public_profile/owner_123")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgGray)
        ) {

            // 1. BANNER DE STATUS
            StatusActionBanner(
                status = status,
                isOwner = isCurrentUserOwner,
                rentalDates = rentalDates,
                rentalTotalValue = rentalTotalValue,
                onAccept = {
                    status = RentalStatus.ACTIVE
                    messages.add(MessageMock("Solicitação aceita! O chat foi liberado.", true))
                },
                onReject = { status = RentalStatus.REJECTED },
                onSetDates = {
                    rentalDates = "15/10 a 20/10"
                    rentalTotalValue = "R$ 75,00"
                    messages.add(MessageMock("Datas definidas: 15/10 a 20/10. Valor final: R$ 75,00", true))
                }
            )

            // 2. LISTA DE MENSAGENS
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    MessageItem(msg)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 3. BARRA DE DIGITAÇÃO
            if (status == RentalStatus.ACTIVE) {
                MessageInputBar(
                    text = messageText,
                    onTextChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank()) {
                            messages.add(MessageMock(messageText, true))
                            messageText = ""
                        }
                    }
                )
            } else {
                Surface(color = Color.White, tonalElevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (status == RentalStatus.PENDING) "Aguardando confirmação do locador" else "Chat encerrado",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun StatusActionBanner(
    status: RentalStatus,
    isOwner: Boolean,
    rentalDates: String?,
    rentalTotalValue: String?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onSetDates: () -> Unit
) {
    val backgroundColor = when (status) {
        RentalStatus.PENDING -> Color(0xFFFFF3E0)
        RentalStatus.ACTIVE -> Color(0xFFE8F5E9)
        RentalStatus.REJECTED -> Color(0xFFFFEBEE)
    }
    val contentColor = when (status) {
        RentalStatus.PENDING -> Color(0xFFF57C00)
        RentalStatus.ACTIVE -> Color(0xFF2E7D32)
        RentalStatus.REJECTED -> Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if(status == RentalStatus.ACTIVE) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when(status) {
                        RentalStatus.PENDING -> "Solicitação Enviada"
                        RentalStatus.ACTIVE -> "Aluguel Aceito!"
                        RentalStatus.REJECTED -> "Recusado"
                    },
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }

            if (status == RentalStatus.ACTIVE && rentalDates != null) {
                Divider(color = contentColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Período", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(rentalDates, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Valor Total", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(rentalTotalValue ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = contentColor)
                    }
                }
            } else if (status == RentalStatus.PENDING && !isOwner) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Aguarde o locador aceitar o pedido para liberar o chat.", fontSize = 14.sp, color = Color.DarkGray)
            }

            if (isOwner) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (status == RentalStatus.PENDING) {
                        OutlinedButton(onClick = onReject, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Text("Recusar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) { Text("Aceitar") }
                    } else if (status == RentalStatus.ACTIVE && rentalDates == null) {
                        Button(
                            onClick = onSetDates,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E8FC6))
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Definir Datas")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatHeader(
    navController: NavController,
    userName: String,
    userPhoto: String,
    productName: String,
    onProfileClick: () -> Unit // NOVO PARÂMETRO
) {
    Surface(shadowElevation = 4.dp, color = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            AsyncImage(
                model = userPhoto,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Weight 1f para empurrar o botão para a direita
            Column(modifier = Modifier.weight(1f)) {
                Text(text = userName, fontWeight = FontWeight.Bold)
                Text(text = "Sobre: $productName", style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1)
            }

            // BOTÃO VER PERFIL (NOVO)
            TextButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver Perfil", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MessageInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(modifier = Modifier.padding(8.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Digite...") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(onClick = onSend) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color(0xFF0E8FC6))
            }
        }
    }
}

@Composable
fun MessageItem(msg: MessageMock) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (msg.isMe) Color(0xFF0E8FC6) else Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 1.dp
        ) {
            Text(
                text = msg.text,
                color = if (msg.isMe) Color.White else Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

data class MessageMock(val text: String, val isMe: Boolean = false)