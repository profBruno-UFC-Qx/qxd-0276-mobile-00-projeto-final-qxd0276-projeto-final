package com.example.ecotracker.data.local.database

import kotlin.jvm.java
import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDataBase? = null

    fun getDatabase(context: Context): AppDataBase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "eco_tracker_db"
            ).fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
}
