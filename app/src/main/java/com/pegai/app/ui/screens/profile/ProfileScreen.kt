package com.pegai.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.R
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.home.HomeViewModel
import com.pegai.app.ui.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ProfileViewModel = viewModel()
) {
    val authUser by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // --- ESTADOS ---
    var showPixManagerDialog by remember { mutableStateOf(false) }
    var chavePix by remember { mutableStateOf("") }
    var tempChavePix by remember { mutableStateOf("") }

    // Gradiente Identidade Visual
    val mainGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A5C8A),
            Color(0xFF0E8FC6),
            Color(0xFF2ED1B2)
        )
    )

    LaunchedEffect(authUser) {
        viewModel.setUsuario(authUser)
    }

    if (uiState.user == null){
        GuestPlaceholder(
            title = "Acesse seu Perfil",
            subtitle = "Faça login para gerenciar seus dados.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState())
            ) {

                // HEADER ESTRUTURADO EM 3 COLUNAS ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    // Fundo Curvo
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                            .background(mainGradient)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Meu Perfil",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        // --- 3 COLUNAS ---
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // COLUNA 1: BOTÃO PIX (Esquerda)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        tempChavePix = chavePix
                                        showPixManagerDialog = true
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_qrcode_pix),
                                        contentDescription = "Pix",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Chave PIX",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // COLUNA 2: FOTO DE PERFIL COM BOTÃO EDITAR (Centro)
                            Column(
                                modifier = Modifier.weight(1.8f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier
                                            .size(110.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .padding(3.dp)
                                            .clip(CircleShape)
                                    ) {
                                        if (uiState.user?.fotoUrl != null && uiState.user?.fotoUrl!!.isNotEmpty()) {
                                            AsyncImage(
                                                model = uiState.user?.fotoUrl,
                                                contentDescription = "Foto",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0xFFE0E0E0)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = uiState.user?.nome?.first()?.toString() ?: "U",
                                                    fontSize = 40.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0E8FC6)
                                                )
                                            }
                                        }
                                    }

                                    // O BOTÃO DE EDITAR (LÁPIS)
                                    Surface(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .offset(x = (-4).dp, y = (-4).dp)
                                            .clickable {
                                                // TODO: Lógica para abrir galeria/câmera
                                            },
                                        shape = CircleShape,
                                        color = Color(0xFF0E8FC6),
                                        border = BorderStroke(2.dp, Color.White),
                                        shadowElevation = 4.dp
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar Foto",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // COLUNA 3: LOGOUT (Direita)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        authViewModel.logout()
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Sair",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nome e Email
                        Text(
                            text = uiState.user?.nome ?: "Usuário",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = uiState.user?.email ?: "",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }

                // STATUS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .offset(y = (-30).dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatusCard(label = "Avaliação", value = "4.9 ★")
                    StatusCard(label = "Aluguéis", value = "0")
                    StatusCard(label = "Anúncios", value = "0")
                }

                // MENU
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp)
                ) {
                    ProfileMenuItem(icon = Icons.Default.Person, text = "Meus Dados", onClick = { navController.navigate("meus_dados")})
                    ProfileMenuItem(icon = Icons.Default.Settings, text = "Configurações", onClick = { navController.navigate("configuracoes")})
                    ProfileMenuItem(icon = Icons.Default.Info, text = "Ajuda e Suporte", onClick = { navController.navigate("ajuda_suporte")})

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

            // DIÁLOGO INTELIGENTE DE GERENCIAMENTO PIX
            if (showPixManagerDialog) {
                Dialog(onDismissRequest = { showPixManagerDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Minha Chave Pix",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0E8FC6)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Edite sua chave abaixo para gerar o QR Code.",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = tempChavePix,
                                onValueChange = { tempChavePix = it },
                                label = { Text("Chave (CPF, Email, Aleatória)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0E8FC6),
                                    focusedLabelColor = Color(0xFF0E8FC6)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    chavePix = tempChavePix
                                    // Salvar backend aqui
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E8FC6)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Atualizar QR Code")
                            }

                            if (chavePix.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Seu QR Code atual:",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                AsyncImage(
                                    model = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${chavePix}",
                                    contentDescription = "QR Code Pix",
                                    modifier = Modifier
                                        .size(180.dp)
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = chavePix,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            TextButton(onClick = { showPixManagerDialog = false }) {
                                Text("Fechar Janela", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun StatusCard(label: String, value: String) {
    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0E8FC6))
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1F5FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0E8FC6)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF424242),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}