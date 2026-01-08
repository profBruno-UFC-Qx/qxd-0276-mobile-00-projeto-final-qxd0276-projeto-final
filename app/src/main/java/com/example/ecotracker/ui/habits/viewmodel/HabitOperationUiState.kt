package com.example.ecotracker.ui.habits.viewmodel

sealed class HabitOperationUiState {
    object Idle : HabitOperationUiState()
    object Success : HabitOperationUiState()
    object Loading : HabitOperationUiState()
    object HabitAdded : HabitOperationUiState()
    object HabitUpdated : HabitOperationUiState()
    object HabitDeleted : HabitOperationUiState()
    data class Error(val msg: String) : HabitOperationUiState()
}