package com.pegai.app.ui.viewmodel.profile

data class ProfileUiState(
    // Controle do Popup do Pix
    val isPixDialogVisible: Boolean = false,
    val chavePix: String = "",
    val chavePixTemp: String = "", // O que o usuário está digitando antes de salvar

    // Feedback
    val isLoading: Boolean = false,
    val erro: String? = null
)