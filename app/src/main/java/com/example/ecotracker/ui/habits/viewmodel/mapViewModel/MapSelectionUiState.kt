package com.example.ecotracker.ui.habits.viewmodel.mapViewModel

import com.google.android.gms.maps.model.LatLng

data class MapSelectionUiState(
    val isLoading: Boolean = false,
    val selectedLatLng: LatLng? = null,
    val placeName: String? = null,
    val errorMessage: String? = null
)
