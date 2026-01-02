package com.example.bookkeeper.ui.theme.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.GoldAccent
import com.example.bookkeeper.viewmodel.BookViewModel
import kotlinx.coroutines.delay

private enum class ScreenMode {
    SELECTION, ISBN_MANUAL, MANUAL_FORM, SCANNER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var currentMode by remember { mutableStateOf(ScreenMode.SELECTION) }

    // Estados do Formulário
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Quero Ler") }
    var currentCoverPath by remember { mutableStateOf<String?>(null) }
    var isbnQuery by remember { mutableStateOf("") }

    val statusOptions = listOf("Quero Ler", "Lendo", "Lido")
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading by viewModel.isLoading.collectAsState()

    // Permissão da Câmera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) {
                currentMode = ScreenMode.SCANNER // Se permitiu, abre a câmera direto
            } else {
                Toast.makeText(context, "Precisamos da câmera para ler o código", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Funcao auxiliar para tentar abrir a camera
    fun tryOpenScanner() {
        if (hasCameraPermission) {
            currentMode = ScreenMode.SCANNER
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { currentCoverPath = viewModel.saveImageToInternalStorage(it) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> if (bitmap != null) { currentCoverPath = viewModel.saveBitmapToInternalStorage(bitmap) } }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Escolher Capa") },
            text = { Text("Fonte da imagem:") },
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
            // Esconde TopBar no modo Scanner para imersão total
            if (currentMode != ScreenMode.SCANNER) {
                TopAppBar(
                    title = {
                        val titulo = when (currentMode) {
                            ScreenMode.SELECTION -> "Adicionar Livro"
                            ScreenMode.ISBN_MANUAL -> "Digitar ISBN"
                            ScreenMode.MANUAL_FORM -> "Cadastro Manual"
                            else -> ""
                        }
                        Text(titulo, fontFamily = FontFamily.Serif)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentMode != ScreenMode.SELECTION) {
                                currentMode = ScreenMode.SELECTION
                            } else {
                                onBackClick()
                            }
                        }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        floatingActionButton = {
            if (currentMode == ScreenMode.MANUAL_FORM) {
                FloatingActionButton(
                    onClick = {
                        if (title.isBlank() || author.isBlank()) {
                            Toast.makeText(context, "Preencha Título e Autor", Toast.LENGTH_SHORT).show()
                        } else {
                            val newBook = Book(
                                title = title, author = author, totalPages = totalPages.toIntOrNull() ?: 0,
                                review = review, userId = 0, coverUrl = currentCoverPath, status = selectedStatus
                            )
                            viewModel.saveBook(newBook)
                            Toast.makeText(context, "Livro Salvo!", Toast.LENGTH_SHORT).show()
                            onSaveSuccess()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) { Icon(Icons.Default.Save, "Salvar") }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when (currentMode) {
                    // 1. TELA DE ESCOLHA
                    ScreenMode.SELECTION -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Como deseja adicionar?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(32.dp))

                            SelectionCard(
                                icon = Icons.Default.QrCodeScanner, // Ícone de Scanner agora
                                title = "Escanear Código de Barras",
                                subtitle = "Busca automática usando a câmera",
                                onClick = { tryOpenScanner() } // Vai direto pro Scanner
                            )
                            Spacer(Modifier.height(24.dp))
                            SelectionCard(
                                icon = Icons.Default.Edit,
                                title = "Digitar Manualmente",
                                subtitle = "Preencha você mesmo os detalhes",
                                onClick = { currentMode = ScreenMode.MANUAL_FORM }
                            )
                        }
                    }

                    // 2. TELA DO SCANNER (ESTILO BANCO)
                    ScreenMode.SCANNER -> {
                        // Estado para controlar se mostra a opção manual
                        var showManualOption by remember { mutableStateOf(false) }

                        // Timer: Espera 5 segundos antes de mostrar o botão de ajuda
                        LaunchedEffect(Unit) {
                            delay(5000) // 5000ms = 5 segundos
                            showManualOption = true
                        }

                        Box(Modifier.fillMaxSize()) {
                            // Câmera no fundo
                            BarcodeScanner(
                                onBarcodeDetected = { code ->
                                    viewModel.searchAndSaveBook(code) { onSaveSuccess() }
                                }
                            )

                            // Botão de Fechar (X) no topo
                            IconButton(
                                onClick = { currentMode = ScreenMode.SELECTION },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                                    .statusBarsPadding() // Respeita o notch/camera frontal
                                    .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                            ) {
                                Icon(Icons.Default.Close, "Fechar", tint = Color.White)
                            }

                            // Mira Visual (Frame central)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(300.dp, 180.dp)
                                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                            ) {
                                // Linha vermelha de laser
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color.Red)
                                        .align(Alignment.Center)
                                )
                            }

                            // Texto instrutivo
                            Text(
                                "Aponte para o código de barras",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 220.dp) // Fica abaixo da mira
                            )

                            // --- BOTÃO DE AJUDA (APARECE DEPOIS DE 5s) ---
                            AnimatedVisibility(
                                visible = showManualOption,
                                enter = slideInVertically { it } + fadeIn(),
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Problemas em ler o código?",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = { currentMode = ScreenMode.ISBN_MANUAL },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text("Digitar número do ISBN")
                                    }
                                }
                            }
                        }
                    }

                    // 3. DIGITAÇÃO DE ISBN (FALLBACK)
                    ScreenMode.ISBN_MANUAL -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(40.dp))
                            Text("Digite o ISBN do livro", fontSize = 18.sp)
                            Text("(Número abaixo do código de barras)", fontSize = 14.sp, color = Color.Gray)

                            Spacer(Modifier.height(24.dp))

                            OutlinedTextField(
                                value = isbnQuery,
                                onValueChange = { isbnQuery = it },
                                label = { Text("Número do ISBN") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("Ex: 97885...") }
                            )

                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    if (isbnQuery.isNotEmpty()) {
                                        Toast.makeText(context, "Buscando...", Toast.LENGTH_SHORT).show()
                                        viewModel.searchAndSaveBook(isbnQuery) { onSaveSuccess() }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("BUSCAR E SALVAR")
                            }

                            // Botão para tentar câmera de novo
                            TextButton(onClick = { currentMode = ScreenMode.SCANNER }) {
                                Text("Tentar Câmera novamente", textDecoration = TextDecoration.Underline)
                            }
                        }
                    }

                    // 4. CADASTRO MANUAL (COMPLETO)
                    ScreenMode.MANUAL_FORM -> {
                        // ... (Mesmo código do form manual de antes) ...
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // ... Campos da Capa, Titulo, Autor ...
                            Card(
                                modifier = Modifier.height(200.dp).width(140.dp).clickable { showImageSourceDialog = true },
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                if (currentCoverPath != null) {
                                    Image(rememberAsyncImagePainter(currentCoverPath), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                } else {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = totalPages, onValueChange = { totalPages = it }, label = { Text("Páginas") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(value = review, onValueChange = { review = it }, label = { Text("Sinopse") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                statusOptions.forEach { status ->
                                    FilterChip(
                                        selected = selectedStatus == status,
                                        onClick = { selectedStatus = status },
                                        label = { Text(status) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldAccent, selectedLabelColor = MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}