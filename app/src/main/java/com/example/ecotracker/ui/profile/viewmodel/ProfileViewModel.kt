package com.example.ecotracker.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.local.entity.User
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.utils.hashPassword
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
        email: String,
        dataNascimento: String,
        bio: String,
        password: String? = null
    ) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch

            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                dataNascimento = dataNascimento,
                bio = bio,
                passwordHash = password?.let { hashPassword(it) } ?: currentUser.passwordHash
            )

            userRepository.updateUser(updatedUser)
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
