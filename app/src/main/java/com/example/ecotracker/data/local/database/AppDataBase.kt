package com.example.ecotracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecotracker.data.local.dao.HabitDao
import com.example.ecotracker.data.local.dao.UserDao
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.data.local.entity.User

@Database(
    entities = [Habit::class, User:: class],
    version = 1,
    exportSchema = false
)

abstract class AppDataBase : RoomDatabase(){
    abstract fun habitDao(): HabitDao
    abstract fun userDao(): UserDao
}