package com.example.bookkeeper.ui.theme.screens

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
import androidx.compose.material.icons.rounded.PlayArrow
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
import com.example.bookkeeper.model.ReadingSession
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.viewmodel.BookViewModel
import com.example.bookkeeper.ui.theme.components.ReadingTimer
import com.example.bookkeeper.ui.theme.components.SaveSessionDialog
import kotlinx.coroutines.launch // Essencial para corrigir o erro

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(viewModel: BookViewModel, bookId: Int, onBackClick: () -> Unit) {
    val books by viewModel.books.collectAsState()
    if (books.isEmpty()) return

    val initialPage = remember(books, bookId) {
        val index = books.indexOfFirst { it.id == bookId }
        if (index >= 0) index else 0
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { books.size })
    var showEditDialog by remember { mutableStateOf(false) }
    val currentBook = books.getOrNull(pagerState.currentPage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes", fontFamily = FontFamily.Serif) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar") } },
                actions = { IconButton(onClick = { showEditDialog = true }) { Icon(Icons.Rounded.Edit, "Editar") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary, actionIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) { pageIndex ->
                val book = books.getOrNull(pageIndex)
                if (book != null) BookDetailContent(book = book, viewModel = viewModel)
            }
            if (showEditDialog && currentBook != null) {
                EditBookDialog(book = currentBook, viewModel = viewModel, onDismiss = { showEditDialog = false })
            }
        }
    }
}

