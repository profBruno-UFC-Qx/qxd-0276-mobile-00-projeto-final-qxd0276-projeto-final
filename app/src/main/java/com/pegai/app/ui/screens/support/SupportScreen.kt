package com.pegai.app.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pegai.app.model.User
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.theme.PegaíTheme
import com.pegai.app.ui.viewmodel.AuthViewModel

@Composable
fun SupportScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.usuarioLogado.collectAsState()

    if (user == null) {
        GuestPlaceholder(
            title = "Acesse o suporte",
            subtitle = "Faça login para visualizar ajuda e entrar em contato.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
        return
    }

    SupportContent(
        user = user!!,
        onBack = { navController.popBackStack() }
    )
}

@Composable
private fun SupportContent(
    user: User,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Dialogs
    var showFaq by rememberSaveable { mutableStateOf(false) }
    var showHowItWorks by rememberSaveable { mutableStateOf(false) }
    var showSafety by rememberSaveable { mutableStateOf(false) }
    var showContact by rememberSaveable { mutableStateOf(false) }
    var showReport by rememberSaveable { mutableStateOf(false) }
    var showReportSent by rememberSaveable { mutableStateOf(false) }

    // Report form (front-only)
    var reportSubject by rememberSaveable { mutableStateOf("") }
    var reportDescription by rememberSaveable { mutableStateOf("") }

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
                text = "Ajuda e Suporte",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(Modifier.height(20.dp))

            // ===== AJUDA =====
            SectionTitle("Ajuda")
            Spacer(Modifier.height(10.dp))

            SettingNavItem(
                icon = Icons.Default.HelpOutline,
                title = "Perguntas frequentes (FAQ)",
                subtitle = "Dúvidas rápidas e respostas",
                containerColor = cinzaCard,
                onClick = { showFaq = true }
            )

            Spacer(Modifier.height(12.dp))

            SettingNavItem(
                icon = Icons.Default.Info,
                title = "Como funciona o app?",
                subtitle = "Passo a passo do básico",
                containerColor = cinzaCard,
                onClick = { showHowItWorks = true }
            )

            Spacer(Modifier.height(12.dp))

            SettingNavItem(
                icon = Icons.Default.Security,
                title = "Segurança",
                subtitle = "Dicas para alugar com tranquilidade",
                containerColor = cinzaCard,
                onClick = { showSafety = true }
            )

            Spacer(Modifier.height(22.dp))

            // ===== CONTATO =====
            SectionTitle("Contato")
            Spacer(Modifier.height(10.dp))

            SettingNavItem(
                icon = Icons.Default.SupportAgent,
                title = "Fale conosco",
                subtitle = "Canais de atendimento",
                containerColor = cinzaCard,
                onClick = { showContact = true }
            )

            Spacer(Modifier.height(12.dp))

            SettingNavItem(
                icon = Icons.Default.BugReport,
                title = "Reportar um problema",
                subtitle = "Conte o que aconteceu",
                containerColor = cinzaCard,
                onClick = { showReport = true }
            )
        }

        // ===== DIALOGS =====
        if (showFaq) {
            SimpleInfoDialog(
                title = "FAQ",
                body =
                    "• Como alugar um item?\n" +
                            "Escolha um item e combine pelo chat.\n\n" +
                            "• Como anuncio um item?\n" +
                            "Vá em Anunciar e preencha as informações.\n\n" +
                            "• Como funciona o chat?\n" +
                            "Use para combinar detalhes com segurança.\n\n" +
                            "• Posso editar meus dados?\n" +
                            "Sim, em Perfil > Meus Dados.\n\n" +
                            "• Tive um problema.\n" +
                            "Use “Reportar um problema” nesta tela.",
                onClose = { showFaq = false }
            )
        }

        if (showHowItWorks) {
            SimpleInfoDialog(
                title = "Como funciona",
                body =
                    "O app conecta pessoas que querem anunciar e alugar itens.\n\n" +
                            "1) Encontre um item\n" +
                            "2) Converse no chat para combinar\n" +
                            "3) Defina valores, datas e entrega/retirada\n" +
                            "4) Finalize com segurança",
                onClose = { showHowItWorks = false }
            )
        }

        if (showSafety) {
            SimpleInfoDialog(
                title = "Segurança",
                body =
                    "• Combine em locais públicos quando possível.\n" +
                            "• Confira o item antes de fechar.\n" +
                            "• Evite compartilhar dados sensíveis.\n" +
                            "• Use o chat para registrar acordos.\n" +
                            "• Se algo parecer suspeito, reporte.",
                onClose = { showSafety = false }
            )
        }

        if (showContact) {
            AlertDialog(
                onDismissRequest = { showContact = false },
                title = { Text("Fale conosco", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Canais (ilustrativo):",
                            color = Color(0xFF555555)
                        )
                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF2F80ED))
                            Spacer(Modifier.width(8.dp))
                            Text("suporte@pegai.com")
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Horário: seg–sex, 9h–18h",
                            color = Color(0xFF6A6A6A),
                            fontSize = 13.sp
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showContact = false }) { Text("Fechar") }
                }
            )
        }

        if (showReport) {
            AlertDialog(
                onDismissRequest = { showReport = false },
                title = { Text("Reportar um problema", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Descreva o problema. (Front-end por enquanto)",
                            color = Color(0xFF555555),
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = reportSubject,
                            onValueChange = { reportSubject = it },
                            singleLine = true,
                            label = { Text("Assunto") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = reportDescription,
                            onValueChange = { reportDescription = it },
                            label = { Text("Descrição") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // front-only: “envia” e reseta
                            showReport = false
                            showReportSent = true
                            reportSubject = ""
                            reportDescription = ""
                        }
                    ) { Text("Enviar") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showReport = false }) { Text("Cancelar") }
                }
            )
        }

        if (showReportSent) {
            SimpleInfoDialog(
                title = "Recebido!",
                body = "Obrigado! Seu reporte foi registrado (ilustrativo).",
                onClose = { showReportSent = false }
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
        confirmButton = { Button(onClick = onClose) { Text("Fechar") } }
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
private fun SupportPreview() {
    PegaíTheme {
        SupportContent(
            user = User(
                uid = "1",
                nome = "Karine",
                sobrenome = "Ennalian",
                email = "karine@ufc.br",
                telefone = "",
                fotoUrl = "",
                chavePix = ""
            ),
            onBack = {}
        )
    }
}
