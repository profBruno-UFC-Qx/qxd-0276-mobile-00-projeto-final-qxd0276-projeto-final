package com.example.bookkeeper.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bookkeeper.BabyPink
import com.example.bookkeeper.DarkGrey
import com.example.bookkeeper.LightPinkBg
import com.example.bookkeeper.SoftRose
import com.example.bookkeeper.White
import com.example.bookkeeper.model.Book
import com.example.bookkeeper.ui.theme.components.SaveSessionDialog
import com.example.bookkeeper.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSessionScreen(
    viewModel: BookViewModel,
    onMenuClick: () -> Unit
) {
    val activeBook by viewModel.activeBookForSession.collectAsState()
    val books by viewModel.books.collectAsState()

    // Estados do Cronômetro
    val elapsedTime by viewModel.elapsedTimeSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val showSessionDialog by viewModel.showSaveSessionDialog.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sessão de Leitura", fontFamily = FontFamily.Serif, color = SoftRose, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Rounded.Menu, null, tint = BabyPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (activeBook == null) {
                // ESTADO 1: SELECIONAR LIVRO
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "O que vamos ler hoje?",
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        color = DarkGrey,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(books.filter { it.status == "Lendo" || it.status == "Quero Ler" }) { book ->
                            BookSelectionCard(book) { viewModel.setActiveBookForSession(book) }
                        }
                    }
                }
            } else {
                // ESTADO 2: CRONÔMETRO ATIVO
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Botão para fechar/trocar livro (só se o timer estiver parado)
                    if (!isTimerRunning && elapsedTime == 0L) {
                        IconButton(
                            onClick = { viewModel.setActiveBookForSession(null) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Rounded.Close, "Trocar livro", tint = Color.Gray)
                        }
                    }

                    // Capa do Livro
                    Card(
                        elevation = CardDefaults.cardElevation(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(200.dp).width(140.dp)
                    ) {
                        if (activeBook!!.coverUrl != null) {
                            AsyncImage(model = activeBook!!.coverUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(Modifier.fillMaxSize().background(BabyPink), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Book, null, tint = White, modifier = Modifier.size(50.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(activeBook!!.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, textAlign = TextAlign.Center, color = DarkGrey)
                    Text("Página atual: ${activeBook!!.currentPage}", color = Color.Gray, fontSize = 14.sp)

                    Spacer(Modifier.height(40.dp))

                    // Mostrador do Tempo
                    val hours = elapsedTime / 3600
                    val minutes = (elapsedTime % 3600) / 60
                    val seconds = elapsedTime % 60
                    val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                    Text(
                        text = timeString,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Light,
                        color = SoftRose,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(Modifier.height(40.dp))

                    // Controles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause
                        Button(
                            onClick = { viewModel.toggleTimer() },
                            shape = CircleShape,
                            contentPadding = PaddingValues(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BabyPink),
                            modifier = Modifier.size(80.dp).shadow(8.dp, CircleShape)
                        ) {
                            Icon(
                                if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                null,
                                modifier = Modifier.size(40.dp),
                                tint = White
                            )
                        }

                        // Stop (Salvar) - A CORREÇÃO ESTÁ AQUI
                        if (elapsedTime > 0) {
                            Button(
                                onClick = { viewModel.finishSession() }, // <--- AGORA CHAMA A FUNÇÃO CERTA
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = LightPinkBg),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Icon(Icons.Rounded.Stop, null, tint = SoftRose, modifier = Modifier.size(30.dp))
                            }
                        }
                    }
                }
            }
        }

        // Dialog de Salvar
        SaveSessionDialog(
            show = showSessionDialog,
            onDismiss = { viewModel.dismissSessionDialog() },
            onConfirm = {
                activeBook?.let { viewModel.confirmSaveSession(it.id) }
            },
            inputValue = viewModel.pagesReadInput,
            onInputChange = { viewModel.pagesReadInput = it }
        )
    }
}

@Composable
fun BookSelectionCard(book: Book, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = LightPinkBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(shape = RoundedCornerShape(4.dp), modifier = Modifier.width(40.dp).fillMaxHeight()) {
                if(book.coverUrl != null) AsyncImage(model = book.coverUrl, null, contentScale = ContentScale.Crop)
                else Box(Modifier.fillMaxSize().background(BabyPink))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(book.title, fontWeight = FontWeight.Bold, color = DarkGrey)
                Text(book.author, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Rounded.PlayArrow, null, tint = SoftRose)
        }
    }
}