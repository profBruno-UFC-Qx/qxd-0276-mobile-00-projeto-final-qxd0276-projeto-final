package com.example.bookkeeper.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Notifications // Import do ícone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BookViewModel,
    onBookClick: (Book) -> Unit,
    onAddBookClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit = {} // Ação opcional para notificações
) {
    val books by viewModel.books.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    // Observa o total de páginas lidas (calculado no ViewModel)
    val totalPages by viewModel.totalPagesRead.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Minha pica",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723) // Marrom Café
                        )
                        // Subtítulo com Total de Páginas + Nome do Usuário
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$totalPages págs. lidas",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                            if (user != null) {
                                Text(
                                    text = " • ${user?.name}",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color(0xFF5D4037).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Ícone de Notificações
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notificações",
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFF3E2723)
                        )
                    }
                    // Ícone de Perfil
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Perfil",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF3E2723)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5DC) // Cor Creme/Bege (Papel)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = Color(0xFF3E2723), // Marrom escuro
                contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Adicionar Livro")
            }
        },
        containerColor = Color(0xFFEFEBE9) // Fundo levemente acinzentado/papel
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Book,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sua estante está vazia.",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Adicione seu primeiro livro!",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(books) { book ->
                        BookItem(book = book, onClick = { onBookClick(book) })
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder da Capa (se book.coverUrl não existir)
            Box(
                modifier = Modifier
                    .size(50.dp, 70.dp)
                    .background(Color(0xFF5D4037), shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Black
                )
                Text(
                    text = book.author,
                    fontSize = 14.sp,
                    color = Color(0xFF5D4037),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = book.status,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}