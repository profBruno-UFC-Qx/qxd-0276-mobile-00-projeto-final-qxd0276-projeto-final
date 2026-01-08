package com.example.ecotracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.ecotracker.data.local.dao.HabitDao
import com.example.ecotracker.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

sealed class HabitError(message: String) : Exception(message) {
    object DatabaseError : HabitError("Erro no banco de Dados")
    object HabitNotFound : HabitError("Hábito não encontrado")
}
class HabitRepository(
    private val habitDao: HabitDao
) {
    // Crud
    suspend fun insertHabit(habit: Habit): Result<Unit> {
        return try{
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
    suspend fun deleteHabit(habit: Habit): Result<Unit> {
        return try{
            // return diferente de 0 indica sucesso na operação
            val row = habitDao.delete(habit)

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

    suspend fun deleteAllHabits(): Result<Unit> {
        return try{
            // return diferente de 0 indica sucesso na operação
            val rows = habitDao.deleteAll()

            if (rows == 0){
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
    fun getAllHabits(): Flow<PagingData<Habit>> {
            return Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { habitDao.getAllHabits() }
            ).flow
    }

    fun getHabitById(id: Long): Flow<Habit?> {
        return habitDao.getHabitById(id)
            .catch { exception ->
                emit(null)
            }
    }

    fun getHabitsByUser(userId: Long): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                habitDao.getHabitsByUser(userId)
            }
        ).flow
    }

    fun getCompletedHabits(): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { habitDao.getCompletedHabits() }
        ).flow
    }

    fun getPendingHabits(): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { habitDao.getPendingHabits() }
        ).flow
    }

    fun countHabits(): Flow<Int> {
        return habitDao.countHabits()
    }

    fun countCompletedHabits(): Flow<Int> {
        return habitDao.countCompletedHabits()
    }

}