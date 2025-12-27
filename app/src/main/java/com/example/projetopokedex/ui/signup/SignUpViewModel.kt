package com.example.projetopokedex.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetopokedex.data.repository.UserRepository
import com.example.projetopokedex.data.model.UserLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onAvatarChange(value: String) {
        _uiState.value = _uiState.value.copy(avatar = value)
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun onRegistrationConsumed() {
        _uiState.value = _uiState.value.copy(
            isRegistered = false,
            successMessage = null
        )
    }

    fun register() {
        val state = _uiState.value

        // validações
        if (state.avatar.isBlank() ||
            state.name.isBlank() ||
            state.email.isBlank() ||
            state.password.isBlank()
        ) {
            _uiState.value = state.copy(
                error = "Preencha todos os campos",
                successMessage = null
            )
            return
        }

        if (!state.email.endsWith("@gmail.com")) {
            _uiState.value = state.copy(
                error = "O e-mail deve terminar com @gmail.com",
                successMessage = null
            )
            return
        }

        if (state.password.length < 8) {
            _uiState.value = state.copy(
                error = "A senha deve ter pelo menos 8 caracteres",
                successMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null, successMessage = null)
            val user = UserLocal(
                avatar = state.avatar,
                name = state.name,
                email = state.email,
                password = state.password
            )
            val result = userRepository.register(user)
            _uiState.value = if (result.isSuccess) {
                state.copy(
                    isLoading = false,
                    isRegistered = true,
                    error = null,
                    successMessage = "Usuário cadastrado com sucesso!"
                )
            } else {
                state.copy(
                    isLoading = false,
                    isRegistered = false,
                    error = result.exceptionOrNull()?.message ?: "Erro ao cadastrar",
                    successMessage = null
                )
            }
        }
    }
}