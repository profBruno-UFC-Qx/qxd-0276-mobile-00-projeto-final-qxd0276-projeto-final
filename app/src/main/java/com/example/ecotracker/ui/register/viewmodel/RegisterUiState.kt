package com.example.ecotracker.ui.register.viewmodel

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val bio: String = "",
    val isNameError: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isBioError: Boolean = false,
    val loading: Boolean = false,
    val generalError: String? = null
)