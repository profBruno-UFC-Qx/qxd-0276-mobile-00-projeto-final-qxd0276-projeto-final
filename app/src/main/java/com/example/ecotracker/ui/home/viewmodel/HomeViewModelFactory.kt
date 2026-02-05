package com.example.ecotracker.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.EcoTrackerApplication
import com.example.ecotracker.data.repository.QuoteRepository
import com.example.ecotracker.ui.profile.viewmodel.ProfileViewModel

object HomeViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val application =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as EcoTrackerApplication

        return HomeViewModel(
            userRepository = application.container.userRepository,
        ) as T
    }
}
