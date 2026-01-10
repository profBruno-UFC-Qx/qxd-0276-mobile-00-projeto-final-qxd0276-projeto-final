package com.example.bookkeeper.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookkeeper.R
import com.example.bookkeeper.viewmodel.BookViewModel

@Composable
fun LoginScreen(viewModel: BookViewModel) {
    // --- ESTADOS DA TELA ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    // Alterna entre Login e Cadastro
    var isRegistering by remember { mutableStateOf(false) }

    // Controla se a senha está visível ou oculta (os 'pontinhos')
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // --- ESTRUTURA PRINCIPAL (LAYERS) ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // CAMADA 1: SUA IMAGEM DE FUNDO
        // Certifique-se de que o arquivo se chama "background_login" na pasta res/drawable
        Image(
            painter = painterResource(id = R.drawable.background_login),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Preenche a tela toda cortando excessos
            modifier = Modifier.fillMaxSize()
        )

        // CAMADA 2: MÁSCARA ESCURA (GRADIENTE)
        // Isso escurece a imagem para o texto branco/dourado brilhar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f), // Topo um pouco transparente
                            Color.Black.copy(alpha = 0.85f) // Base bem escura (onde estão os campos)
                        )
                    )
                )
        )

        // CAMADA 3: CONTEÚDO (Logo, Campos, Botões)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp), // Margem nas laterais
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // LOGO
            Image(
                painter = painterResource(id = R.drawable.logo_bookkeeper),
                contentDescription = "Logo BookKeeper",
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 16.dp)
            )

            // TÍTULO GRANDE (Estilo GYM FIT)
            Text(
                text = "BOOK\nKEEPER",
                style = MaterialTheme.typography.displayLarge, // Usa a fonte Bellefair configurada no Theme
                color = MaterialTheme.colorScheme.primary, // Cor Bronze/Dourado
                fontWeight = FontWeight.Bold,
                lineHeight = 50.sp, // Altura da linha para juntar o BOOK e o KEEPER
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // --- CAMPOS DE ENTRADA ---

            // Campo Nome (Só aparece se estiver cadastrando)
            if (isRegistering) {
                VintageGlassTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Seu Nome",
                    icon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Campo Email
            VintageGlassTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Senha (com olho para mostrar/esconder)
            VintageGlassTextField(
                value = password,
                onValueChange = { password = it },
                label = "Senha",
                icon = Icons.Default.Lock,
                isPassword = true,
                isPasswordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÃO DE AÇÃO ---
            Button(
                onClick = {
                    if (isRegistering) {
                        // LÓGICA DE CADASTRO
                        if (name.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.register(name, email, password) { success ->
                                if (success) Toast.makeText(context, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(context, "Erro ao cadastrar. Tente outro email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // LÓGICA DE LOGIN
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Preencha email e senha!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.login(email, password) { success ->
                                if (!success) Toast.makeText(context, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Botão alto e imponente
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary // Bronze
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isRegistering) "CADASTRAR" else "ENTRAR",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary // Texto claro
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- RODAPÉ (Trocar entre Login/Cadastro) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isRegistering = !isRegistering }
            ) {
                Text(
                    text = if (isRegistering) "Já tem uma conta? " else "Não tem conta? ",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (isRegistering) "Entrar" else "Registre-se",
                    color = MaterialTheme.colorScheme.primary, // Dourado
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// --- COMPONENTE CUSTOMIZADO: CAMPO "VIDRO" (Vintage Glass) ---
// Este componente cria o visual de fundo escuro transparente
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VintageGlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.White.copy(alpha = 0.5f)) },
        // Ícone da esquerda (Person, Email, etc)
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary // Ícone Dourado
            )
        },
        // Ícone da direita (Olho da senha), só aparece se for senha
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onVisibilityChange) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Alternar visibilidade",
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        } else null,
        // Transforma o texto em bolinhas se for senha e não estiver visível
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            // Fundo Preto Transparente (Efeito Vidro Escuro)
            focusedContainerColor = Color.Black.copy(alpha = 0.5f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),

            // Cores do Texto
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            cursorColor = MaterialTheme.colorScheme.primary,

            // Remove a linha sublinhada padrão
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}