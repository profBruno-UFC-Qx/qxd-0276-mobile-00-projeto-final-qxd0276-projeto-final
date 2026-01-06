package com.marcos.myspentapp.ui.state

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

data class CardUiState (
    val id: String = UUID.randomUUID().toString(),
    val imageUri: Uri?,
    val title: String,
    val value: Double
)

