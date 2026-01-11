package com.example.ecotracker.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun login(onSuccess: () -> Unit) {
        _loading.value = true
        _error.value = null

        // 🔹 Simulação (depois você liga com API / Firebase)
        if (email.value == "admin@eco.com" && password.value == "123456") {
            _loading.value = false
            onSuccess()
        } else {
            _loading.value = false
            _error.value = "Email ou senha inválidos"
        }
    }
}
