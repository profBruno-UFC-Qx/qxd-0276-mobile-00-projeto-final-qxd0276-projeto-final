package com.example.bookkeeper.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.viewmodel.BookViewModel


private enum class ScreenMode { SELECTION, ISBN_MANUAL, MANUAL_FORM, SCANNER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var currentMode by remember { mutableStateOf(ScreenMode.SELECTION) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Quero Ler") }
    var currentCoverPath by remember { mutableStateOf<String?>(null) }
    var isbnQuery by remember { mutableStateOf("") }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) currentMode = ScreenMode.SCANNER
            else Toast.makeText(context, "Permissão necessária", Toast.LENGTH_SHORT).show()
        }
    )

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Escolher Capa") },
            confirmButton = { TextButton(onClick = { showImageSourceDialog = false }) { Text("Câmera") } },
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false }) { Text("Galeria") } }
        )
    }

    Scaffold(
        topBar = {
            if (currentMode != ScreenMode.SCANNER) {
                TopAppBar(
                    title = { Text(if (currentMode == ScreenMode.SELECTION) "Adicionar Livro" else "Cadastro", fontFamily = FontFamily.Serif) },
                    navigationIcon = { IconButton(onClick = { if (currentMode != ScreenMode.SELECTION) currentMode = ScreenMode.SELECTION else onBackClick() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
        },
        floatingActionButton = {
            if (currentMode == ScreenMode.MANUAL_FORM) {
                FloatingActionButton(onClick = {
                    if (title.isBlank() || author.isBlank()) Toast.makeText(context, "Preencha Título e Autor", Toast.LENGTH_SHORT).show()
                    else {
                        viewModel.saveBook(Book(title = title, author = author, totalPages = totalPages.toIntOrNull() ?: 0, review = review, userId = 0, coverUrl = currentCoverPath, status = selectedStatus))
                        onSaveSuccess()
                    }
                }, containerColor = MaterialTheme.colorScheme.secondary) { Icon(Icons.Default.Save, "Salvar") }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            else {
                when (currentMode) {
                    ScreenMode.SELECTION -> {
                        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Como deseja adicionar?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(32.dp))
                            SelectionCard(Icons.Default.QrCodeScanner, "Escanear Código", "Busca automática") {
                                if (hasCameraPermission) currentMode = ScreenMode.SCANNER else permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            Spacer(Modifier.height(24.dp))
                            SelectionCard(Icons.Default.Edit, "Digitar Manualmente", "Preencher detalhes") { currentMode = ScreenMode.MANUAL_FORM }
                        }
                    }
                    ScreenMode.SCANNER -> {
                        Box(Modifier.fillMaxSize()) {
                            BarcodeScanner(onBarcodeDetected = { code ->
                                currentMode = ScreenMode.SELECTION
                                viewModel.searchAndSaveBook(code) { onSaveSuccess() }
                            })
                            IconButton(onClick = { currentMode = ScreenMode.SELECTION }, modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(0.5f), CircleShape)) {
                                Icon(Icons.Default.Close, null, tint = Color.White)
                            }
                            Box(Modifier.align(Alignment.Center).size(280.dp, 150.dp).border(2.dp, Color.White, RoundedCornerShape(12.dp))) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(Color.Red).align(Alignment.Center))
                            }
                        }
                    }
                    ScreenMode.ISBN_MANUAL -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            OutlinedTextField(value = isbnQuery, onValueChange = { isbnQuery = it }, label = { Text("ISBN") }, modifier = Modifier.fillMaxWidth())
                            Button(onClick = { viewModel.searchAndSaveBook(isbnQuery) { onSaveSuccess() } }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("BUSCAR") }
                        }
                    }
                    ScreenMode.MANUAL_FORM -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                            val pagesInt = totalPages.toIntOrNull() ?: 0
                            if (pagesInt == 0) {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Defina as páginas para o progresso funcionar.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Páginas") }, isError = pagesInt == 0, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = review, onValueChange = { review = it }, label = { Text("Sinopse") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf("Quero Ler", "Lendo", "Lido").forEach { status ->
                                    FilterChip(selected = selectedStatus == status, onClick = { selectedStatus = status }, label = { Text(status) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().height(100.dp).clickable(onClick = onClick)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, fontSize = 14.sp) }
        }
    }
}