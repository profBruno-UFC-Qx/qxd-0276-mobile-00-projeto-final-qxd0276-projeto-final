package com.pegai.app.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.R
import com.pegai.app.ui.viewmodel.register.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState.erro != null) {
            Toast.makeText(context, uiState.erro, Toast.LENGTH_LONG).show()
        }
        if (uiState.cadastroSucesso) {
            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
            navController.navigate(route = "home")
        }
    }

    val VerdePrincipal = Color(0xFF88D888)
    val CinzaBorda = Color(0xFFD3D3D3)
    val CinzaTexto = Color(0xFF5A5A5A)
    val CinzaInputBackground = Color(0xFFF5F5F5)
    val gradienteBotao = Brush.linearGradient(
        colors = listOf(Color(0xFFAAD87A), Color(0xFF88D888), Color(0xFF66E0B8)),
        start = Offset.Zero, end = Offset.Infinite
    )
    val corDesabilitado = Color(0xFFBFC4CE)
    val larguraInputs = 350.dp
    val alturaComponente = 60.dp

    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    var isFocusedNome by remember { mutableStateOf(false) }
    var isFocusedSNome by remember { mutableStateOf(false) }
    var isFocusedEmail by remember { mutableStateOf(false) }
    var isFocusedTel by remember { mutableStateOf(false) }
    var isFocusedSenha by remember { mutableStateOf(false) }
    var isFocusedConfSenha by remember { mutableStateOf(false) }

    val camposValidos = nome.isNotBlank() &&
            sobrenome.isNotBlank() &&
            email.isNotBlank() &&
            telefone.isNotBlank() &&
            senha.length >= 6 &&
            senha == confirmarSenha

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.blobs1), contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(180.dp).height(180.dp).align(Alignment.TopStart).offset(y = 30.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.blobs2), contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(110.dp).height(110.dp).align(Alignment.BottomEnd).offset(y = 25.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .shadow(elevation = 4.dp, shape = RectangleShape)
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = CinzaTexto)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Criar Conta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CinzaTexto)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier.width(larguraInputs).padding(bottom = 30.dp, top = 20.dp)
                ) {
                    Text("    Vamos começar,", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("           Criando seu perfil!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // --- Campo Nome ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Nome",
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = "Ex: João",
                    isFocused = isFocusedNome,
                    onFocusChange = { isFocusedNome = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Sobrenome ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Sobrenome",
                    value = sobrenome,
                    onValueChange = { sobrenome = it },
                    placeholder = "Ex: Silva",
                    isFocused = isFocusedSNome,
                    onFocusChange = { isFocusedSNome = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Email ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Ex: exemplo@email.com",
                    keyboardType = KeyboardType.Email,
                    isFocused = isFocusedEmail,
                    onFocusChange = { isFocusedEmail = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Telefone ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Telefone",
                    value = telefone,
                    onValueChange = { telefone = it },
                    placeholder = "Ex: (88) 99999-9999",
                    keyboardType = KeyboardType.Phone,
                    isFocused = isFocusedTel,
                    onFocusChange = { isFocusedTel = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                //  --- LINHA DE SENHAS (Lado a Lado) ---
                Row(
                    modifier = Modifier.width(larguraInputs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Senha
                    InputItem(
                        modifier = Modifier.weight(1f),
                        label = "Senha",
                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = "******",
                        isPassword = true,
                        isFocused = isFocusedSenha,
                        onFocusChange = { isFocusedSenha = it },
                        verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                    )

                    Spacer(modifier = Modifier.width(12.dp)) // Espacinho entre os dois campos

                    // confirmação de senha
                    InputItem(
                        modifier = Modifier.weight(1f),
                        label = "Confirmar Senha",
                        value = confirmarSenha,
                        onValueChange = { confirmarSenha = it },
                        placeholder = "******",
                        isPassword = true,
                        imeAction = ImeAction.Done,
                        isFocused = isFocusedConfSenha,
                        onFocusChange = { isFocusedConfSenha = it },
                        verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                    )
                }

                if (senha.isNotEmpty() && confirmarSenha.isNotEmpty() && senha != confirmarSenha) {
                    Text("As senhas não coincidem", color = Color.Red, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .width(larguraInputs)
                        .height(48.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            if (camposValidos) gradienteBotao else SolidColor(corDesabilitado)
                        )
                        .clickable(enabled = camposValidos && !uiState.isLoading) {
                            viewModel.cadastrarUsuario(
                                nome = nome,
                                sobrenome = sobrenome,
                                email = email,
                                senha = senha,
                                telefone = telefone,
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Criar Conta",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
@Composable
fun InputItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    verdePrincipal: Color,
    cinzaBorda: Color,
    cinzaBg: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false
) {
    val CinzaTexto = Color(0xFF5A5A5A)
    val alturaComponente = 60.dp

    Column(modifier = modifier) {
        Text(label, color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            modifier = Modifier
                .fillMaxWidth()
                .height(alturaComponente)
                .onFocusChanged { onFocusChange(it.isFocused) }
                .border(2.dp, if (isFocused) verdePrincipal else cinzaBorda, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = cinzaBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}