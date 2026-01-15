package com.example.ecotracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ecotracker.data.local.dao.HabitCompletionDao
import com.example.ecotracker.data.local.dao.HabitDao
import com.example.ecotracker.data.local.dao.UserDao
import com.example.ecotracker.data.local.dao.UserPointsDao
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.data.local.entity.User
import com.example.ecotracker.data.local.entity.HabitCompletion
import com.example.ecotracker.data.local.entity.UserPoints

@Database(
    entities = [Habit::class, User:: class, HabitCompletion:: class, UserPoints::class],
    version = 4,
    exportSchema = false
)

abstract class AppDataBase : RoomDatabase(){
    abstract fun habitDao(): HabitDao
    abstract fun userDao(): UserDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun userPointsDao(): UserPointsDao
}