package com.example.ecotracker.ui.habits.viewmodel

data class AddHabitUiState(
    val habitId: Long? = null,
    val name: String = "",
    val description: String = "",
    val locationName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isNameError: Boolean = false,
    val showMapScreen: Boolean = false
)