package com.example.ecotracker.ui.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.datastore.UserPreferences
import com.example.ecotracker.data.datastore.UserSession
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.utils.hashPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

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
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try{
                val user = userRepository
                    .getUserByEmail(email.value)
                    .first()

                if (user == null) {
                    _error.value = "Usuário não encontrado"
                    _loading.value = false
                    return@launch
                }

                val passwordHash = hashPassword(password.value)

                if (user.passwordHash != passwordHash) {
                    _error.value = "Email ou senha inválidos"
                    _loading.value = false
                    return@launch
                }

                // salva a sessão no datastore
                Log.d("LOGIN", "Salvando usuário no DataStore: $user")
                userRepository.saveLoggedUser(
                    UserSession(id = user.id,
                        name = user.name,
                        email = user.email)
                )

                _loading.value = false
                onSuccess()
            }catch (e: Exception){
                _error.value = "Erro ao realizar login"
            }finally {
                _loading.value = false
            }
        }
    }
}
