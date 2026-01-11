package com.example.ecotracker.ui.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.local.entity.User
import com.example.ecotracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        birth: String,
        bio: String
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val user = User(
                    name = name,
                    email = email,
                    dataNascimento = birth,
                    bio = bio
                )

                userRepository.registerUser(user)

            } catch (e: Exception) {
                _error.value = "Erro ao criar conta"
            } finally {
                _loading.value = false
            }
        }
    }
}
