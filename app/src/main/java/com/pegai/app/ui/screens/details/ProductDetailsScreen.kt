package com.pegai.app.ui.screens.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.details.ProductDetailsViewModel
import com.pegai.app.ui.viewmodel.details.ReviewUI

@Composable
fun ProductDetailsScreen(
    navController: NavController,
    productId: String?,
    authViewModel: AuthViewModel,
    viewModel: ProductDetailsViewModel = viewModel()
) {
    val user by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.carregarDetalhes(productId)
    }

    var currentImageIndex by remember { mutableStateOf(0) }
    var showRentalConfirmationDialog by remember { mutableStateOf(false) }
    var showLoginRequiredDialog by remember { mutableStateOf(false) }

    val mainColor = Color(0xFF0E8FC6)
    val bottomBarGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0A5C8A), Color(0xFF0E8FC6), Color(0xFF2ED1B2))
    )

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mainColor)
        }
        return
    }

    val product = uiState.produto ?: return
    val imagens = uiState.imagensCarrossel
    val reviewsList = uiState.reviews

    Scaffold(
        bottomBar = {
            BottomRentBar(
                price = "R$ ${String.format("%.2f", product.preco)} / dia",
                onRentClick = {
                    if (user != null) showRentalConfirmationDialog = true
                    else showLoginRequiredDialog = true
                },
                gradient = bottomBarGradient
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // === CARROSSEL ===
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).background(Color(0xFFF0F0F0))) {
                    if (imagens.isNotEmpty()) {
                        AsyncImage(
                            model = imagens[currentImageIndex],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (imagens.size > 1) {
                    if (currentImageIndex > 0) {
                        Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)) {
                            IconButton(onClick = { currentImageIndex-- }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(40.dp)) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.White)
                            }
                        }
                    }
                    if (currentImageIndex < imagens.size - 1) {
                        Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)) {
                            IconButton(onClick = { currentImageIndex++ }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(40.dp)) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
                            }
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp).background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp)).padding(8.dp, 4.dp)) {
                        Text("${currentImageIndex + 1}/${imagens.size}", color = Color.White, fontSize = 12.sp)
                    }
                }

                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(start = 12.dp).offset(y = (-25).dp).background(Color.White.copy(alpha = 0.9f), CircleShape).size(50.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
                }

                Row(modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(end = 12.dp).offset(y = (-25).dp)) {
                    IconButton(onClick = { /* Share */ }, modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape).size(50.dp)) {
                        Icon(Icons.Default.Share, null, tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { /* Favorite */ }, modifier = Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape).size(50.dp)) {
                        Icon(Icons.Default.FavoriteBorder, null, tint = Color.Black)
                    }
                }
            }

            // === CONTEÚDO ===
            Column(modifier = Modifier.padding(24.dp)) {
                Surface(color = mainColor.copy(alpha = 0.1f), shape = RoundedCornerShape(50)) {
                    Text(product.categoria, modifier = Modifier.padding(12.dp, 6.dp), style = MaterialTheme.typography.labelMedium, color = mainColor, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(product.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                    Text(" ${product.nota} ", fontWeight = FontWeight.SemiBold)
                    Text("• ${uiState.avaliacoesCount} Avaliações", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(24.dp))

                // CARD DONO
                Text("Anunciado por", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.PublicProfile.createRoute(product.donoId))
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Text(uiState.nomeDono.first().toString(), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(uiState.nomeDono, fontWeight = FontWeight.Bold)
                            Text("Ver perfil", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Sobre o produto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.descricao, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5A5A5A), lineHeight = 24.sp)

                Spacer(modifier = Modifier.height(32.dp))
                Text("Avaliações do Item", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                reviewsList.forEach { review ->
                    ReviewProdutoItem(review)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showRentalConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showRentalConfirmationDialog = false },
                title = { Text(text = "Solicitar Aluguel?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Você deseja enviar uma solicitação de aluguel para ${uiState.nomeDono}? O chat será aberto para negociação.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRentalConfirmationDialog = false
                            // TODO: Navegar para o chat no futuro
                            // navController.navigate("chat_detail/novo")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(bottomBarGradient)
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sim, solicitar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRentalConfirmationDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
            )
        }

        if (showLoginRequiredDialog) {
            AlertDialog(
                onDismissRequest = { showLoginRequiredDialog = false },
                title = { Text("Login Necessário", fontWeight = FontWeight.Bold) },
                text = { Text("Para alugar este produto, você precisa entrar na sua conta.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLoginRequiredDialog = false
                            navController.navigate(Screen.Login.route)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(bottomBarGradient)
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Fazer Login", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLoginRequiredDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

// COMPONENTES AUXILIARES
@Composable
fun ReviewProdutoItem(review: ReviewUI) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(review.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        repeat(5) { index ->
                            Icon(Icons.Default.Star, null, tint = if (index < review.rating) Color(0xFFFFB300) else Color(0xFFE0E0E0), modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(review.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5A5A5A))
        }
    }
}

@Composable
fun BottomRentBar(price: String, onRentClick: () -> Unit, gradient: Brush) {
    Surface(shadowElevation = 20.dp, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
        Box(modifier = Modifier.fillMaxWidth().background(gradient).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Valor: ", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(price, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                }
                Button(
                    onClick = onRentClick, colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp), modifier = Modifier.height(40.dp).width(140.dp)
                ) {
                    Text("Alugar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0E8FC6))
                }
            }
        }
    }
}