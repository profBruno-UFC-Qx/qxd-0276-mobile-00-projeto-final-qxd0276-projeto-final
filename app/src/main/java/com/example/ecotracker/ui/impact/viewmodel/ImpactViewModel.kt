package com.example.ecotracker.ui.impact.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ImpactUiState(
    val totalHabitsCompleted: Int = 0,
    val estimatedCo2Saved: Double = 0.0,
    val points: Int = 0
)

class ImpactViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ImpactUiState())
    val uiState: StateFlow<ImpactUiState> = _uiState

    fun loadImpactData() {
        // 🔹 Futuramente:
        // - Buscar hábitos concluídos
        // - Calcular impacto ambiental/social
        _uiState.value = ImpactUiState(
            totalHabitsCompleted = 42,
            estimatedCo2Saved = 12.5,
            points = 180
        )
    }
}
