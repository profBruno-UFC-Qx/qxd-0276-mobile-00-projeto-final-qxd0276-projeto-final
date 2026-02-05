package com.example.ecotracker.ui.habits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val selectedLatLng: LatLng? = null,
    val placeName: String? = null,
    val errorMessage: String? = null
)

class MapSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun onMapClicked(latLng: LatLng, name: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedLatLng = latLng,
                placeName = name ?: "Local Selecionado"
            )
        }
    }

    fun clearError() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }

    fun showError(message: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(errorMessage = message)
        }
    }

    fun setInitialLocation(latLng: LatLng, name: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedLatLng = latLng,
                placeName = name
            )
        }
    }
}
