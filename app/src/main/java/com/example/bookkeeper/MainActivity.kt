package com.example.bookkeeper

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.ui.theme.screens.AddBookScreen
import com.example.bookkeeper.ui.theme.screens.BookDetailScreen
import com.example.bookkeeper.ui.theme.screens.LoadingScreen
import com.example.bookkeeper.ui.theme.screens.LoginScreen
import com.example.bookkeeper.ui.theme.screens.NotificationScreen // --- IMPORT NOVO ---
import com.example.bookkeeper.ui.theme.screens.ProfileScreen
import com.example.bookkeeper.ui.theme.screens.ReadingSessionScreen
import com.example.bookkeeper.viewmodel.BookViewModel
import kotlinx.coroutines.launch

// --- PALETA DE CORES GLOBAL (ROSA/BRANCO) ---
val BabyPink = Color(0xFFFFB6C1)       // Rosa Bebê (Botões/Destaques)
val LightPinkBg = Color(0xFFFFF0F5)    // Rosa muito clarinho (Fundo de cards)
val DarkGrey = Color(0xFF4A4A4A)       // Texto Principal
val SoftRose = Color(0xFFD81B60)       // Ícones/Texto de destaque
val White = Color(0xFFFFFFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            // Permissão de Notificação (Android 13+)
            val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            BookKeeperTheme(darkTheme = isDarkTheme) {
                val currentUser by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                var currentScreen by remember { mutableStateOf("library") }
                var selectedBookId by remember { mutableStateOf<Int?>(null) }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                if (isLoading) {
                    LoadingScreen()
                } else if (currentUser == null) {
                    LoginScreen(viewModel = viewModel)
                    currentScreen = "library"
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(modifier = Modifier.width(300.dp), drawerContainerColor = White) {
                                // CABEÇALHO DO DRAWER
                                Column(modifier = Modifier.fillMaxWidth().background(LightPinkBg).padding(24.dp)) {
                                    Surface(shape = CircleShape, color = White, modifier = Modifier.size(64.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.AccountCircle, null, modifier = Modifier.size(40.dp), tint = BabyPink) }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(currentUser?.name ?: "Leitor", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SoftRose, fontFamily = FontFamily.Serif)
                                    Text(currentUser?.email ?: "", fontSize = 14.sp, color = DarkGrey)
                                }
                                Spacer(Modifier.height(12.dp))

                                // ITENS DO MENU
                                NavigationDrawerItem(label = { Text("Minha Estante", color = DarkGrey) }, icon = { Icon(Icons.Rounded.Book, null, tint = SoftRose) }, selected = currentScreen == "library", onClick = { currentScreen = "library"; scope.launch { drawerState.close() } }, modifier = Modifier.padding(horizontal = 12.dp))
                                NavigationDrawerItem(label = { Text("Sessão de Leitura", color = DarkGrey) }, icon = { Icon(Icons.Rounded.Timer, null, tint = SoftRose) }, selected = currentScreen == "reading_session", onClick = { currentScreen = "reading_session"; scope.launch { drawerState.close() } }, modifier = Modifier.padding(horizontal = 12.dp))
                                NavigationDrawerItem(label = { Text("Meu Perfil", color = DarkGrey) }, icon = { Icon(Icons.Rounded.Person, null, tint = SoftRose) }, selected = currentScreen == "profile", onClick = { currentScreen = "profile"; scope.launch { drawerState.close() } }, modifier = Modifier.padding(horizontal = 12.dp))

                                Spacer(Modifier.weight(1f)); HorizontalDivider(color = BabyPink.copy(alpha = 0.5f))
                                NavigationDrawerItem(label = { Text("Sair") }, icon = { Icon(Icons.Rounded.Logout, null) }, selected = false, onClick = { scope.launch { drawerState.close() }; viewModel.logout() }, modifier = Modifier.padding(12.dp), colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = SoftRose, unselectedTextColor = SoftRose))
                            }
                        }
                    ) {
                        // NAVEGAÇÃO PRINCIPAL
                        when (currentScreen) {
                            "library" -> LibraryScreen(
                                viewModel = viewModel,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onAddBookClick = { currentScreen = "add_book" },
                                onBookClick = { book -> selectedBookId = book.id; currentScreen = "book_detail" },
                                // --- AQUI ESTÁ A LIGAÇÃO DO CLIQUE ---
                                onNotificationClick = { currentScreen = "notifications" }
                            )

                            // --- AQUI ESTÁ A NOVA ROTA ---
                            "notifications" -> {
                                NotificationScreen(onBackClick = { currentScreen = "library" })
                                BackHandler { currentScreen = "library" }
                            }
                            // -----------------------------

                            "reading_session" -> ReadingSessionScreen(
                                viewModel = viewModel,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                            "profile" -> {
                                ProfileScreen(viewModel, { viewModel.logout() }, { currentScreen = "library" })
                                BackHandler { currentScreen = "library" }
                            }
                            "add_book" -> {
                                AddBookScreen(viewModel, { currentScreen = "library" }, { currentScreen = "library" })
                                BackHandler { currentScreen = "library" }
                            }
                            "book_detail" -> {
                                if (selectedBookId != null) {
                                    BookDetailScreen(viewModel, selectedBookId!!, { currentScreen = "library" })
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
}

// TELA DA BIBLIOTECA (LIBRARY SCREEN)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: BookViewModel,
    onMenuClick: () -> Unit,
    onAddBookClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onNotificationClick: () -> Unit
) {
    val bookList by viewModel.books.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val totalPages by viewModel.totalPagesRead.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos") }

    val filteredBooks = bookList.filter { book ->
        val matchesSearch = book.title.contains(searchQuery, ignoreCase = true) || book.author.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatus == "Todos") true else book.status == selectedStatus
        matchesSearch && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Minha Estante", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = SoftRose)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = BabyPink.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) { Text("$totalPages págs. lidas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftRose, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                            if (currentUser != null) Text(" • ${currentUser?.name}", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, "Menu", tint = BabyPink) } },
                actions = { IconButton(onClick = onNotificationClick) { Icon(Icons.Rounded.Notifications, "Notificações", tint = SoftRose) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAddBookClick, containerColor = BabyPink, contentColor = White, shape = CircleShape) { Icon(Icons.Default.Add, null) } },
        containerColor = White
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Busca e Filtros
            Column(modifier = Modifier.background(White).padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar título ou autor...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BabyPink, unfocusedBorderColor = Color.LightGray, focusedLabelColor = BabyPink, cursorColor = SoftRose)
                )
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    listOf("Todos", "Lendo", "Quero Ler", "Lido").forEach { filter ->
                        FilterChip(
                            selected = selectedStatus == filter, onClick = { selectedStatus = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BabyPink, selectedLabelColor = White, containerColor = LightPinkBg, labelColor = DarkGrey),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedStatus == filter, borderColor = if (selectedStatus == filter) BabyPink else Color.Transparent)
                        )
                    }
                }
            }
            // Grid de Livros
            if (filteredBooks.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Book, null, Modifier.size(60.dp), tint = BabyPink.copy(0.4f))
                        Text(if (searchQuery.isNotEmpty()) "Nenhum livro encontrado." else "Sua estante está vazia.", fontFamily = FontFamily.Serif, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredBooks) { book -> BookCard(book) { onBookClick(book) } }
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier.height(260.dp).fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = LightPinkBg), shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(0.75f).fillMaxWidth()) {
                if (book.coverUrl != null) Image(painter = rememberAsyncImagePainter(book.coverUrl), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else Box(Modifier.fillMaxSize().background(BabyPink), Alignment.Center) { Icon(Icons.Rounded.Book, null, tint = White, modifier = Modifier.size(40.dp)) }
                Box(Modifier.padding(8.dp).align(Alignment.TopEnd)) {
                    Surface(color = White.copy(0.9f), shape = RoundedCornerShape(4.dp)) { Text(book.status.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SoftRose, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) }
                }
            }
            Column(Modifier.weight(0.25f).fillMaxWidth().padding(8.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                Text(book.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = DarkGrey, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center, maxLines = 1)
                Text(book.author, style = MaterialTheme.typography.labelSmall, color = Color.Gray, textAlign = TextAlign.Center, maxLines = 1)
                if (book.totalPages > 0 && book.currentPage > 0) {
                    val percent = ((book.currentPage.toFloat() / book.totalPages.toFloat()) * 100).toInt()
                    LinearProgressIndicator(progress = { percent / 100f }, modifier = Modifier.fillMaxWidth().height(4.dp).padding(top = 4.dp).clip(RoundedCornerShape(2.dp)), color = BabyPink, trackColor = White)
                }
            }
        }
    }
}