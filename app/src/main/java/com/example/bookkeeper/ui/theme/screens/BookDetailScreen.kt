package com.example.bookkeeper.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.ui.theme.GoldAccent
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

    // Estados para edição (Iniciam vazios e preenchem no LaunchedEffect)
    var titleEdit by remember { mutableStateOf("") }
    var authorEdit by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Lendo") }
    // Guarda o caminho da imagem (pode ser URL ou caminho local)
    var currentCoverPath by remember { mutableStateOf("") }

    // --- CONFIGURAÇÃO DA GALERIA ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Quando o usuário escolhe uma imagem na galeria:
        uri?.let { safeUri ->
            // Pede ao ViewModel para salvar uma cópia interna e pega o novo caminho
            val localPath = viewModel.saveImageToInternalStorage(safeUri)
            if (localPath != null) {
                currentCoverPath = localPath
            }
        }
    }

    // Carrega os dados do livro quando a tela abre
    LaunchedEffect(book) {
        book?.let {
            titleEdit = it.title
            authorEdit = it.author
            currentPage = if (it.currentPage > 0) it.currentPage.toString() else ""
            totalPages = if (it.totalPages > 0) it.totalPages.toString() else ""
            review = it.review
            status = it.status
            currentCoverPath = it.coverUrl ?: ""
        }
    }

    if (book == null) return

    val current = currentPage.toIntOrNull() ?: 0
    val total = totalPages.toIntOrNull() ?: 1
    val progress = if (total > 0) current.toFloat() / total.toFloat() else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Livro", fontFamily = FontFamily.Serif) },
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
                    // SALVAR TUDO (Título, Autor, Capa, Progresso)
                    val updatedBook = book.copy(
                        title = titleEdit,
                        author = authorEdit,
                        currentPage = currentPage.toIntOrNull() ?: 0,
                        totalPages = totalPages.toIntOrNull() ?: 0,
                        review = review,
                        status = if (progress >= 1f) "Lido" else status,
                        // Salva o caminho novo da imagem (ou o antigo se não mudou)
                        coverUrl = if (currentCoverPath.isNotBlank()) currentCoverPath else null
                    )
                    viewModel.saveBook(updatedBook)
                    onBackClick()
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Rounded.Save, contentDescription = null)
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
            // --- CAPA EDITÁVEL ---
            Box(contentAlignment = Alignment.BottomEnd) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(250.dp)
                        .width(170.dp)
                        .clickable {
                            // Abre a galeria para imagens
                            galleryLauncher.launch("image/*")
                        }
                ) {
                    if (currentCoverPath.isNotBlank()) {
                        // O Coil é esperto: ele lê tanto URLs quanto arquivos locais
                        Image(
                            painter = rememberAsyncImagePainter(currentCoverPath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val backgroundColor = if (book.coverColorHex != null) Color(book.coverColorHex) else Color.LightGray
                        Box(modifier = Modifier.fillMaxSize().background(backgroundColor), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Image, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                        }
                    }
                }
                // Ícone de "Editar" sobre a capa
                Surface(
                    shape = RoundedCornerShape(topStart = 12.dp),
                    color = GoldAccent,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Alterar Capa", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Text("Toque na capa para alterar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // --- CAMPOS DE EDIÇÃO (TÍTULO E AUTOR) ---
            OutlinedTextField(
                value = titleEdit,
                onValueChange = { titleEdit = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authorEdit,
                onValueChange = { authorEdit = it },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CARTÃO DE PROGRESSO (Igual ao anterior) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Seu Progresso", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% Lido",
                        modifier = Modifier.align(Alignment.End),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = currentPage,
                            onValueChange = { currentPage = it },
                            label = { Text("Pág. Atual") },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = totalPages,
                            onValueChange = { totalPages = it },
                            label = { Text("Total Págs") },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CARTÃO DE RESENHA (Igual ao anterior) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Anotações & Resenha", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = review,
                        onValueChange = { review = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        placeholder = { Text("O que você está achando do livro?") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}