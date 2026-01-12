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

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun register(
        name: String,
        email: String,
        password: String,
        birth: String,
        bio: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val passwordHash = hashPassword(password)

                val user = User(
                    name = name,
                    email = email,
                    passwordHash = passwordHash,
                    dataNascimento = birth,
                    bio = bio,
                    logged = true // logado apos cadastro
                )
                val existingUser = userRepository.getUserByEmail(email).first()
                if(existingUser != null) {
                    _error.value = "Email já cadastrado"
                    return@launch
                }
                val result = userRepository.registerUser(user)
                Log.e("RegisterViewModel", "Resultado do registro: $result")
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _error.value = "Erro ao criar conta"
                }

            } catch (e: Exception) {
                _error.value = "Erro inesperado ao criar conta"
            } finally {
                _loading.value = false
            }
        }
    }
}
