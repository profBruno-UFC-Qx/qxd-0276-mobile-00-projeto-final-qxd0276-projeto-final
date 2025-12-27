package com.example.projetopokedex.ui.signup

data class SignUpUiState(
    val avatar: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false,
    val successMessage: String? = null
)
