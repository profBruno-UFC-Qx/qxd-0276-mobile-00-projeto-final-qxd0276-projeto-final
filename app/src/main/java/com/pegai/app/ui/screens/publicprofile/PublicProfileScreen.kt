package com.pegai.app.ui.screens.publicprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.ui.viewmodel.publicprofile.ProdutoMock
import com.pegai.app.ui.viewmodel.publicprofile.PublicProfileViewModel
import com.pegai.app.ui.viewmodel.publicprofile.ReviewMock

@Composable
fun PublicProfileScreen(
    navController: NavController,
    userId: String,
    viewModel: PublicProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.carregarPerfil(userId)
    }

    // --- DADOS VINDOS DO ESTADO (MVVM PURO) ---
    val user = uiState.user
    val nomeUsuario = user?.nome ?: "Carregando..."
    val fotoUrl = if (!user?.fotoUrl.isNullOrEmpty()) user!!.fotoUrl else "https://via.placeholder.com/150"

    // Dados fixos de visual (podem vir do banco depois)
    val notaGeral = uiState.nota
    val totalAvaliacoes = uiState.totalAvaliacao

    // Lógica inteligente: Se tiver produtos reais, mostra eles. Se não, mostra sugestões (mock)
    val listaProdutosExibicao = if (uiState.produtos.isNotEmpty()) {
        uiState.produtos.map { ProdutoMock(it.titulo, "R$ ${it.preco}", it.imageUrl) }
    } else {
        uiState.produtosSugeridos // Vem do ViewModel
    }

    val comentarios = uiState.reviews // Vem do ViewModel

    val mainGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0A5C8A), Color(0xFF0E8FC6), Color(0xFF2ED1B2))
    )

    Scaffold(
        bottomBar = {
            Button(
                onClick = { navController.navigate("chat_detail/novo_chat_123") },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E8FC6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Mensagem", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0E8FC6))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState())
            ) {
                // === HEADER ===
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)).background(mainGradient))

                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.statusBarsPadding().padding(start = 8.dp).align(Alignment.TopStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White).padding(3.dp).clip(CircleShape)) {
                            AsyncImage(model = fotoUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(nomeUsuario, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.CheckCircle, "Verificado", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$notaGeral", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                            Text(" ($totalAvaliacoes avaliações)", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === SEÇÃO 1: ANÚNCIOS ===
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Anúncios de $nomeUsuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listaProdutosExibicao) { produto ->
                            MiniProductCard(produto)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // === SEÇÃO 2: AVALIAÇÕES ===
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("O que dizem sobre ele(a)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    comentarios.forEach { review ->
                        ReviewItem(review)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun ReviewItem(review: ReviewMock) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
                    Text(if (review.nome.isNotEmpty()) review.nome.first().toString() else "?", fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(review.nome, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(review.data, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    repeat(5) { index ->
                        Icon(Icons.Default.Star, null, tint = if (index < review.nota) Color(0xFFFFB300) else Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comentario, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5A5A5A))
        }
    }
}

@Composable
fun MiniProductCard(produto: ProdutoMock) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = produto.imagem, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(90.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(produto.nome, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(produto.preco, color = Color(0xFF0E8FC6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}