package com.example.bookkeeper.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReadingTimer(
    elapsedSeconds: Long,
    isRunning: Boolean,
    onToggle: () -> Unit
) {
    val formattedTime = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isRunning) Color(0xFFE8F5E9) else Color(0xFFFFF3E0) // Verde se rodando, Creme se parado
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(text = "Sessão de Leitura", style = MaterialTheme.typography.titleMedium)

            Text(
                text = formattedTime,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isRunning) Color(0xFF2E7D32) else Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color.Red else Color(0xFF3E2723)
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = if (isRunning) "PARAR" else "COMEÇAR")
            }
        }
    }
}

@Composable
fun SaveSessionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    inputValue: String,
    onInputChange: (String) -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Sessão Finalizada! 🎉") },
            text = {
                Column {
                    Text("Quantas páginas você leu agora?")
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { if (it.all { char -> char.isDigit() }) onInputChange(it) },
                        label = { Text("Páginas lidas") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = onConfirm) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    }
}