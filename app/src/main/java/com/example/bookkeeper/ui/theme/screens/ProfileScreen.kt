package com.example.bookkeeper.ui.theme.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.bookkeeper.viewmodel.BookViewModel

@Composable
fun ProfileScreen(
    viewModel: BookViewModel,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var tempBio by remember { mutableStateOf("") }
    var tempEmail by remember { mutableStateOf("") }
    var tempPass by remember { mutableStateOf("") }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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
            try { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) { }
            tempUri = uri
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir Conta?", color = MaterialTheme.colorScheme.primary) },
            text = { Text("Tem certeza? Todos os seus livros e anotações serão apagados permanentemente.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; viewModel.deleteAccount { Toast.makeText(context, "Conta excluída.", Toast.LENGTH_LONG).show(); onLogoutClick() } },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sim, Excluir") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(top = 48.dp, start = 16.dp).align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Voltar", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .size(130.dp)
                            .clickable(enabled = isEditing) { photoPickerLauncher.launch("image/*") }
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(4.dp, MaterialTheme.colorScheme.surface)
                        ) {
                            if (tempUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(tempUri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
                                    Icon(Icons.Rounded.Person, null, modifier = Modifier.size(70.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        if (isEditing) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp).padding(4.dp)) {
                                Icon(Icons.Rounded.AddAPhoto, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEditing) {
                        Text(user?.name ?: "Leitor", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f))
                    } else {
                        Text("Editando Perfil...", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                if (!isEditing && tempBio.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Biografia", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Text(tempBio, style = MaterialTheme.typography.bodyMedium, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }

                if (isEditing) {
                    VintageOutlinedTextField(tempName, { tempName = it }, "Nome", Icons.Rounded.Person)
                    Spacer(Modifier.height(12.dp))
                    VintageOutlinedTextField(tempBio, { tempBio = it }, "Bio", Icons.Rounded.FormatQuote)
                    Spacer(Modifier.height(12.dp))
                    VintageOutlinedTextField(tempEmail, { tempEmail = it }, "Email", Icons.Rounded.Email, keyboardType = KeyboardType.Email)
                    Spacer(Modifier.height(12.dp))
                    VintageOutlinedTextField(tempPass, { tempPass = it }, "Senha", Icons.Rounded.Lock, isPassword = true)
                    Spacer(Modifier.height(24.dp))
                }

                Button(
                    onClick = {
                        if (isEditing) {
                            viewModel.updateUserProfile(tempName, tempBio, tempEmail, tempPass, tempUri?.toString())
                            isEditing = false
                            Toast.makeText(context, "Perfil Atualizado!", Toast.LENGTH_SHORT).show()
                        } else { isEditing = true }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(if (isEditing) Icons.Rounded.Check else Icons.Rounded.Edit, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isEditing) "Salvar Alterações" else "Editar Perfil")
                }

                if (!isEditing) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        onClick = { viewModel.toggleTheme() },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (isDark) Icons.Rounded.DarkMode else Icons.Rounded.LightMode, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(16.dp))
                                Text("Modo Escuro")
                            }
                            Switch(checked = isDark, onCheckedChange = { viewModel.toggleTheme() })
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.logout(); onLogoutClick() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Logout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sair da conta")
                    }

                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.DeleteForever, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Excluir conta permanentemente")
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VintageOutlinedTextField(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isPassword: Boolean = false, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}