package com.pegai.app.ui.viewmodel.register

data class RegisterUiState(
    val isLoading: Boolean = false,
    val erro: String? = null,
    val cadastroSucesso: Boolean = false
)