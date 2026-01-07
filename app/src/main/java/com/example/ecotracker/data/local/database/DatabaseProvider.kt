package com.example.ecotracker.data.local.database

import kotlin.jvm.java
import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var INSTANCE: AppDataBase? = null

    fun getDatabase(context: Context): AppDataBase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "ecotracker_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
