package com.example.ecotracker.ui.register.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.register.viewmodel.RegisterViewModel
import com.example.ecotracker.ui.register.viewmodel.RegisterViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory),
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Criar Conta") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Insira seus dados",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 24.sp
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Nome") },
                isError = uiState.isNameError,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.isNameError) {
                Text(
                    text = "Nome é obrigatório",
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                isError = uiState.isNameError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.isEmailError) {
                Text(
                    text = "Email inválido",
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.isPasswordError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (uiState.isPasswordError) {
                Text(
                    text = "Senha deve ter no mínimo 6 caracteres",
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = uiState.bio,
                onValueChange = { viewModel.onBioChange(it) },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.register(onSuccess = onRegisterSuccess) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.loading
            ) {
                Text(if (uiState.loading) "Criando conta..." else "Cadastrar")
            }

            TextButton(
                onClick = onGoToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Já tenho uma conta")
            }
        }
    }
}
