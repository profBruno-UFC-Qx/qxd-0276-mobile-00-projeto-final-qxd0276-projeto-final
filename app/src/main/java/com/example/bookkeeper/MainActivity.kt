package com.example.bookkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp // <--- IMPORT CORRIGIDO AQUI
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.screens.AddBookScreen
import com.example.bookkeeper.ui.theme.screens.BookDetailScreen
import com.example.bookkeeper.ui.theme.screens.LoadingScreen
import com.example.bookkeeper.ui.theme.screens.LoginScreen
import com.example.bookkeeper.ui.theme.screens.ProfileScreen
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            BookKeeperTheme(darkTheme = isDarkTheme) {
                val currentUser by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                var currentScreen by remember { mutableStateOf("library") }
                var selectedBookId by remember { mutableStateOf<Int?>(null) }

                if (isLoading) {
                    LoadingScreen()
                } else if (currentUser == null) {
                    LoginScreen(viewModel = viewModel)
                    currentScreen = "library"
                } else {
                    when (currentScreen) {
                        "library" -> {
                            LibraryScreen(
                                viewModel = viewModel,
                                onProfileClick = { currentScreen = "profile" },
                                onAddBookClick = { currentScreen = "add_book" },
                                onBookClick = { book ->
                                    selectedBookId = book.id
                                    currentScreen = "book_detail"
                                }
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
                            AddBookScreen(
                                viewModel = viewModel,
                                onBackClick = { currentScreen = "library" },
                                onSaveSuccess = { currentScreen = "library" }
                            )
                            BackHandler { currentScreen = "library" }
                        }
                        "book_detail" -> {
                            if (selectedBookId != null) {
                                BookDetailScreen(
                                    viewModel = viewModel,
                                    bookId = selectedBookId!!,
                                    onBackClick = { currentScreen = "library" }
                                )
                                BackHandler { currentScreen = "library" }
                            } else {
                                currentScreen = "library"
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: BookViewModel,
    onProfileClick: () -> Unit,
    onAddBookClick: () -> Unit,
    onBookClick: (Book) -> Unit
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
                        Icon(Icons.Rounded.AccountCircle, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.secondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.secondary, actionIconContentColor = MaterialTheme.colorScheme.secondary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (bookList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Sua estante está vazia.", fontFamily = FontFamily.Serif, color = MaterialTheme.colorScheme.onBackground)
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
                    BookCard(book, onClick = { onBookClick(book) })
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(220.dp)
            .fillMaxWidth()
            .clickable { onClick() },
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

                val backgroundColor = if (book.coverColorHex != null) {
                    Color(book.coverColorHex)
                } else {
                    Color.LightGray
                }
                Box(modifier = Modifier.fillMaxSize().background(backgroundColor))
            }
            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(book.author, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.9f), textAlign = TextAlign.Center)

                if (book.totalPages > 0 && book.currentPage > 0) {
                    val percent = ((book.currentPage.toFloat() / book.totalPages.toFloat()) * 100).toInt()
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = GoldAccent, shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "$percent%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}