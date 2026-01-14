package com.example.ecotracker.ui.map.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecotracker.ui.habits.viewmodel.MapSelectionViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition

data class  SelectedPlace(
    val name: String?,
    val latLng: LatLng
)
@SuppressLint("MissingPermission")
@Composable
fun MapSelectionScreen(
    viewModel: MapSelectionViewModel = viewModel(),
    onPlaceSelected: (SelectedPlace) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Estado da permissão
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Solicitar permissão runtime
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    // Estado da camera
    val cameraPositionState = rememberCameraPositionState()

    // Centraliza a câmera
    LaunchedEffect(hasLocationPermission) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val target = if (location != null) {
                    LatLng(location.latitude, location.longitude)
                } else {
                    LatLng(-23.5505, -46.6333) // Centraliza em São Paulo
                }
                cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
            }.addOnFailureListener {
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(LatLng(-23.5505, -46.6333), 4f)
            }
        } else {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(-23.5505, -46.6333), 4f)
        }
    }
    // Local selecionado
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->

            selectedLatLng = latLng

            val placeName = "Local selecionado"
            onPlaceSelected(
                SelectedPlace(
                    name = placeName,
                    latLng = latLng
                )
            )
        },
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission
        ),
    ){
        // marca o ponto selecionado
        selectedLatLng?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Local selecionado"
            )
        }
    }
}