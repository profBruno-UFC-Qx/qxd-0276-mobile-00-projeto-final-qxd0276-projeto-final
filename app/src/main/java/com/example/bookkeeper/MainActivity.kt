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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.ui.theme.screens.AddBookScreen
import com.example.bookkeeper.ui.theme.screens.BookDetailScreen
import com.example.bookkeeper.ui.theme.screens.LoadingScreen
import com.example.bookkeeper.ui.theme.screens.LoginScreen
import com.example.bookkeeper.ui.theme.screens.ProfileScreen
// REMOVI O IMPORT DO STATSSCREEN QUE DAVA ERRO
import com.example.bookkeeper.viewmodel.BookViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            // Permissão de Notificação
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { }
            )
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
                            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(24.dp)) {
                                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(64.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.AccountCircle, null, modifier = Modifier.size(40.dp)) }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(currentUser?.name ?: "Leitor", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GoldAccent, fontFamily = FontFamily.Serif)
                                    Text(currentUser?.email ?: "", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                                }
                                Spacer(Modifier.height(12.dp))

                                NavigationDrawerItem(label = { Text("Minha Estante") }, icon = { Icon(Icons.Rounded.Book, null) }, selected = currentScreen == "library", onClick = { currentScreen = "library"; scope.launch { drawerState.close() } }, modifier = Modifier.padding(horizontal = 12.dp))
                                NavigationDrawerItem(label = { Text("Meu Perfil") }, icon = { Icon(Icons.Rounded.Person, null) }, selected = currentScreen == "profile", onClick = { currentScreen = "profile"; scope.launch { drawerState.close() } }, modifier = Modifier.padding(horizontal = 12.dp))
                                // REMOVI O BOTÃO DE ESTATÍSTICAS AQUI
                                NavigationDrawerItem(label = { Text("Sobre o App") }, icon = { Icon(Icons.Rounded.Info, null) }, selected = false, onClick = { scope.launch { drawerState.close() } }, modifier = Modifier.padding(horizontal = 12.dp))

                                Spacer(Modifier.weight(1f)); HorizontalDivider()
                                NavigationDrawerItem(label = { Text("Sair") }, icon = { Icon(Icons.Rounded.Logout, null) }, selected = false, onClick = { scope.launch { drawerState.close() }; viewModel.logout() }, modifier = Modifier.padding(12.dp), colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = MaterialTheme.colorScheme.error, unselectedTextColor = MaterialTheme.colorScheme.error))
                            }
                        }
                    ) {
                        when (currentScreen) {
                            "library" -> LibraryScreen(viewModel, { scope.launch { drawerState.open() } }, { currentScreen = "add_book" }, { book -> selectedBookId = book.id; currentScreen = "book_detail" })
                            "profile" -> { ProfileScreen(viewModel, { viewModel.logout() }, { currentScreen = "library" }); BackHandler { currentScreen = "library" } }
                            // REMOVI O CASE "STATS" AQUI
                            "add_book" -> { AddBookScreen(viewModel, { currentScreen = "library" }, { currentScreen = "library" }); BackHandler { currentScreen = "library" } }
                            "book_detail" -> { if (selectedBookId != null) { BookDetailScreen(viewModel, selectedBookId!!, { currentScreen = "library" }); BackHandler { currentScreen = "library" } } else currentScreen = "library" }
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
    onMenuClick: () -> Unit,
    onAddBookClick: () -> Unit,
    onBookClick: (Book) -> Unit
) {
    val bookList by viewModel.books.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos") }

    // Filtro local na lista
    val filteredBooks = bookList.filter { book ->
        val matchesSearch = book.title.contains(searchQuery, ignoreCase = true) ||
                book.author.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatus == "Todos") true else book.status == selectedStatus
        matchesSearch && matchesStatus
    }

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
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.secondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondary,
                    actionIconContentColor = MaterialTheme.colorScheme.secondary
                )
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
        Column(modifier = Modifier.padding(padding)) {

            // Busca e Filtros
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar título ou autor...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val filters = listOf("Todos", "Lendo", "Quero Ler", "Lido")
                    filters.forEach { filter ->
                        FilterChip(
                            selected = selectedStatus == filter,
                            onClick = { selectedStatus = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedStatus == filter,
                                borderColor = if (selectedStatus == filter) GoldAccent else Color.Transparent
                            )
                        )
                    }
                }
            }

            // Grid de Livros
            if (filteredBooks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Nenhum livro encontrado." else "Sua estante está vazia.",
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredBooks) { book ->
                        BookCard(book, onClick = { onBookClick(book) })
                    }
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
                val backgroundColor = if (book.coverColorHex != null) Color(book.coverColorHex) else Color.LightGray
                Box(modifier = Modifier.fillMaxSize().background(backgroundColor))
            }
            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.9f),
                    textAlign = TextAlign.Center
                )

                // Barra de progresso simples
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