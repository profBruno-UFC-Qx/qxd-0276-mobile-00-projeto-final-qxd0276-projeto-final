package com.example.ecotracker

import android.app.Application
import com.example.ecotracker.data.di.AppContainer

class EcoTrackerApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer(this)
    }
}