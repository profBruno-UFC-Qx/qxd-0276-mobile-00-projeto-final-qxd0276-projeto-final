package com.example.ecotracker.ui.impact.viewmodel

import com.example.ecotracker.data.local.entity.HabitMapItem
import com.google.android.gms.maps.model.LatLng

data class ImpactUiState(
    val totalHabitsCompleted: Int = 0,
    val points: Int = 0,
    val habitsLocations: List<HabitMapItem> = emptyList()
)