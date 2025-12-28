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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            BookKeeperTheme(darkTheme = isDarkTheme) {
                val currentUser by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                // Estado de navegação
                var currentScreen by remember { mutableStateOf("library") }
                var selectedBookId by remember { mutableStateOf<Int?>(null) }

                // --- CONTROLE DA BARRA LATERAL (DRAWER) ---
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                if (isLoading) {
                    LoadingScreen()
                } else if (currentUser == null) {
                    LoginScreen(viewModel = viewModel)
                    currentScreen = "library"
                } else {
                    // O MENU LATERAL "ABRAÇA" O APP TODO
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.width(300.dp) // Largura da barra
                            ) {
                                // --- CABEÇALHO DO MENU (FOTO E NOME) ---
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(24.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = currentUser?.name ?: "Leitor",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = GoldAccent,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Text(
                                        text = currentUser?.email ?: "",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // --- ITENS DO MENU ---
                                NavigationDrawerItem(
                                    label = { Text("Minha Estante") },
                                    icon = { Icon(Icons.Rounded.Book, null) },
                                    selected = currentScreen == "library",
                                    onClick = {
                                        currentScreen = "library"
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                NavigationDrawerItem(
                                    label = { Text("Meu Perfil") },
                                    icon = { Icon(Icons.Rounded.Person, null) },
                                    selected = currentScreen == "profile",
                                    onClick = {
                                        currentScreen = "profile"
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                // Item Decorativo (Exemplo de "Outras coisas")
                                NavigationDrawerItem(
                                    label = { Text("Estatísticas (Em breve)") },
                                    icon = { Icon(Icons.Rounded.BarChart, null) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        // Aqui você poderia navegar para uma tela de stats
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                NavigationDrawerItem(
                                    label = { Text("Sobre o App") },
                                    icon = { Icon(Icons.Rounded.Info, null) },
                                    selected = false,
                                    onClick = { scope.launch { drawerState.close() } },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f)) // Empurra o Sair para baixo
                                HorizontalDivider()

                                NavigationDrawerItem(
                                    label = { Text("Sair") },
                                    icon = { Icon(Icons.Rounded.Logout, null) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        viewModel.logout()
                                    },
                                    modifier = Modifier.padding(12.dp),
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedIconColor = MaterialTheme.colorScheme.error,
                                        unselectedTextColor = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    ) {
                        // --- CONTEÚDO PRINCIPAL (TELAS) ---
                        when (currentScreen) {
                            "library" -> {
                                LibraryScreen(
                                    viewModel = viewModel,
                                    // Agora o clique abre o Drawer em vez de ir pro perfil direto
                                    onMenuClick = { scope.launch { drawerState.open() } },
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
                                    onLogoutClick = { /* O logout já é tratado no ViewModel */ },
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
}

// ATUALIZAÇÃO NA TELA DA ESTANTE: Ícone de Menu no lugar do Perfil
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: BookViewModel,
    onMenuClick: () -> Unit, // Mudou de onProfileClick para onMenuClick
    onAddBookClick: () -> Unit,
    onBookClick: (Book) -> Unit
) {
    val bookList by viewModel.books.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Todos") }

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
                // ÍCONE DO MENU HAMBÚRGUER (ABRE A BARRA LATERAL)
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.secondary)
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
        Column(modifier = Modifier.padding(padding)) {

            // --- ÁREA DE CONTROLE (BUSCA + CHIPS) ---
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
                            label = {
                                Text(
                                    filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedStatus == filter) FontWeight.Bold else FontWeight.Normal
                                )
                            },
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

            // --- GRID DE LIVROS ---
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

// BookCard continua o mesmo (não precisa mudar, mas mantive o import)
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

                // Barra de progresso compacta (Requer que você tenha criado o ReadingProgressBar no passo anterior)
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