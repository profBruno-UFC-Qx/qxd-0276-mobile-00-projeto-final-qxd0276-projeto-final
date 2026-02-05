package com.example.ecotracker.ui.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.datastore.UserPreferences
import com.example.ecotracker.data.local.entity.User
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.utils.hashPassword
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _deleteState = MutableStateFlow(false)
    val deleteState: StateFlow<Boolean> = _deleteState

    val uiState: StateFlow<ProfileUiState> =
        userRepository.getLoggedUserPreference()
            .filterNotNull()
            .map{ it.id }
            .flatMapLatest{ userId ->
                combine(
                    userRepository.getUserById(userId),
                    userRepository.getUserPoints(userId)
                ){ user, points ->
                    ProfileUiState(
                        user = user,
                        points = points
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = ProfileUiState()
            )

    fun updateUser(
        name: String,
        email: String,
        bio: String,
        password: String? = null
    ) {
        viewModelScope.launch {
            val currentUser = uiState.value.user ?: return@launch

            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                bio = bio,
                passwordHash = password?.let { hashPassword(it) } ?: currentUser.passwordHash
            )

            userRepository.updateUser(updatedUser)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
    fun deleteAccount(userId: Long){
        if(userId <= 0L) return

        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                _deleteState.value = true
            } catch (e: Exception) {
                _deleteState.value = false
            }
        }
    }

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}
