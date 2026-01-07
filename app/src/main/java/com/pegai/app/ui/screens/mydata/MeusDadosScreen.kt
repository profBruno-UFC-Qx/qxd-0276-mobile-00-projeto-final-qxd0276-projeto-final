package com.pegai.app.ui.screens.mydata

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.theme.PegaíTheme
import com.pegai.app.ui.viewmodel.AuthViewModel

@Composable
fun MeusDadosScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.usuarioLogado.collectAsState()

    if (user == null) {
        GuestPlaceholder(
            title = "Acesse seus dados",
            subtitle = "Faça login para visualizar e editar suas informações.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
        return
    }

    MeusDadosContent(
        onBack = { navController.popBackStack() },
        initialNome = user?.nome.orEmpty(),
        initialSobrenome = user?.sobrenome.orEmpty(),
        initialEmail = user?.email.orEmpty(),
        initialTelefone = user?.telefone.orEmpty(),
        onSave = {
            // FRONT-ONLY por enquanto
        }
    )
}

@Composable
private fun MeusDadosContent(
    onBack: () -> Unit,
    initialNome: String,
    initialSobrenome: String,
    initialEmail: String,
    initialTelefone: String,
    onSave: () -> Unit
) {
    var nome by rememberSaveable(initialEmail) { mutableStateOf(initialNome) }
    var sobrenome by rememberSaveable(initialEmail) { mutableStateOf(initialSobrenome) }
    val email by rememberSaveable(initialEmail) { mutableStateOf(initialEmail) }

    var telefone by rememberSaveable(initialEmail) { mutableStateOf(initialTelefone) }

    var confirmacaoSenha by rememberSaveable(initialEmail) { mutableStateOf("") }
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val azul = Color(0xFF2F80ED)
    val azulLink = Color(0xFF2F80ED)
    val cinzaField = Color(0xFFF1F1F1)
    val cinzaDisabled = Color(0xFFBDBDBD)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopRightBlob(
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 14.dp, bottom = 220.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = Color(0xFFEAF2FF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = azulLink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Voltar",
                    color = azulLink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(26.dp))

            Text(
                text = "Informações",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(Modifier.height(28.dp))

            // Nome / Sobrenome
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                LabeledField(
                    label = "Nome",
                    modifier = Modifier.weight(1f)
                ) {
                    PegaiField(
                        value = nome,
                        onValueChange = { nome = it },
                        placeholder = "",
                        containerColor = cinzaField
                    )
                }

                LabeledField(
                    label = "Sobrenome",
                    modifier = Modifier.weight(1f)
                ) {
                    PegaiField(
                        value = sobrenome,
                        onValueChange = { sobrenome = it },
                        placeholder = "",
                        containerColor = cinzaField
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Email (disabled)
            LabeledField(label = "Endereço de Email") {
                PegaiField(
                    value = email,
                    onValueChange = { },
                    placeholder = "",
                    enabled = false,
                    containerColor = cinzaField,
                    disabledContainerColor = cinzaDisabled
                )
            }

            Spacer(Modifier.height(24.dp))

            // Telefone
            LabeledField(label = "Número de Telefone") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountryCodeBox(
                        text = "(+55)",
                        modifier = Modifier.width(70.dp)
                    )

                    PegaiField(
                        value = telefone,
                        onValueChange = { telefone = it },
                        placeholder = "(88)98888-8888",
                        containerColor = cinzaField,
                        keyboardType = KeyboardType.Phone,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Barra inferior (senha + botão fixos)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .padding(bottom = 18.dp, top = 10.dp)
                .navigationBarsPadding(),
        ) {
            LabeledField(label = "Confirmação de Senha") {
                PegaiField(
                    value = confirmacaoSenha,
                    onValueChange = { confirmacaoSenha = it },
                    placeholder = "",
                    containerColor = cinzaField,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    trailing = {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/ocultar senha",
                            tint = Color(0xFF7A7A7A),
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { senhaVisivel = !senhaVisivel }
                        )
                    }
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azul)
            ) {
                Text(
                    text = "SALVAR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PegaiField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    containerColor: Color,
    disabledContainerColor: Color = containerColor,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable (() -> Unit))? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(text = placeholder, color = Color(0xFFB1B1B1))
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp), clip = false),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailing,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF2F80ED)
        )
    )
}

@Composable
private fun CountryCodeBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(54.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp), clip = false),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F1F1)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = Color(0xFFB1B1B1),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun TopRightBlob(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .offset(x = 80.dp, y = (-60).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFF3A5), Color(0xFFFFD68A))
                    )
                )
        )
        Box(
            modifier = Modifier
                .offset(x = 130.dp, y = (-20).dp)
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFF0A8), Color(0xFFFFD08A))
                    )
                )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MeusDadosPreview() {
    PegaíTheme {
        MeusDadosContent(
            onBack = {},
            initialNome = "Guilherme",
            initialSobrenome = "Barros",
            initialEmail = "Gui@ufc.br",
            initialTelefone = "",
            onSave = {}
        )
    }
}
