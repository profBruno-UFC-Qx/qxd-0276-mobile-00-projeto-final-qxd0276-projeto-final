package com.example.ecotracker.ui.register.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.local.entity.User
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.utils.hashPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    // Atualizações de campos
    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, isNameError = false)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, isEmailError = false)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, isPasswordError = false)
    }

    fun onBioChange(value: String) {
        _uiState.value = _uiState.value.copy(bio = value, isBioError = false)
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validação básica
        val passwordError = state.password.length < 6

        var hasError = false
        _uiState.value = state.copy(
            isNameError = state.name.isBlank().also { if (it) hasError = true },
            isEmailError = state.email.isBlank().also { if (it) hasError = true },
            isPasswordError = passwordError,
            isBioError = state.bio.isBlank().also { if (it) hasError = true },
            generalError = null
        )

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, generalError = null)

            try {
                val passwordHash = hashPassword(state.password)

                val user = User(
                    name = state.name,
                    email = state.email,
                    passwordHash = passwordHash,
                    bio = state.bio
                )
                val existingUser = userRepository.getUserByEmail(state.email).first()
                if(existingUser != null) {
                    _uiState.value = _uiState.value.copy(
                        generalError = "Email já registrado",
                        loading = false
                    )
                    return@launch
                }
                val result = userRepository.registerUser(user)
                Log.d("RegisterViewModel", "Resultado do registro: $result")

                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        generalError = "Erro ao criar conta",
                        loading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    generalError = "Erro inesperado ao criar conta",
                    loading = false
                )
            }
        }
    }
    fun resetForm() {
        _uiState.value = RegisterUiState()
    }
}
