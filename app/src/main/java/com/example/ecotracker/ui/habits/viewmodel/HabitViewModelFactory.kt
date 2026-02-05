package com.example.ecotracker.ui.habits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.EcoTrackerApplication

object HabitViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as EcoTrackerApplication

        return HabitViewModel(
            repository = application.container.habitRepository,
            userPreferences = application.container.userPreferences
        ) as T
    }
}