package com.example.ecotracker.di

import android.content.Context
import com.example.ecotracker.data.local.database.DatabaseProvider
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.datastore.UserPreferences
import com.example.ecotracker.data.repository.UserRepository

class AppContainer(context: Context) {

    private val appContext = context.applicationContext
    private val database = DatabaseProvider.getDatabase(context)

    private val habitDao = database.habitDao()
    private val userDao = database.userDao()
    // DataStore
    val userPreferences = UserPreferences(appContext)

    val habitRepository = HabitRepository(habitDao)
    val userRepository = UserRepository(userDao, userPreferences)
}