package com.example.bookkeeper.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.ui.theme.components.ReadingProgressBar
import com.example.bookkeeper.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookViewModel,
    bookId: Int,
    onBackClick: () -> Unit
) {
    val bookList by viewModel.books.collectAsState()
    val book = bookList.find { it.id == bookId }

    var isEditing by remember { mutableStateOf(false) }

    var titleEdit by remember { mutableStateOf("") }
    var authorEdit by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Lendo") }
    var currentCoverPath by remember { mutableStateOf("") }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { if (viewModel.saveImageToInternalStorage(it) != null) currentCoverPath = viewModel.saveImageToInternalStorage(it)!! }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) { if (viewModel.saveBitmapToInternalStorage(bitmap) != null) currentCoverPath = viewModel.saveBitmapToInternalStorage(bitmap)!! }
    }

    LaunchedEffect(book) {
        book?.let {
            titleEdit = it.title
            authorEdit = it.author
            currentPage = if (it.currentPage > 0) it.currentPage.toString() else ""
            totalPages = if (it.totalPages > 0) it.totalPages.toString() else ""
            review = it.review
            selectedStatus = it.status
            currentCoverPath = it.coverUrl ?: ""
        }
    }

    if (book == null) return

    if (showImageSourceDialog && isEditing) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Alterar Capa") },
            text = { Text("Escolha a fonte da imagem:") },
            confirmButton = {
                TextButton(onClick = { showImageSourceDialog = false; cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Câmera")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Galeria")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "Editando..." else "Detalhes", fontFamily = FontFamily.Serif)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteBook(book)
                        onBackClick()
                    }) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        val updatedBook = book.copy(
                            title = titleEdit,
                            author = authorEdit,
                            currentPage = currentPage.toIntOrNull() ?: 0,
                            totalPages = totalPages.toIntOrNull() ?: 0,
                            review = review,
                            status = selectedStatus,
                            coverUrl = if (currentCoverPath.isNotBlank()) currentCoverPath else null
                        )
                        viewModel.saveBook(updatedBook)
                        isEditing = false
                    } else {
                        isEditing = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                if (isEditing) {
                    Icon(Icons.Rounded.Save, contentDescription = "Salvar")
                } else {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!isEditing) {
                Card(
                    elevation = CardDefaults.cardElevation(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(300.dp).width(200.dp)
                ) {
                    if (currentCoverPath.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(currentCoverPath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(book.coverColorHex ?: 0xFF888888)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Image, contentDescription = null, tint = Color.White.copy(0.5f), modifier = Modifier.size(64.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(book.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(book.author, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.7f), fontFamily = FontFamily.Serif)

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = GoldAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                ) {
                    Text(
                        text = book.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                ReadingProgressBar(
                    currentPage = book.currentPage,
                    totalPages = book.totalPages,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (book.review.isNotBlank()) {
                    Text("Minhas Anotações", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.review,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f), RoundedCornerShape(8.dp)).padding(16.dp)
                    )
                } else {
                    Text(
                        "Nenhuma anotação ainda!",
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        fontSize = 14.sp
                    )
                }

            } else {

                Box(contentAlignment = Alignment.BottomEnd) {
                    Card(
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(250.dp).width(170.dp).clickable { showImageSourceDialog = true }
                    ) {
                        if (currentCoverPath.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(currentCoverPath),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Image, contentDescription = null, tint = Color.White.copy(0.5f), modifier = Modifier.size(48.dp))
                            }
                        }
                    }
                    Surface(shape = CircleShape, color = GoldAccent, modifier = Modifier.size(40.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    }
                }
                Text("Toque para alterar capa", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.6f), modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(value = titleEdit, onValueChange = { titleEdit = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = authorEdit, onValueChange = { authorEdit = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Quero Ler", "Lendo", "Lido").forEach { statusOption ->
                        FilterChip(
                            selected = selectedStatus == statusOption,
                            onClick = { selectedStatus = statusOption },
                            label = { Text(statusOption) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedTextField(value = currentPage, onValueChange = { currentPage = it }, label = { Text("Pág. Atual") }, modifier = Modifier.weight(1f).padding(end = 8.dp), singleLine = true)
                    OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Total Págs") }, modifier = Modifier.weight(1f).padding(start = 8.dp), singleLine = true)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("Resenha / Anotações") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    placeholder = { Text("Escreva aqui...") }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}