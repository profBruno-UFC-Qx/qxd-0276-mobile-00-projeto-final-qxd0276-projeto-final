package com.example.ecotracker.ui.impact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.EcoTrackerApplication
import com.example.ecotracker.ui.profile.viewmodel.ProfileViewModel

object ImpactViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as EcoTrackerApplication

        return ImpactViewModel(
            habitRepository = application.container.habitRepository,
            userRepository = application.container.userRepository
        ) as T
    }
}