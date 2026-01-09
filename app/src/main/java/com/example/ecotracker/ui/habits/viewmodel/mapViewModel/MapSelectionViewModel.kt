package com.example.ecotracker.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ecotracker.ui.habits.viewmodel.mapViewModel.MapSelectionUiState
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapSelectionViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(MapSelectionUiState())
    val uiState: StateFlow<MapSelectionUiState> = _uiState.asStateFlow()

    // Ao clicar diretamente no mapa
    fun onMapClicked(latLng: LatLng) {
        _uiState.value = MapSelectionUiState(
            selectedLatLng = latLng,
            placeName = "Local selecionado no mapa"
        )
    }

    // ao digitar o local no campo de texto
    fun onPlaceSelected(
        name: String?,
        latLng: LatLng?
    ) {
        if (latLng == null) {
            _uiState.value = MapSelectionUiState(
                errorMessage = "Local inválido"
            )
            return
        }

        _uiState.value = MapSelectionUiState(
            selectedLatLng = latLng,
            placeName = name
        )
    }

    // Setar o estado
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
