package com.example.ecotracker.data.di

import android.content.Context
import com.example.ecotracker.data.local.database.DatabaseProvider
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.repository.UserRepository
import com.google.android.libraries.places.api.Places

class AppContainer(context: Context) {

    private val database = DatabaseProvider.getDatabase(context)

    private val habitDao = database.habitDao()
    private val userDao = database.userDao()

    val habitRepository = HabitRepository(habitDao)
    val userRepository = UserRepository(userDao)
}
