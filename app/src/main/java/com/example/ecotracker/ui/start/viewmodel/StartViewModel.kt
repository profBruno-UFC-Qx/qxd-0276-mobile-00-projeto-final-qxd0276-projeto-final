package com.example.ecotracker.ui.start.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StartViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private var _onUserLogged: (() -> Unit)? = null
    fun setOnUserLogged(callback: () -> Unit) {
        _onUserLogged = callback
    }

    init {
        checkLoggedUser()
    }

    private fun checkLoggedUser() {
        viewModelScope.launch {
            userRepository.getLoggedUserPreference().collect { user ->
                _loading.value = false
                if (user != null) {
                    _onUserLogged?.invoke()
                }
            }
        }
    }
}