@Composable
fun BookDetailContent(book: Book, viewModel: BookViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sinopse", "Notas", "Histórico")
    val context = LocalContext.current
    val elapsedTime by viewModel.elapsedTimeSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val showSessionDialog by viewModel.showSaveSessionDialog.collectAsState()
    var currentPageInput by remember(book.id, book.currentPage) { mutableStateOf(book.currentPage.toString()) }
    val progressFraction = remember(book.currentPage, book.totalPages) { if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages.toFloat() else 0f }
    val progressPercent = (progressFraction * 100).toInt()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Card(elevation = CardDefaults.cardElevation(12.dp), shape = RoundedCornerShape(8.dp), modifier = Modifier.height(220.dp).width(150.dp)) {
                if (book.coverUrl != null) AsyncImage(model = book.coverUrl, contentDescription = "Capa", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                else Box(modifier = Modifier.fillMaxSize().background(Color(book.coverColorHex ?: 0xFF888888)), contentAlignment = Alignment.Center) { Text("Sem Capa", color = Color.White) }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(book.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center, lineHeight = 26.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Text(book.author, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
        Surface(color = GoldAccent.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)) {
            Text(book.status.uppercase(), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(24.dp))

        if (book.status == "Lendo") {
            ReadingTimer(elapsedSeconds = elapsedTime, isRunning = isTimerRunning, onToggle = { viewModel.toggleTimer() })
        } else if (book.status == "Quero Ler") {
            Button(onClick = { viewModel.updateBook(book.copy(status = "Lendo")); Toast.makeText(context, "Boa leitura!", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                Icon(Icons.Rounded.PlayArrow, null); Spacer(Modifier.width(8.dp)); Text("Começar Leitura", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Progresso", fontWeight = FontWeight.Bold); Text("$progressPercent%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                LinearProgressIndicator(progress = { progressFraction }, modifier = Modifier.fillMaxWidth().height(10.dp).padding(vertical = 4.dp), color = GoldAccent, trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = currentPageInput, onValueChange = { if (it.all { c -> c.isDigit() }) currentPageInput = it }, label = { Text("Pág.") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Text("/ ${book.totalPages}", fontWeight = FontWeight.Bold)
                    FilledIconButton(onClick = {
                        val newPage = currentPageInput.toIntOrNull() ?: 0
                        val validPage = newPage.coerceIn(0, book.totalPages)
                        viewModel.updateBook(book.copy(currentPage = validPage))
                        Toast.makeText(context, "Atualizado!", Toast.LENGTH_SHORT).show()
                    }) { Icon(Icons.Rounded.Check, "Salvar") }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = MaterialTheme.colorScheme.background, indicator = { TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(it[selectedTabIndex]), color = GoldAccent) }) {
            tabs.forEachIndexed { index, title -> Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }) }
        }
        when (selectedTabIndex) {
            0 -> Column(Modifier.padding(24.dp)) { Text(if (book.review.isNotBlank()) book.review else "Sem sinopse.", textAlign = TextAlign.Justify) }
            1 -> UserNotesTab(book, viewModel)
            2 -> { val sessions by viewModel.getBookSessions(book.id).collectAsState(initial = emptyList()); ReadingHistoryTab(sessions) }
        }
        Spacer(Modifier.height(50.dp))
    }
    SaveSessionDialog(show = showSessionDialog, onDismiss = { viewModel.dismissSessionDialog() }, onConfirm = { viewModel.confirmSaveSession(book.id) }, inputValue = viewModel.pagesReadInput, onInputChange = { viewModel.pagesReadInput = it })
}

@Composable
fun ReadingHistoryTab(sessions: List<ReadingSession>) {
    if (sessions.isEmpty()) Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) { Text("Nenhuma sessão registrada.", color = Color.Gray) }
    else Column(Modifier.padding(16.dp)) { sessions.forEach { HistoryItem(it); Spacer(Modifier.height(8.dp)) } }
}

@Composable
fun HistoryItem(session: ReadingSession) {
    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    val dateString = dateFormat.format(java.util.Date(session.endTime))
    val durationText = if (session.durationSeconds / 60 > 0) "${session.durationSeconds / 60} min" else "${session.durationSeconds} s"
    Card(elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column { Text(dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray); Text("+ ${session.pagesRead} pág", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(16.dp)) { Text(durationText, Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall) }
        }
    }
}

@Composable
fun UserNotesTab(book: Book, viewModel: BookViewModel) {
    var notes by remember(book.id) { mutableStateOf(book.userNotes) }
    Column(Modifier.padding(24.dp)) {
        OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth().height(250.dp), placeholder = { Text("Anotações...") }, shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.updateBookNotes(book, notes) }, Modifier.align(Alignment.End), enabled = notes != book.userNotes) { Icon(Icons.Rounded.Save, null); Spacer(Modifier.width(8.dp)); Text("Salvar") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookDialog(book: Book, viewModel: BookViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author) }
    var totalPages by remember { mutableStateOf(book.totalPages.toString()) }
    var selectedStatus by remember { mutableStateOf(book.status) }
    var currentCoverPath by remember { mutableStateOf(book.coverUrl) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                currentCoverPath = viewModel.saveImageToInternalStorage(uri)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            scope.launch {
                currentCoverPath = viewModel.saveBitmapToInternalStorage(bitmap)
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Alterar Capa") },
            confirmButton = { TextButton(onClick = { showImageSourceDialog = false; cameraLauncher.launch(null) }) { Text("Câmera") } },
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) { Text("Galeria") } }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Editar Livro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    Card(modifier = Modifier.height(180.dp).width(120.dp).clickable { showImageSourceDialog = true }) {
                        if (currentCoverPath != null) AsyncImage(model = currentCoverPath, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        else Box(Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) { Icon(Icons.Default.AddPhotoAlternate, null) }
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent, modifier = Modifier.padding(4.dp)) { Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(24.dp).padding(4.dp)) }
                }
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Páginas") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(24.dp))
                val statusOptions = listOf("Quero Ler", "Lendo", "Lido")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    statusOptions.forEach { status -> FilterChip(selected = selectedStatus == status, onClick = { selectedStatus = status }, label = { Text(status) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent)) }
                }
                Spacer(Modifier.height(32.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val updatedBook = book.copy(title = title, author = author, totalPages = totalPages.toIntOrNull() ?: 0, status = selectedStatus, coverUrl = currentCoverPath)
                        viewModel.updateBook(updatedBook)
                        onDismiss()
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Salvar") }
                }
            }
        }
    }
}