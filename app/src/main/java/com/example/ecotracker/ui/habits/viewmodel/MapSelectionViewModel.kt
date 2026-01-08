package com.example.ecotracker.ui.habits.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import android.content.Context
import kotlinx.coroutines.flow.asStateFlow

class MapSelectionViewModel(
    private val locationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _deviceLocation = MutableStateFlow<LatLng?>(null)
    val deviceLocation = _deviceLocation.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchDeviceLocation(context: Context) {
        try {
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    _deviceLocation.value =
                        LatLng(location.latitude, location.longitude)
                }
            }
        } catch (e: SecurityException) {
            _error.value = "Permissão de localização negada"
        }
    }
}
