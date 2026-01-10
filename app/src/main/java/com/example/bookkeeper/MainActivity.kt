package com.example.bookkeeper

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.ui.theme.screens.*
import com.example.bookkeeper.viewmodel.BookViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

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

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoading) {
                        LoadingScreen()
                    } else if (currentUser == null) {
                        LoginScreen(viewModel = viewModel)
                        currentScreen = "library"
                    } else {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                ModalDrawerSheet(
                                    modifier = Modifier.width(300.dp),
                                    drawerContainerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    // CABEÇALHO DO MENU LATERAL
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                            .padding(24.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surface)
                                                .clickable {
                                                    currentScreen = "profile"
                                                    scope.launch { drawerState.close() }
                                                }
                                        ) {
                                            val imageUri = currentUser?.profilePictureUri
                                            if (!imageUri.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = imageUri,
                                                    contentDescription = "Foto de Perfil",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Rounded.AccountCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(60.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        Text(
                                            currentUser?.name ?: "Leitor",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            currentUser?.email ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    // --- ITEM DE ESTATÍSTICAS NO MENU ---
                                    NavigationDrawerItem(
                                        label = { Text("Estatísticas") },
                                        icon = { Icon(Icons.Rounded.BarChart, null) },
                                        selected = currentScreen == "stats",
                                        onClick = { currentScreen = "stats"; scope.launch { drawerState.close() } },
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                                    )

                                    NavigationDrawerItem(
                                        label = { Text("Minha Estante") },
                                        icon = { Icon(Icons.Rounded.Book, null) },
                                        selected = currentScreen == "library",
                                        onClick = { currentScreen = "library"; scope.launch { drawerState.close() } },
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                                    )
                                    NavigationDrawerItem(
                                        label = { Text("Sessão de Leitura") },
                                        icon = { Icon(Icons.Rounded.Timer, null) },
                                        selected = currentScreen == "reading_session",
                                        onClick = { currentScreen = "reading_session"; scope.launch { drawerState.close() } },
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                                    )
                                    NavigationDrawerItem(
                                        label = { Text("Meu Perfil") },
                                        icon = { Icon(Icons.Rounded.Person, null) },
                                        selected = currentScreen == "profile",
                                        onClick = { currentScreen = "profile"; scope.launch { drawerState.close() } },
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                                    )

                                    Spacer(Modifier.weight(1f))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    NavigationDrawerItem(
                                        label = { Text("Sair") },
                                        icon = { Icon(Icons.Rounded.Logout, null) },
                                        selected = false,
                                        onClick = { scope.launch { drawerState.close() }; viewModel.logout() },
                                        modifier = Modifier.padding(12.dp),
                                        colors = NavigationDrawerItemDefaults.colors(
                                            unselectedIconColor = MaterialTheme.colorScheme.error,
                                            unselectedTextColor = MaterialTheme.colorScheme.error
                                        )
                                    )
                                }
                            }
                        ) {
                            // --- NAVEGAÇÃO PRINCIPAL ATUALIZADA ---
                            when (currentScreen) {
                                "library" -> LibraryScreen(
                                    viewModel = viewModel,
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onAddBookClick = { currentScreen = "add_book" },
                                    onBookClick = { book -> selectedBookId = book.id; currentScreen = "book_detail" },
                                    onNotificationClick = { currentScreen = "notifications" }
                                )

                                "stats" -> {
                                    StatsScreen(
                                        viewModel = viewModel,
                                        onMenuClick = { scope.launch { drawerState.open() } }
                                    )
                                    BackHandler { currentScreen = "library" }
                                }

                                "notifications" -> {
                                    NotificationScreen(onBackClick = { currentScreen = "library" })
                                    BackHandler { currentScreen = "library" }
                                }
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
}

// O restante das funções LibraryScreen e BookCard seguem abaixo sem alterações...

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
    var isSearchVisible by remember { mutableStateOf(false) }
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
                    Column(modifier = Modifier.padding(top = 40.dp, bottom = 8.dp)) {
                        Text("Minha Estante", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                                Text("$totalPages págs. lidas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            if (currentUser != null) Text(" • ${currentUser?.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick, modifier = Modifier.padding(top = 40.dp)) {
                        Icon(Icons.Default.Menu, "Menu", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(top = 40.dp)) {
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            val iconColor = if (isSearchVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            Icon(Icons.Rounded.Search, "Buscar", tint = iconColor)
                        }
                        IconButton(onClick = onNotificationClick) {
                            Icon(Icons.Rounded.Notifications, "Notificações", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier.height(120.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBookClick, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, shape = CircleShape) {
                Icon(Icons.Default.Add, null)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp, vertical = 8.dp)) {
                AnimatedVisibility(visible = isSearchVisible, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar título ou autor...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Rounded.Close, "Limpar", tint = MaterialTheme.colorScheme.outline) }
                            }
                        }
                    )
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    listOf("Todos", "Lendo", "Quero Ler", "Lido").forEach { filter ->
                        FilterChip(
                            selected = selectedStatus == filter,
                            onClick = { selectedStatus = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }
            }

            if (filteredBooks.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Book, null, Modifier.size(60.dp), tint = MaterialTheme.colorScheme.outline)
                        Text(if (searchQuery.isNotEmpty()) "Nenhum livro encontrado." else "Sua estante está vazia.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                    items(filteredBooks) { book -> BookCard(book) { onBookClick(book) } }
                }
            }
        }
    }
}

@Composable
fun BookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier.height(280.dp).fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(0.65f).fillMaxWidth()) {
                if (book.coverUrl != null) Image(painter = rememberAsyncImagePainter(book.coverUrl), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer), Alignment.Center) { Icon(Icons.Rounded.Book, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(40.dp)) }
            }
            Column(Modifier.weight(0.35f).fillMaxWidth().padding(8.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                Text(text = book.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, lineHeight = 18.sp)
                Text(text = book.author, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                if (book.totalPages > 0) LinearProgressIndicator(progress = { book.currentPage.toFloat() / book.totalPages.toFloat() }, modifier = Modifier.fillMaxWidth().height(4.dp).padding(top = 6.dp).clip(RoundedCornerShape(2.dp)), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}