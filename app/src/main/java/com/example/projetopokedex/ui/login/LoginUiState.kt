package com.example.projetopokedex.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val isLoggedIn: Boolean = false,
    val successMessage: String? = null
)
