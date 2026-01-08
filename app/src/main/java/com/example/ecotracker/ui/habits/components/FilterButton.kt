package com.example.ecotracker.ui.habits.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = if (isSelected) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.outlinedButtonColors()
    Button(onClick = onClick, colors = colors) {
        Text(text)
    }
}