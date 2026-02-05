package com.example.ecotracker.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.EcoTrackerApplication

object ThemeViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as EcoTrackerApplication

        return ThemeViewModel(
            userPreferences = application.container.userPreferences
        ) as T
    }
}