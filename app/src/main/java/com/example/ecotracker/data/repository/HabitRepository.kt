package com.example.ecotracker.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ecotracker.data.local.dao.HabitCompletionDao
import com.example.ecotracker.data.local.dao.HabitDao
import com.example.ecotracker.data.local.dao.UserPointsDao
import com.example.ecotracker.data.local.entity.Habit
import com.example.ecotracker.data.local.entity.HabitCompletion
import com.example.ecotracker.data.local.entity.UserPoints
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

sealed class HabitError(message: String) : Exception(message) {
    object DatabaseError : HabitError("Erro no banco de Dados")
    object HabitNotFound : HabitError("Hábito não encontrado")
}
class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
    private val userPointsDao: UserPointsDao
) {
    // Crud
    suspend fun insertHabit(habit: Habit): Result<Unit> {
        return try{
            Log.d("HabitDebug", "Salvando hábito: $habit")
            habitDao.insert(habit)
            Result.success(Unit)
        }
        catch (e: Exception){
            Result.failure(HabitError.DatabaseError)
        }
    }
    suspend fun updateHabit(habit: Habit): Result<Unit> {
        return try{
            val row = habitDao.update(habit)
            // return diferente de 0 indica sucesso na operação
            if(row == 0){
                Result.failure(HabitError.HabitNotFound)
            } else {
                Result.success(Unit)
            }
        }
        catch(e: Exception){
            Result.failure(HabitError.DatabaseError)
        }
    }
    suspend fun deleteHabitById(habitId: Long): Result<Unit> {
        return try{
            // return diferente de 0 indica sucesso na operação
            val row = habitDao.deleteById(habitId)

            if (row == 0){
                Result.failure(HabitError.HabitNotFound)
            } else {
                Result.success(Unit)
            }
        }
        catch (e: Exception){
            Result.failure(HabitError.DatabaseError)
        }
    }

    // Querys
    fun getHabitsByUser(userId: Long): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                habitDao.getHabitsByUser(userId)
            }
        ).flow
    }
    suspend fun getHabitById(habitId: Long): Habit?{
        return habitDao.getHabitById(habitId)
    }


    suspend fun getCompletedHabitsWithLocationByUser(userId: Long, ): Flow<List<Habit>> {
        return completionDao.getCompletedHabitsWithLocationByUser(userId)
    }

    fun getCompletedHabits(userId: Long, date: String): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { habitDao.getCompletedHabits(userId, date) }
        ).flow
    }

    fun getPendingHabits(userId: Long, date: String): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { habitDao.getPendingHabits(userId, date) }
        ).flow
    }

    fun countHabits(userId: Long): Flow<Int> {
        return habitDao.countHabits(userId)
    }

    fun countCompletedToday(userId: Long, date: String): Flow<Int> {
        return completionDao.countCompletedToday(userId, date)
    }
    fun countAllCompletedHabits(userId: Long): Flow<Int> {
        return completionDao.countAllCompletedHabits(userId)
    }

    suspend fun toggleHabitCompletion(userId: Long, isCompleted:Boolean, habitId: Long, date: String) {
        if (isCompleted) {
            completionDao.removeCompletion(habitId, date)
            userPointsDao.addPoints(userId, delta = -10)
        } else {
            completionDao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    date = date
                )
            )
            userPointsDao.addPoints(userId, delta = +10)
        }
    }

    fun isHabitCompleted(habitId: Long, date: String): Flow<Boolean> {
        return completionDao.isHabitCompleted(habitId, date)
    }
}