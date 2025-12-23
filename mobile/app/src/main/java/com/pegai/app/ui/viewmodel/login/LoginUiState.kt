package com.pegai.app.ui.viewmodel.login
import com.pegai.app.model.User

data class LoginUiState(
    val isLoading: Boolean = false,
    val erro: String? = null,
    val loginSucesso: Boolean = false,
    val usuario: User? = null
)