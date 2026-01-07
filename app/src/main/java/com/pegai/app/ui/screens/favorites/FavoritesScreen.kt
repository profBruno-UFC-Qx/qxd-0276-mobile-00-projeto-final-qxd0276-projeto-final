package com.pegai.app.ui.screens.favorites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.theme.PegaíTheme
import com.pegai.app.ui.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun FavoritesScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    // Front hoje, backend depois: substitui isso por um ViewModel que faz fetch da lista
    favorites: List<Product> = emptyList(),
    onRemoveFavorite: (Product) -> Unit = {} // backend depois: remover do banco e atualizar lista
) {
    val user by authViewModel.usuarioLogado.collectAsState()

    if (user == null) {
        GuestPlaceholder(
            title = "Acesse seus favoritos",
            subtitle = "Faça login para ver os itens que você favoritou.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
        return
    }

    FavoritesContent(
        user = user!!,
        favorites = favorites,
        onBack = { navController.popBackStack() },
        onExplore = {
            // volta pra Home (mais simples e funciona bem)
            navController.popBackStack()
        },
        onOpenProduct = { product ->
            navController.navigate("product_details/${product.pid}")
        },
        onRemoveFavorite = onRemoveFavorite
    )
}

@Composable
private fun FavoritesContent(
    user: User,
    favorites: List<Product>,
    onBack: () -> Unit,
    onExplore: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit
) {
    val azulLink = Color(0xFF2F80ED)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        //TopRightBlob(modifier = Modifier.align(Alignment.TopEnd))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 14.dp, bottom = 16.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            // Voltar
            Row(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = Color(0xFFEAF2FF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = azulLink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Voltar",
                    color = azulLink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Favoritos",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Itens que você salvou para ver depois.",
                fontSize = 14.sp,
                color = Color(0xFF6A6A6A)
            )

            Spacer(Modifier.height(18.dp))

            // Conteúdo
            if (favorites.isEmpty()) {
                EmptyFavoritesState(
                    onExplore = onExplore,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 110.dp) // pra não bater na bottom bar
                ) {
                    items(favorites, key = { it.pid }) { product ->
                        FavoriteProductCard(
                            product = product,
                            onClick = { onOpenProduct(product) },
                            onRemove = { onRemoveFavorite(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState(
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {

                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color(0xFF2F80ED),
                        modifier = Modifier.size(34.dp)
                    )



            Spacer(Modifier.height(14.dp))

            Text(
                text = "Você ainda não favoritou nenhum item",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Quando você favoritar, seus itens aparecem aqui.",
                fontSize = 13.sp,
                color = Color(0xFF6A6A6A)
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onExplore,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Explorar itens", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun FavoriteProductCard(
    product: Product,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val preco = remember(product.preco) {
        // mantém simples e “bonito”
        "R$ " + String.format(Locale("pt", "BR"), "%.2f", product.preco) + " / dia"
    }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F8F8))
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ImageNotSupported,
                            contentDescription = null,
                            tint = Color(0xFFB0B0B0),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Remover dos favoritos
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remover dos favoritos",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2F7DBF)
                ) {
                    Text(
                        text = preco,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 12.dp, bottom = 4.dp)
            ) {
                Text(
                    text = product.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

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
                        text = " (${product.totalAvaliacoes})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dono: ${product.donoNome}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TopRightBlob(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .offset(x = 80.dp, y = (-60).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFF3A5), Color(0xFFFFD68A))
                    )
                )
        )
        Box(
            modifier = Modifier
                .offset(x = 130.dp, y = (-20).dp)
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFF0A8), Color(0xFFFFD08A))
                    )
                )
        )
    }
}

/* =========================
   PREVIEW
   ========================= */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoritesPreviewEmpty() {
    PegaíTheme {
        FavoritesContent(
            user = User(uid = "1", nome = "Karine"),
            favorites = emptyList(),
            onBack = {},
            onExplore = {},
            onOpenProduct = {},
            onRemoveFavorite = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FavoritesPreviewFilled() {
    PegaíTheme {
        FavoritesContent(
            user = User(uid = "1", nome = "Karine"),
            favorites = listOf(
                Product(
                    pid = "1",
                    titulo = "Furadeira Bosch",
                    preco = 15.0,
                    categoria = "Ferramentas",
                    imageUrl = "",
                    donoNome = "Guilherme",
                    nota = 4.8,
                    totalAvaliacoes = 12
                ),
                Product(
                    pid = "2",
                    titulo = "Câmera Instax",
                    preco = 25.0,
                    categoria = "Eletrônicos",
                    imageUrl = "",
                    donoNome = "Ana",
                    nota = 4.6,
                    totalAvaliacoes = 8
                )
            ),
            onBack = {},
            onExplore = {},
            onOpenProduct = {},
            onRemoveFavorite = {}
        )
    }
}
