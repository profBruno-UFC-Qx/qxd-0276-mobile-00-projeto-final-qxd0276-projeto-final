package com.example.bookkeeper.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookkeeper.R
import com.example.bookkeeper.viewmodel.BookViewModel

@Composable
fun LoginScreen(viewModel: BookViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var isRegistering by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_bookkeeper),
            contentDescription = "Logo BookKeeper",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = if (isRegistering) "Novo Leitor" else "Bem-vindo ao BookKeeper",
            fontSize = 32.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isRegistering) {
            VintageTextField(value = name, onValueChange = { name = it }, label = "Seu Nome")
            Spacer(modifier = Modifier.height(16.dp))
        }

        VintageTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(16.dp))

        VintageTextField(
            value = password,
            onValueChange = { password = it },
            label = "Senha",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isRegistering) {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Preencha todos os campos para cadastrar!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.register(name, email, password) { success ->
                            if (success) {
                                // Se cadastrou com sucesso, o MainActivity vai detectar o currentUser e mudar a tela
                                Toast.makeText(context, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro ao cadastrar. Tente outro email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                // LÓGICA DE LOGIN
                else {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Preencha email e senha!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.login(email, password) { success ->
                            if (!success) {
                                Toast.makeText(context, "Email ou senha inválidos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (isRegistering) "Assinar Livro de Registros" else "Abrir Biblioteca",
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRegistering) "Já tem cadastro? Entre aqui." else "Primeira vez? Cadastre-se.",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                isRegistering = !isRegistering

            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VintageTextField(value: String, onValueChange: (String) -> Unit, label: String, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if(isPassword) KeyboardType.Password else KeyboardType.Text),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}