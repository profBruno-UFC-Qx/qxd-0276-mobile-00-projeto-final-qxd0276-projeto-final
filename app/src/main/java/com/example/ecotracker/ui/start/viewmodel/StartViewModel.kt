package com.example.ecotracker.ui.start.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class StartDestination{
    object Loading : StartDestination()
    object Login : StartDestination()
    object Home : StartDestination()
}

class StartViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<StartDestination>(StartDestination.Loading)
    val destination: StateFlow<StartDestination> = _destination

    init {
        viewModelScope.launch {
            userRepository.getLoggedUserPreference().collect{ session ->
                if(session != null){
                    _destination.value = StartDestination.Home
                } else{
                    _destination.value = StartDestination.Login
                }
            }
        }
    }
}
