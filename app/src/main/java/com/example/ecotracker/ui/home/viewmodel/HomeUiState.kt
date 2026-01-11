package com.example.ecotracker.ui.home.viewmodel

data class HomeUiState(
    val motivationalPhrase: String = "",
    val progress: Float = 0f, // 0.0 a 1.0
    val isLoading: Boolean = false,
    val error: String? = null
)
