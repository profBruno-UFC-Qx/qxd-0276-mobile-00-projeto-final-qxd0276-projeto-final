package com.pegai.app.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.ui.navigation.Screen
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: HomeViewModel = viewModel()
) {
    // [MVVM] Estado Único
    val uiState by viewModel.uiState.collectAsState()
    val usuarioLogado by authViewModel.usuarioLogado.collectAsState()

    val context = LocalContext.current

    // --- LÓGICA DE PERMISSÃO DE GPS ---
    val launcherPermissao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissoes ->
        val concedido = permissoes[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissoes[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            viewModel.obterLocalizacaoAtual(context)
        }
    }

    LaunchedEffect(Unit) {
        val temPermissaoFina = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (temPermissaoFina) {
            viewModel.obterLocalizacaoAtual(context)
        } else {
            launcherPermissao.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // HEADER
        HomeHeader(
            user = usuarioLogado,
            localizacao = uiState.localizacaoAtual,
            onLoginClick = { navController.navigate(Screen.Login.route) },
            onFavoritesClick = { /* TODO rota favoritos */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // GRID PRINCIPAL
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Barra de Pesquisa
            item(span = { GridItemSpan(2) }) {
                SearchBar(
                    texto = uiState.textoPesquisa,
                    onTextoChange = { viewModel.atualizarPesquisa(it) }
                )
            }

            // Filtro de Categorias
            item(span = { GridItemSpan(2) }) {
                CategoryRow(
                    categorias = uiState.categorias,
                    selecionada = uiState.categoriaSelecionada,
                    onCategoriaClick = { viewModel.selecionarCategoria(it) }
                )
            }

            // Seção: Populares (Só aparece se tiver itens)
            if (uiState.produtosPopulares.isNotEmpty() && uiState.textoPesquisa.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Text(
                            text = "Populares",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 2.dp)
                        ) {
                            items(uiState.produtosPopulares) { produto ->
                                CompactProductCard(produto)
                            }
                        }
                    }
                }
            }

            // Seção: Lista Geral
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Para Você",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            items(uiState.produtos) { produto ->
                ProductCard(
                    product = produto,
                    onClick = {
                        // Exemplo de navegação com ID
                        // navController.navigate("product_details/${produto.id}")
                    }
                )
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun SearchBar(
    texto: String,
    onTextoChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 8.dp),
        shape = RoundedCornerShape(50),
        color = Color(0xFFFFFFFF),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (texto.isEmpty()) {
                    Text("O que você procura?", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                BasicTextField(
                    value = texto,
                    onValueChange = onTextoChange,
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
// COMPONENTES AUXILIARES
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Column(modifier = Modifier.padding(8.dp)) {
            // Imagem e Preço
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F8F8))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { /* TODO: Favoritar */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2F7DBF) // Roxo
                ) {
                    Text(
                        text = "R$ ${product.preco} / dia",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Textos
            Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 12.dp, bottom = 4.dp)) {
                Text(
                    text = product.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Nota",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.nota.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = " (201)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dono: ${product.dono}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CompactProductCard(product: Product) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F8F8))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color (0xFF2F7DBF)
                ) {
                    Text(
                        text = "R$ ${product.preco}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.titulo,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = product.nota.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun HomeHeader(
    user: User?,
    localizacao: String,
    onLoginClick: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    // Gradiente do Botão "Entrar"
    val blueGreenGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A5C8A), // Azul escuro
            Color(0xFF0E8FC6), // Azul médio
            Color(0xFF2ED1B2)  // Verde água
        ),
        start = Offset(0f, 0f),
        end = Offset(300f, 100f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Saudação e Localização
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.fotoUrl != "") {
                        AsyncImage(
                            model = user.fotoUrl,
                            contentDescription = "Foto de Perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = user.nome.first().toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Olá, ${user.nome.split(" ").first()} \uD83D\uDC4B",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = localizacao,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Visitante
                Column {
                    Text(
                        text = "Bem-vindo ao Pegaí",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = localizacao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Botões de Ação
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                IconButton(
                    onClick = onFavoritesClick,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritos",
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = { /* TODO: Notificações */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificações",
                        tint = Color.Black
                    )
                }
            } else {
                // BOTÃO COM GRADIENTE
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(blueGreenGradient)
                ) {
                    Button(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Entrar")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    val textoPesquisa by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(50),
        color = Color(0xFFFFFFFF),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (textoPesquisa.isEmpty()) {
                    Text(
                        text = "O que você procura?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                BasicTextField(
                    value = textoPesquisa,
                    onValueChange = { /* TODO: Atualizar estado */ },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CategoryRow(
    categorias: List<String>,
    selecionada: String,
    onCategoriaClick: (String) -> Unit
) {
    val mainColor = Color(0xFF0E8FC6)

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        items(categorias) { categoria ->
            val isSelected = categoria == selecionada

            FilterChip(
                selected = isSelected,
                onClick = { onCategoriaClick(categoria) },
                label = {
                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier.height(32.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = mainColor,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFFFFFFF),
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) mainColor else Color(0xFFEEEEEE),
                    borderWidth = 1.dp
                ),
                shape = RoundedCornerShape(50)
            )
        }
    }
}