package com.marcos.myspentapp

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.marcos.myspentapp.ui.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val user = userViewModel.userState
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Scaffold {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var itensChecked by remember { mutableStateOf(false) }

            Image(
                painter = painterResource(id = R.drawable.ms1),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp),
                alignment = Alignment.TopCenter
            )
            Text(
                text = "Bem-vindo de volta!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF827E7D)
            )

            Spacer(Modifier.height(70.dp))

            if(itensChecked) {
                Text(
                    text = "Informações Incorretas",
                    fontSize = 14.sp,
                    color = Color.Red
                )
            }

            Spacer(Modifier.height(16.dp))

            // CAMPO EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                keyboardOptions = KeyboardOptions.Default.copy( imeAction = ImeAction.Next ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // CAMPO SENHA
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if(user.email == email && user.senha == senha) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        itensChecked = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Entrar", fontSize = 18.sp, color = Color.White)
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.7f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        navController.navigate("register")
                    }
                ) {
                    Text(
                        "Esqueceu a senha?",
                        color = Color(0xFF827E7D),
                        fontSize = 14.sp
                    )
                }

                TextButton(
                    onClick = {
                        navController.navigate("register")
                    }
                ) {
                    Text(
                        "Criar conta",
                        color = Color(0xFF827E7D),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val user = userViewModel.userState

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    var erroMsg by remember { mutableStateOf("") }

    Scaffold {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.ms1),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp),
                alignment = Alignment.TopCenter
            )

            Text(
                text = "Criar sua conta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF827E7D)
            )

            Spacer(Modifier.height(50.dp))

            if (erroMsg.isNotEmpty()) {
                Text(
                    text = erroMsg,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    erroMsg = ""
                },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = {
                    senha = it
                    erroMsg = ""
                },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password,
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = {
                    confirmarSenha = it
                    erroMsg = ""
                },
                label = { Text("Confirmar senha") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {

                    if (email.isBlank() || senha.isBlank() || confirmarSenha.isBlank()) {
                        erroMsg = "Preencha todos os campos"
                        return@Button
                    }

                    if (!email.contains("@") || !email.contains(".com")) {
                        erroMsg = "E-mail inválido"
                        return@Button
                    }

                    if (senha.length < 6) {
                        erroMsg = "A senha deve ter no mínimo 6 caracteres"
                        return@Button
                    }

                    if (senha != confirmarSenha) {
                        erroMsg = "As senhas devem ser iguais"
                        return@Button
                    }

                    if (user.email == email) {
                        erroMsg = "E-mail já registrado"
                        return@Button
                    }

                    userViewModel.updateEmail(email)
                    userViewModel.updateSenha(senha)

                    // Avançar para parte 2 do registro
                    navController.navigate("register2") {
                        popUpTo("register") { inclusive = true }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clickable(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF494241)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Registrar", fontSize = 18.sp, color = Color.White)
            }

            Spacer(Modifier.height(4.dp))

            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            ) {
                Text(
                    "Já possui conta? Entrar",
                    color = Color(0xFF827E7D),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenPart2(
    navController: NavController,
    userViewModel: UserViewModel,
    fotoUri: Uri? = null
) {

    var fotoUri by remember { mutableStateOf(fotoUri) }
    var codeRescue by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    var erroMsg by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Launcher para abrir galeria
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        fotoUri = uri
    }

    val bitmap: ImageBitmap? = remember(fotoUri) {
        fotoUri?.let { uri ->
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        .asImageBitmap()
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source).asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Adicionar foto",
                        tint = Color.Gray,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (erroMsg.isNotEmpty()) {
                Text(
                    text = erroMsg,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = userName,
                onValueChange = {
                    userName = it
                    erroMsg = ""
                },
                label = { Text("Nome de usuário") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = codeRescue,
                onValueChange = {
                    codeRescue = it
                    erroMsg = ""
                },
                label = { Text("Código de recuperação") },
                modifier = Modifier.fillMaxWidth(0.7f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(2.dp))

            Text(
                "* Necessário caso esqueça a senha",
                color = Color(0xFF827E7D),
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // BOTÃO Criar Conta
            Button(
                onClick = {

                    if (codeRescue.isBlank() || userName.isBlank()) {
                        erroMsg = "Preencha todos os campos"
                        return@Button
                    }
                    if (codeRescue.length < 6) {
                        erroMsg = "A senha deve ter no mínimo 6 caracteres"
                        return@Button
                    }

                    // CRIA A CONTA
                    userViewModel.updatePhoto(fotoUri)
                    userViewModel.updateCode(codeRescue)
                    userViewModel.updateName(userName)
                    navController.navigate("main")
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Registrar", fontSize = 18.sp, color = Color.White)
            }

            Spacer(Modifier.height(4.dp))

            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            ) {
                Text(
                    "Já possui conta? Entrar",
                    color = Color(0xFF827E7D),
                    fontSize = 14.sp
                )
            }
        }
    }
}


