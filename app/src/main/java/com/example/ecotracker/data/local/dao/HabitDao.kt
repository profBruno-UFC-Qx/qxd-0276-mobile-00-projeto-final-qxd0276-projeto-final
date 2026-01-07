package com.example.ecotracker.data.local.dao

import androidx.room.*

import com.example.ecotracker.data.local.entity.Habit
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingSource

@Dao
interface HabitDao {
    // Crud de Habitos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit:Habit): Int

    @Delete
    suspend fun delete(habit: Habit): Int

    @Query("DELETE FROM habit WHERE id = :habitId")
    suspend fun deleteById(habitId: Long): Int

    @Query("DELETE FROM habit")
    suspend fun deleteAll(): Int


    // Querys

    // listar todos os hábitos
    @Query("SELECT * FROM habit ORDER BY createdAt DESC")
    fun getAllHabits(): PagingSource<Int, Habit>

    // Buscar Hábito por ID
    @Query("SELECT * FROM habit WHERE id = :habitId LIMIT 1")
    fun getHabitById(habitId: Long): Flow<Habit?>

    @Query("SELECT * FROM habit WHERE userId = :userId ORDER BY createdAt DESC")
    fun getHabitsByUser(userId: Long): PagingSource<Int, Habit>

    // Lista hábitos Feitos
    @Query("SELECT * FROM habit WHERE isCompleted = 1")
    fun getCompletedHabits(): PagingSource<Int, Habit>

    // Lista Hábitos Pendentes
    @Query("SELECT * FROM habit WHERE isCompleted = 0")
    fun getPendingHabits(): PagingSource<Int, Habit>

    // Contabiliza todos os hábitos
    @Query("SELECT COUNT(*) FROM habit")
    fun countHabits(): Flow<Int>

    // Contabiliza os Hábitos completos
    @Query("SELECT COUNT(*) FROM habit WHERE isCompleted = 1")
    fun countCompletedHabits(): Flow<Int>
}
