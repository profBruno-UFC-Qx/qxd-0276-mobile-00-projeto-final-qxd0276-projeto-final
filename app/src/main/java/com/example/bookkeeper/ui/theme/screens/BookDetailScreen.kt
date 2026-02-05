package com.example.bookkeeper.ui.theme.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.model.Note
import com.example.bookkeeper.model.ReadingSession
import com.example.bookkeeper.viewmodel.BookViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
            val book = books.getOrNull(pageIndex)
            if (book != null) {
                BookDetailDesignSkoob(
                    book = book,
                    viewModel = viewModel,
                    onEditClick = { showEditDialog = true }
                )
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", tint = Color.White)
        }

        if (showEditDialog && currentBook != null) {
            EditBookDialog(
                book = currentBook,
                viewModel = viewModel,
                onDismiss = { showEditDialog = false },
                onDelete = {
                    showEditDialog = false
                    onBackClick()
                }
            )
        }
    }
}

@Composable
fun BookDetailDesignSkoob(
    book: Book,
    viewModel: BookViewModel,
    onEditClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sinopse", "Notas", "Histórico")
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (book.coverUrl != null) {
            AsyncImage(
                model = book.coverUrl, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(350.dp).blur(radius = 20.dp)
            )
            Box(modifier = Modifier.fillMaxWidth().height(350.dp).background(Color.Black.copy(alpha = 0.4f)))
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(350.dp).background(
                Brush.verticalGradient(listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                ))
            ))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 110.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .padding(bottom = 50.dp)
                ) {
                    Spacer(modifier = Modifier.height(150.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = book.author,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val percent = if (book.totalPages > 0) (book.currentPage * 100 / book.totalPages) else 0
                            val sendIntent: android.content.Intent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT,
                                    "Estou lendo '${book.title}' e já completei $percent% da leitura! 📚 #BookKeeper")
                                type = "text/plain"
                            }
                            val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.width(8.dp))
                        Text("Compartilhar Progresso", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatItem(value = "${book.currentPage}", label = "Lidas")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        StatItem(value = "${book.totalPages}", label = "Total")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        StatItem(value = book.status, label = "Status")
                    }

                    val progress = if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 24.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    UpdatePageQuickAction(book, viewModel)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 8.dp)

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(it[selectedTabIndex]), color = MaterialTheme.colorScheme.primary) },
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }

                    when (selectedTabIndex) {
                        0 -> Column(Modifier.padding(24.dp)) { Text(text = if (book.review.isNotBlank()) book.review else "Nenhuma sinopse adicionada.", textAlign = TextAlign.Justify, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyLarge) }
                        1 -> UserNotesTab(book, viewModel)
                        2 -> { val sessions by viewModel.getBookSessions(book.id).collectAsState(initial = emptyList()); ReadingHistoryTab(sessions) }
                    }
                }

                Box {
                    Card(
                        elevation = CardDefaults.cardElevation(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(150.dp).height(230.dp)
                    ) {
                        if (book.coverUrl != null) {
                            AsyncImage(model = book.coverUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer), Alignment.Center) { Icon(Icons.Rounded.Add, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp)) }
                        }
                    }

                    FloatingActionButton(
                        onClick = onEditClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.BottomEnd).offset(x = 25.dp, y = 10.dp).size(56.dp)
                    ) {
                        Icon(Icons.Rounded.Edit, "Editar", modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun UpdatePageQuickAction(book: Book, viewModel: BookViewModel) {
    var currentPageInput by remember(book.currentPage) { mutableStateOf("") }
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Text("Atualizar leitura:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.width(8.dp))
        OutlinedTextField(
            value = currentPageInput,
            onValueChange = { if (it.all { c -> c.isDigit() }) currentPageInput = it },
            placeholder = { Text("${book.currentPage}") },
            modifier = Modifier.width(70.dp).height(48.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )
        IconButton(onClick = { val newPage = currentPageInput.toIntOrNull(); if (newPage != null) { val validPage = newPage.coerceIn(0, book.totalPages); viewModel.updateBook(book.copy(currentPage = validPage)); currentPageInput = ""; Toast.makeText(context, "Página atualizada!", Toast.LENGTH_SHORT).show() } }) { Icon(Icons.Rounded.Check, "Salvar", tint = MaterialTheme.colorScheme.primary) }
    }
}

@Composable
fun ReadingHistoryTab(sessions: List<ReadingSession>) { if (sessions.isEmpty()) Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) { Text("Nenhuma sessão registrada.", color = MaterialTheme.colorScheme.onSurfaceVariant) } else Column(Modifier.padding(16.dp)) { sessions.forEach { HistoryItem(it); Spacer(Modifier.height(8.dp)) } } }

@Composable
fun HistoryItem(session: ReadingSession) {
    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
    val durationText = if (session.durationSeconds / 60 > 0) "${session.durationSeconds / 60} min" else "${session.durationSeconds} s"
    Card(elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column { Text(dateFormat.format(java.util.Date(session.endTime)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("+ ${session.pagesRead} páginas", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp) }
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) { Text(durationText, Modifier.padding(6.dp, 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
fun UserNotesTab(book: Book, viewModel: BookViewModel) {
    val notesList by viewModel.getBookNotes(book.id).collectAsState(initial = emptyList())
    var showNoteDialog by remember { mutableStateOf(false) }
    var currentNoteToEdit by remember { mutableStateOf<Note?>(null) }
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { currentNoteToEdit = null; showNoteDialog = true }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Icon(Icons.Rounded.Add, null); Spacer(Modifier.width(8.dp)); Text("Nova Anotação") }
        if (notesList.isEmpty()) Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) { Text("Nenhuma anotação.", color = MaterialTheme.colorScheme.onSurfaceVariant) } else Column(modifier = Modifier.fillMaxWidth()) { notesList.forEach { note -> NoteItemCard(note = note, onEdit = { currentNoteToEdit = note; showNoteDialog = true }, onDelete = { viewModel.deleteNote(note) }); Spacer(modifier = Modifier.height(12.dp)) } }
    }
    if (showNoteDialog) NoteInputDialog(initialContent = currentNoteToEdit?.content ?: "", onDismiss = { showNoteDialog = false }, onConfirm = { content -> viewModel.saveNote(bookId = book.id, content = content, noteId = currentNoteToEdit?.id ?: 0); showNoteDialog = false })
}

@Composable
fun NoteItemCard(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = java.text.SimpleDateFormat("dd/MM 'às' HH:mm", java.util.Locale.getDefault())
    Card(elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = dateFormat.format(java.util.Date(note.timestamp)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Row { IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) { Icon(Icons.Rounded.Edit, "Editar", tint = MaterialTheme.colorScheme.onSurfaceVariant) }; IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) { Icon(Icons.Rounded.Delete, "Excluir", tint = MaterialTheme.colorScheme.error.copy(0.6f)) } }
            }
            Spacer(modifier = Modifier.height(4.dp)); Text(text = note.content, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun NoteInputDialog(initialContent: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initialContent) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialContent.isEmpty()) "Nova Anotação" else "Editar Anotação", color = MaterialTheme.colorScheme.primary) },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth().height(150.dp), placeholder = { Text("Escreva aqui...") }, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, cursorColor = MaterialTheme.colorScheme.primary)) },
        confirmButton = { Button(onClick = { if (text.isNotBlank()) onConfirm(text) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookDialog(book: Book, viewModel: BookViewModel, onDismiss: () -> Unit, onDelete: () -> Unit) {
    var title by remember { mutableStateOf(book.title) }
    var author by remember { mutableStateOf(book.author) }
    var totalPages by remember { mutableStateOf(book.totalPages.toString()) }
    var selectedStatus by remember { mutableStateOf(book.status) }
    var currentCoverPath by remember { mutableStateOf(book.coverUrl) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) scope.launch { currentCoverPath = viewModel.saveImageToInternalStorage(uri) } }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap -> if (bitmap != null) scope.launch { currentCoverPath = viewModel.saveBitmapToInternalStorage(bitmap) } }

    if (showImageSourceDialog) AlertDialog(onDismissRequest = { showImageSourceDialog = false }, title = { Text("Alterar Capa") }, confirmButton = { TextButton(onClick = { showImageSourceDialog = false; cameraLauncher.launch(null) }) { Text("Câmera", color = MaterialTheme.colorScheme.primary) } }, dismissButton = { TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) { Text("Galeria", color = MaterialTheme.colorScheme.primary) } }, containerColor = MaterialTheme.colorScheme.surface)
    if (showDeleteConfirmation) AlertDialog(onDismissRequest = { showDeleteConfirmation = false }, title = { Text("Excluir Livro?") }, text = { Text("Tem certeza que deseja apagar '${book.title}'? Essa ação não pode ser desfeita.") }, confirmButton = { Button(onClick = { viewModel.deleteBook(book); showDeleteConfirmation = false; onDelete() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Sim, Excluir") } }, dismissButton = { TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant) } }, containerColor = MaterialTheme.colorScheme.surface)

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Editar Livro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(24.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    Card(modifier = Modifier.height(180.dp).width(120.dp).clickable { showImageSourceDialog = true }) { if (currentCoverPath != null) AsyncImage(model = currentCoverPath, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) else Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } }
                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(4.dp)) { Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(24.dp).padding(4.dp), tint = MaterialTheme.colorScheme.onPrimary) }
                }
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, focusedLabelColor = MaterialTheme.colorScheme.primary)); Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, focusedLabelColor = MaterialTheme.colorScheme.primary)); Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Páginas") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, focusedLabelColor = MaterialTheme.colorScheme.primary))
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { listOf("Quero Ler", "Lendo", "Lido").forEach { status -> FilterChip(selected = selectedStatus == status, onClick = { selectedStatus = status }, label = { Text(status) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary)) } }
                Spacer(Modifier.height(32.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showDeleteConfirmation = true }) { Icon(Icons.Rounded.Delete, "Excluir", tint = MaterialTheme.colorScheme.error) }
                    Row { TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant) }; Spacer(Modifier.width(8.dp)); Button(onClick = { val updatedBook = book.copy(title = title, author = author, totalPages = totalPages.toIntOrNull() ?: 0, status = selectedStatus, coverUrl = currentCoverPath); viewModel.updateBook(updatedBook); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Salvar") } }
                }
            }
        }
    }
}