package com.example.ecotracker.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.local.entity.User
import com.example.ecotracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUser() {
        viewModelScope.launch {
            userRepository.getLoggedUser().collectLatest { loggedUser ->
                _user.value = loggedUser
            }
        }
    }
    fun updateUser(
        name: String,
        dataNascimento: String,
        email:String,
        bio: String
    ){
        val currentUser = _user.value ?: return
        viewModelScope.launch{
            try {
                val updatedUser = currentUser.copy(
                    name = name,
                    dataNascimento = dataNascimento,
                    email = email,
                    bio = bio
                )

                userRepository.updateUser(updatedUser)
                _user.value = updatedUser

            } catch (e: Exception) {
                _error.value = "Erro ao atualizar perfil"
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            userRepository.logOutUser()
        }
    }

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}
