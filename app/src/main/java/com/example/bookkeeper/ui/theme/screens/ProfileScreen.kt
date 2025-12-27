package com.example.bookkeeper.ui.theme.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.viewmodel.BookViewModel

@Composable
fun ProfileScreen(
    viewModel: BookViewModel,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var tempBio by remember { mutableStateOf("") }
    var tempEmail by remember { mutableStateOf("") }
    var tempPass by remember { mutableStateOf("") }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(user) {
        tempName = user?.name ?: ""
        tempEmail = user?.email ?: ""
        tempPass = user?.password ?: ""
        tempBio = user?.bio ?: ""
        tempUri = user?.profilePictureUri?.let { Uri.parse(it) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
            }
            tempUri = uri
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF5D4037))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Perfil do Leitor",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(120.dp)
                    .clickable(enabled = isEditing) { photoPickerLauncher.launch("image/*") }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color(0xFFF5F5DC),
                    shadowElevation = 8.dp
                ) {
                    if (tempUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(tempUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = Color(0xFF5D4037)
                            )
                        }
                    }
                }
                if (isEditing) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp).padding(4.dp)
                    ) {
                        Icon(Icons.Rounded.AddAPhoto, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5DC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (!isEditing) {
                        Text(user?.name ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(user?.email ?: "", color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tempBio.ifBlank { "Sem biografia..." }, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    } else {
                        OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Nome") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = tempBio, onValueChange = { tempBio = it }, label = { Text("Bio") })
                        Spacer(modifier = Modifier.height(8.dp))
                        // Campos de email e senha omitidos pra não ficar gigante, mas pode manter
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isEditing) {
                        viewModel.updateUserProfile(
                            tempName, tempBio, tempEmail, tempPass,
                            tempUri?.toString() // Salva a URI como string
                        )
                        isEditing = false
                        Toast.makeText(context, "Perfil Atualizado!", Toast.LENGTH_SHORT).show()
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
            ) {
                Text(if (isEditing) "Salvar Alterações" else "Editar Perfil")
            }

            // Botão Logout
            if (!isEditing) {
                TextButton(onClick = {
                    viewModel.logout()
                    onLogoutClick()
                }) {
                    Text("Sair da conta", color = Color.White.copy(0.7f))
                }
            }
        }
    }
}