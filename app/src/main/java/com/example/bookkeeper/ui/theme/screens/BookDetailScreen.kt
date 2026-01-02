package com.example.bookkeeper.ui.theme.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.viewmodel.BookViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookViewModel,
    bookId: Int,
    onBackClick: () -> Unit
) {
    val books by viewModel.books.collectAsState()
    val initialPage = remember(books, bookId) {
        val index = books.indexOfFirst { it.id == bookId }
        if (index >= 0) index else 0
    }

    if (books.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { books.size }
    )

    var showEditDialog by remember { mutableStateOf(false) }
    val currentBook = books.getOrNull(pagerState.currentPage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes", fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Editar Detalhes")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) { pageIndex ->
                val book = books.getOrNull(pageIndex)
                if (book != null) {
                    BookDetailContent(book = book, viewModel = viewModel)
                }
            }

            if (showEditDialog && currentBook != null) {
                EditBookDialog(
                    book = currentBook,
                    viewModel = viewModel,
                    onDismiss = { showEditDialog = false }
                )
            }
        }
    }
}

@Composable
fun BookDetailContent(book: Book, viewModel: BookViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sinopse", "Minhas Notas")
    val context = LocalContext.current

    // --- ESTADOS PARA O PROGRESSO ---
    // Reinicia os estados quando muda de livro (swipe)
    var currentPageInput by remember(book.id) { mutableStateOf(book.currentPage.toString()) }
    // Calcula porcentagem para a barra visual (0.0 a 1.0)
    val progressFraction = remember(book.currentPage, book.totalPages) {
        if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages.toFloat() else 0f
    }
    val progressPercent = (progressFraction * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // CABEÇALHO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(12.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(220.dp).width(150.dp)
            ) {
                if (book.coverUrl != null) {
                    AsyncImage(model = book.coverUrl, contentDescription = "Capa", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color(book.coverColorHex ?: 0xFF888888)), contentAlignment = Alignment.Center) {
                        Text("Sem Capa", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = book.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = book.author,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // STATUS
        Surface(
            color = GoldAccent.copy(alpha = 0.2f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
        ) {
            Text(
                text = book.status.uppercase(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CARTÃO DE PROGRESSO DE LEITURA (NOVO) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Progresso de Leitura", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("$progressPercent%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BARRA DE PROGRESSO VISUAL
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .padding(vertical = 4.dp),
                    color = GoldAccent,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CONTROLES DE PÁGINA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Input: Página Atual
                    OutlinedTextField(
                        value = currentPageInput,
                        onValueChange = { newValue ->
                            // Só deixa digitar números
                            if (newValue.all { it.isDigit() }) {
                                currentPageInput = newValue
                            }
                        },
                        label = { Text("Pág. Atual") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Texto fixo: Total
                    Text(
                        "/ ${book.totalPages}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Botão: Atualizar
                    FilledIconButton(
                        onClick = {
                            val newPage = currentPageInput.toIntOrNull() ?: 0
                            val validPage = newPage.coerceIn(0, book.totalPages) // Impede passar do total

                            viewModel.saveBook(book.copy(currentPage = validPage))

                            // Feedback visual rápido
                            if (newPage > book.totalPages) {
                                Toast.makeText(context, "Ajustado para o máximo (${book.totalPages})", Toast.LENGTH_SHORT).show()
                                currentPageInput = book.totalPages.toString()
                            } else {
                                Toast.makeText(context, "Progresso atualizado!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = "Atualizar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ABAS ---
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = GoldAccent
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (book.review.isNotBlank()) {
                        Text(
                            text = book.review,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(
                            "Nenhuma sinopse disponível.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
            1 -> { UserNotesTab(book, viewModel) }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun UserNotesTab(book: Book, viewModel: BookViewModel) {
    var notes by remember(book.id) { mutableStateOf(book.userNotes) }
    Column(modifier = Modifier.padding(24.dp)) {
        OutlinedTextField(
            value = notes, onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().height(250.dp),
            placeholder = { Text("Escreva suas citações favoritas...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldAccent, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.updateBookNotes(book, notes) },
            modifier = Modifier.align(Alignment.End),
            enabled = notes != book.userNotes,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Rounded.Save, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Salvar Notas")
        }
    }
}

// DIALOG DE EDIÇÃO (Mantido igual, mas garante que dá pra editar o Total de Páginas caso esteja errado)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookDialog(book: Book, viewModel: BookViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author) }
    var totalPages by remember { mutableStateOf(book.totalPages.toString()) }
    var selectedStatus by remember { mutableStateOf(book.status) }
    var currentCoverPath by remember { mutableStateOf(book.coverUrl) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val statusOptions = listOf("Quero Ler", "Lendo", "Lido")
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { currentCoverPath = viewModel.saveImageToInternalStorage(it) } }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap -> if (bitmap != null) { currentCoverPath = viewModel.saveBitmapToInternalStorage(bitmap) } }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Alterar Capa") },
            confirmButton = { TextButton(onClick = { showImageSourceDialog = false; cameraLauncher.launch(null) }) { Text("Câmera") } },
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) { Text("Galeria") } }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Editar Informações", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))

                Box(contentAlignment = Alignment.BottomEnd) {
                    Card(modifier = Modifier.height(180.dp).width(120.dp).clickable { showImageSourceDialog = true }) {
                        if (currentCoverPath != null) Image(rememberAsyncImagePainter(currentCoverPath), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        else Box(Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) { Icon(Icons.Default.AddPhotoAlternate, null) }
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent, modifier = Modifier.padding(4.dp)) { Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(24.dp).padding(4.dp)) }
                }
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Total de Páginas") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    statusOptions.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status, onClick = { selectedStatus = status },
                            label = { Text(status) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, selectedLabelColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updatedBook = book.copy(title = title, author = author, totalPages = totalPages.toIntOrNull() ?: 0, status = selectedStatus, coverUrl = currentCoverPath)
                            viewModel.saveBook(updatedBook)
                            onDismiss()
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("Salvar") }
                }
            }
        }
    }
}