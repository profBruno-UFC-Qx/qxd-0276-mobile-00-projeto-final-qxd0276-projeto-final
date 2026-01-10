package com.example.bookkeeper.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bookkeeper.BabyPink
import com.example.bookkeeper.DarkGrey
import com.example.bookkeeper.SoftRose
import com.example.bookkeeper.White


@Composable
fun SaveSessionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    inputValue: String,
    onInputChange: (String) -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sessão Finalizada!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftRose
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Quantas páginas você leu hoje?",
                        color = DarkGrey,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { newText ->
                            if (newText.all { char -> char.isDigit() }) {
                                onInputChange(newText)
                            }
                        },
                        placeholder = { Text("Ex: 15") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BabyPink,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = BabyPink,
                            cursorColor = SoftRose
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Descartar", color = Color.Gray)
                        }
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = BabyPink),
                            shape = RoundedCornerShape(12.dp),
                            enabled = inputValue.isNotEmpty() && (inputValue.toIntOrNull() ?: 0) > 0
                        ) {
                            Text("Salvar", color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}