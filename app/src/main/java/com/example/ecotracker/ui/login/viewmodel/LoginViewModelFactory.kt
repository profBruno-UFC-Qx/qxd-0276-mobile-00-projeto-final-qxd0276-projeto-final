package com.example.ecotracker.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ecotracker.EcoTrackerApplication
import com.example.ecotracker.data.datastore.UserPreferences
import com.example.ecotracker.data.repository.UserRepository

object LoginViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {

        if (!modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel")
        }

        val application =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as EcoTrackerApplication

        return LoginViewModel(
            userRepository = application.container.userRepository
        ) as T
    }
}
