package com.example.ecotracker.ui.habits.viewmodel.mapViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.ui.map.viewmodel.MapSelectionViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

object MapSelectionViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application = extras[
            ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
        ] ?: throw IllegalStateException("Application is required")

        if (modelClass.isAssignableFrom(MapSelectionViewModel::class.java)) {
            val placesClient: PlacesClient =
                Places.createClient(application)

            return MapSelectionViewModel(
                placesClient = placesClient
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

