package com.example.projetopokedex.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetopokedex.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.isLoggedIn.collectLatest { logged ->
                _uiState.value = _uiState.value.copy(isLoggedIn = logged)
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login() {
        val state = _uiState.value

        // validação simples de campos vazios
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(
                error = "Preencha e-mail e senha",
                successMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            val result = userRepository.login(
                email = state.email,
                password = state.password
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    token = result.getOrNull(),
                    error = null,
                    isLoggedIn = true,
                    successMessage = "Login efetuado com sucesso!"
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Erro ao fazer login",
                    successMessage = null,
                    isLoggedIn = false
                )
            }
        }
    }

    fun clearCredentials() {
        _uiState.value = _uiState.value.copy(
            email = "",
            password = "",
            error = null,
            successMessage = null
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }
}