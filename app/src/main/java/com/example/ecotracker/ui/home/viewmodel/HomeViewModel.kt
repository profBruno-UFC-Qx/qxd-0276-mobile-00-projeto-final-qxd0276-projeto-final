package com.example.ecotracker.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            motivationalPhrase = "Cada pequeno hábito constrói um futuro melhor 🌱",
            progress = 0.4f
        )
    )
    val uiState = _uiState.asStateFlow()

    fun refreshPhrase() {
        // Futuro: API
        _uiState.value = _uiState.value.copy(
            motivationalPhrase = "Persistência transforma ações em resultados."
        )
    }
}

