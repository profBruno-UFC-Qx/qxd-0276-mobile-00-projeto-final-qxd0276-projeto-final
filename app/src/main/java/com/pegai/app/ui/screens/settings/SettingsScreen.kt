package com.pegai.app.ui.screens.settings

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pegai.app.model.User
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.theme.PegaíTheme
import com.pegai.app.ui.viewmodel.AuthViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.usuarioLogado.collectAsState()

    if (user == null) {
        GuestPlaceholder(
            title = "Acesse as configurações",
            subtitle = "Faça login para gerenciar preferências e sua conta.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
        return
    }

    SettingsContent(
        user = user!!,
        onBack = { navController.popBackStack() },
        onGoMeusDados = { navController.navigate("meus_dados") },
        onLogout = {
            authViewModel.logout()
            navController.popBackStack() // volta pro perfil e vira "Visitante"
        }
    )
}

@Composable
private fun SettingsContent(
    user: User,
    onBack: () -> Unit,
    onGoMeusDados: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Toggles (front-only)
    var notifPush by rememberSaveable { mutableStateOf(true) }
    var notifChat by rememberSaveable { mutableStateOf(true) }
    var notifAlugueis by rememberSaveable { mutableStateOf(true) }

    // Dialogs
    var showPixDialog by rememberSaveable { mutableStateOf(false) }
    var showTermsDialog by rememberSaveable { mutableStateOf(false) }
    var showPrivacyDialog by rememberSaveable { mutableStateOf(false) }

    // Pix (front-only; você pluga no backend depois)
    var chavePix by rememberSaveable { mutableStateOf(user.chavePix) }
    var tempChavePix by rememberSaveable { mutableStateOf(user.chavePix) }

    val azulLink = Color(0xFF2F80ED)
    val cinzaCard = Color(0xFFF7F7F7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopRightBlob(modifier = Modifier.align(Alignment.TopEnd))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 14.dp, bottom = 28.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            // Voltar
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
                text = "Configurações",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(Modifier.height(20.dp))

            // ===== CONTA =====
            SectionTitle("Conta")
            Spacer(Modifier.height(10.dp))

            SettingNavItem(
                icon = Icons.Default.Person,
                title = "Meus Dados",
                subtitle = "Editar nome, telefone e senha",
                containerColor = cinzaCard,
                onClick = onGoMeusDados
            )

            Spacer(Modifier.height(12.dp))

            SettingNavItem(
                icon = Icons.Default.QrCode,
                title = "Chave PIX",
                subtitle = if (chavePix.isBlank()) "Adicionar chave" else "Editar chave",
                containerColor = cinzaCard,
                onClick = {
                    tempChavePix = chavePix
                    showPixDialog = true
                }
            )

            Spacer(Modifier.height(22.dp))

            // ===== NOTIFICAÇÕES =====
            SectionTitle("Notificações")
            Spacer(Modifier.height(10.dp))

            SettingSwitchItem(
                icon = Icons.Default.Notifications,
                title = "Notificações push",
                subtitle = "Avisos gerais do app",
                checked = notifPush,
                onCheckedChange = { notifPush = it },
                containerColor = cinzaCard
            )

            Spacer(Modifier.height(12.dp))

            SettingSwitchItem(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = "Mensagens do chat",
                subtitle = "Novas mensagens e respostas",
                checked = notifChat,
                onCheckedChange = { notifChat = it },
                containerColor = cinzaCard
            )

            Spacer(Modifier.height(12.dp))

            SettingSwitchItem(
                icon = Icons.Default.Security,
                title = "Aluguéis",
                subtitle = "Solicitações e atualizações",
                checked = notifAlugueis,
                onCheckedChange = { notifAlugueis = it },
                containerColor = cinzaCard
            )

            Spacer(Modifier.height(22.dp))

            // ===== PRIVACIDADE =====
            SectionTitle("Privacidade")
            Spacer(Modifier.height(10.dp))

            SettingNavItem(
                icon = Icons.Default.Description,
                title = "Termos de uso",
                subtitle = "Ver termos",
                containerColor = cinzaCard,
                onClick = { showTermsDialog = true }
            )

            Spacer(Modifier.height(12.dp))

            SettingNavItem(
                icon = Icons.Default.PrivacyTip,
                title = "Política de privacidade",
                subtitle = "Como tratamos seus dados",
                containerColor = cinzaCard,
                onClick = { showPrivacyDialog = true }
            )

            Spacer(Modifier.height(22.dp))

            // ===== SOBRE =====
            SectionTitle("Sobre")
            Spacer(Modifier.height(10.dp))

            SettingInfoItem(
                icon = Icons.Default.Info,
                title = "Versão do app",
                value = "1.0.0",
                containerColor = cinzaCard
            )

            Spacer(Modifier.height(26.dp))

            // ===== SAIR =====
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(10.dp), clip = false),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sair",
                    tint = Color.White
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "SAIR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        // ===== DIALOGS =====
        if (showPixDialog) {
            AlertDialog(
                onDismissRequest = { showPixDialog = false },
                title = { Text("Chave PIX", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Adicione ou edite sua chave. (Front-end por enquanto)",
                            color = Color(0xFF555555),
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        PixTextField(
                            value = tempChavePix,
                            onValueChange = { tempChavePix = it }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            chavePix = tempChavePix.trim()
                            showPixDialog = false
                        }
                    ) { Text("Salvar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showPixDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showTermsDialog) {
            SimpleInfoDialog(
                title = "Termos de uso",
                body = "Ao utilizar este aplicativo, você concorda em manter seus dados corretos e usar a plataforma de forma responsável e respeitosa. Você é responsável pela sua conta, pelas informações que publica e por qualquer ação realizada por ela. É proibido publicar conteúdo ofensivo, enganoso, ilegal ou que viole direitos de terceiros; o app pode remover conteúdos e suspender contas em caso de abuso. As condições de anúncios e aluguéis são definidas pelos usuários, e o aplicativo atua como plataforma de conexão, não garantindo disponibilidade, qualidade ou cumprimento do que foi combinado. Estes termos podem ser atualizados para melhorar a segurança e a experiência no app.",
                onClose = { showTermsDialog = false }
            )
        }

        if (showPrivacyDialog) {
            SimpleInfoDialog(
                title = "Política de privacidade",
                body = "Você concorda em utilizar a plataforma de forma responsável, mantendo seus dados atualizados e respeitando outros usuários. As informações exibidas (anúncios, itens e condições) são fornecidas pelos próprios usuários, e o app pode ajustar ou remover conteúdos que violem regras básicas de convivência, segurança ou legalidade. Para funcionar, podemos coletar dados como nome, e-mail, telefone e informações de uso do aplicativo; caso você permita, também podemos usar localização aproximada para melhorar a experiência. Esses dados são utilizados para autenticação, funcionamento do app, suporte, melhoria de recursos e prevenção de fraudes. Não vendemos seus dados e só compartilhamos o necessário com serviços essenciais (como autenticação/analytics) ou quando exigido por lei. Você pode solicitar atualização ou remoção de dados conforme as opções disponíveis no aplicativo.",
                onClose = { showPrivacyDialog = false }
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
private fun SettingNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(14.dp), clip = false)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(icon)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 13.sp, color = Color(0xFF6A6A6A))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Abrir",
                tint = Color(0xFF9A9A9A)
            )
        }
    }
}

@Composable
private fun SettingSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    containerColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(14.dp), clip = false),
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(icon)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 13.sp, color = Color(0xFF6A6A6A))
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    containerColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(14.dp), clip = false),
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(icon)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(Modifier.height(2.dp))
                Text(value, fontSize = 13.sp, color = Color(0xFF6A6A6A))
            }
        }
    }
}

@Composable
private fun IconBubble(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.size(42.dp),
        shape = CircleShape,
        color = Color(0xFFEAF2FF)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2F80ED)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PixTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text("Ex: email, CPF, telefone...") },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp), clip = false),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF1F1F1),
            unfocusedContainerColor = Color(0xFFF1F1F1),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SimpleInfoDialog(
    title: String,
    body: String,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(body, color = Color(0xFF555555)) },
        confirmButton = {
            Button(onClick = onClose) { Text("Fechar") }
        }
    )
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

/* =========================
   PREVIEW
   ========================= */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsPreview() {
    PegaíTheme {
        SettingsContent(
            user = User(
                uid = "1",
                nome = "Guilherme",
                sobrenome = "B",
                email = "guilhermebvda@alu.ufc.br",
                telefone = "",
                fotoUrl = "",
                chavePix = ""
            ),
            onBack = {},
            onGoMeusDados = {},
            onLogout = {}
        )
    }
}
