package com.example.ecotracker.ui.home.viewmodel

data class HomeUiState(
    val motivationalPhrase: String = "Carregando Frase do Dia...",
    val progress: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null
)
