package com.example.ecotracker.ui.map.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.habits.viewmodel.mapViewModel.MapSelectionViewModelFactory
import com.example.ecotracker.ui.map.viewmodel.MapSelectionViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

data class SelectedPlace(
    val name: String?,
    val address: String?,
    val latLng: LatLng?
)

@Composable
fun MapSelectionScreen(
    viewModel: MapSelectionViewModel = viewModel(),
    onPlaceSelected: (SelectedPlace) -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraPositionState = rememberCameraPositionState()

    SnackbarHost(hostState = snackbarHostState)

    // Move a câmera quando um local for selecionado
    LaunchedEffect(uiState.selectedLatLng) {
        uiState.selectedLatLng?.let {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(it, 15f)

            onPlaceSelected(
                SelectedPlace(
                    name = uiState.placeName,
                    address = null,
                    latLng = it
                )
            )
        }
    }

    // Exibe erros
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            viewModel.onMapClicked(latLng)
        }
    ) {
        uiState.selectedLatLng?.let {
            Marker(
                state = MarkerState(it),
                title = uiState.placeName
            )
        }
    }

}