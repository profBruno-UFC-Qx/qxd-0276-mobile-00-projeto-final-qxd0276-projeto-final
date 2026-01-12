package com.example.ecotracker

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.example.ecotracker.di.AppContainer

class EcoTrackerApplication : Application() {

    val container: AppContainer by lazy {
        AppContainer(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                BuildConfig.MAPS_API_KEY
            )
        }
    }
}
