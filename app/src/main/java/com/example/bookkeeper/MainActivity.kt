package com.example.bookkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.screens.AddBookScreen // Importante!
import com.example.bookkeeper.ui.theme.screens.LoginScreen
import com.example.bookkeeper.ui.theme.screens.ProfileScreen
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookKeeperTheme {
                val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
                val currentUser by viewModel.currentUser.collectAsState()

                // Estado da tela atual
                var currentScreen by remember { mutableStateOf("library") }

                if (currentUser == null) {
                    LoginScreen(viewModel = viewModel)
                    currentScreen = "library"
                } else {
                    when (currentScreen) {
                        "library" -> {
                            LibraryScreen(
                                viewModel = viewModel,
                                onProfileClick = { currentScreen = "profile" },
                                onAddBookClick = { currentScreen = "add_book" } // <--- MUDANÇA DE TELA
                            )
                        }
                        "profile" -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                onLogoutClick = { },
                                onBackClick = { currentScreen = "library" }
                            )
                            BackHandler { currentScreen = "library" }
                        }
                        "add_book" -> {
                            // AQUI CHAMA A TELA NOVA
                            AddBookScreen(
                                viewModel = viewModel,
                                onBackClick = { currentScreen = "library" },
                                onSaveSuccess = { currentScreen = "library" }
                            )
                            BackHandler { currentScreen = "library" }
                        }
                    }
                }
            }
        }
    }
}

// ... LibraryScreen e BookCard abaixo (pode manter como estavam se não mudou nada neles) ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: BookViewModel,
    onProfileClick: () -> Unit,
    onAddBookClick: () -> Unit
) {
    val bookList by viewModel.books.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Minha Estante", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Text(
                            "Leitor: ${currentUser?.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldAccent
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = "Perfil", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.secondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.secondary, actionIconContentColor = MaterialTheme.colorScheme.secondary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick, // <--- AÇÃO CORRETA AQUI
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (bookList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Sua estante está vazia.", fontFamily = FontFamily.Serif)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(bookList) { book ->
                    BookCard(book) // Certifique-se de que o BookCard suporta imagem!
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Card(
        modifier = Modifier.height(220.dp).fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, topStart = 2.dp, bottomStart = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (book.coverUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(book.coverUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(book.coverColorHex)))
            }
            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(book.author, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.9f), textAlign = TextAlign.Center)
            }
        }
    }
}