package com.pegai.app.ui.viewmodel.profile

import com.pegai.app.model.User

data class ProfileUiState(
    val user: User? = null,

    // Controle do Popup do Pix
    val isPixDialogVisible: Boolean = false,
    val chavePix: String = "",
    val chavePixTemp: String = "",

    // Feedback
    val isLoading: Boolean = false,
    val erro: String? = null,

    // Status
    val avaliacao: String = "4.9 ★",
    val alugueis: String = "0",
    val anuncios: String = "0"
)
