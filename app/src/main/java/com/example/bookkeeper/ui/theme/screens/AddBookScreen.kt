package com.example.bookkeeper.ui.theme.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.CameraAlt
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.viewmodel.BookViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    viewModel: BookViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val statusOptions = listOf("Quero Ler", "Lendo", "Lido")
    var selectedStatus by remember { mutableStateOf(statusOptions[0]) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            coverUri = uri
        }
        showPhotoDialog = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            coverUri = tempCameraUri
        }
        showPhotoDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Livro", fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5DC))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (title.isBlank() || author.isBlank()) {
                        Toast.makeText(context, "Preencha título e autor!", Toast.LENGTH_SHORT).show()
                    } else {
                        val userId = viewModel.currentUser.value?.id ?: 0

                        val newBook = Book(
                            userId = userId,
                            title = title,
                            author = author,
                            description = description,
                            status = selectedStatus,
                            coverUrl = coverUri?.toString()
                        )
                        viewModel.saveBook(newBook)
                        Toast.makeText(context, "Livro salvo!", Toast.LENGTH_SHORT).show()
                        onSaveSuccess()
                    }
                },
                containerColor = Color(0xFF3E2723), contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salvar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .clickable { showPhotoDialog = true }
            ) {
                if (coverUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(coverUri),
                        contentDescription = "Capa do Livro",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.AddPhotoAlternate, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Text("Adicionar Capa", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título do Livro") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Dropdown Status
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedStatus, onValueChange = {}, label = { Text("Status") }, readOnly = true,
                    trailingIcon = { IconButton(onClick = { expanded = true }) {} }, modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { selectedStatus = option; expanded = false })
                    }
                }
            }
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Sinopse (Opcional)") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
        }
    }

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Adicionar Capa") },
            text = { Text("Como você quer adicionar a imagem?") },
            confirmButton = {
                TextButton(onClick = {
                    val uri = createImageFile(context)
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Icon(Icons.Rounded.CameraAlt, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Câmera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    galleryLauncher.launch("image/*")
                }) {
                    Icon(Icons.Rounded.Image, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Galeria")
                }
            }
        )
    }
}

private fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = context.getExternalFilesDir(null)
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}