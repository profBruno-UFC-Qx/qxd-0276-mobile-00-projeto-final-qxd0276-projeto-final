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
    suspend fun getHabitById(habitId: Long): Habit?

    @Query("SELECT * FROM habit WHERE userId = :userId ORDER BY createdAt DESC")
    fun getHabitsByUser(userId: Long): PagingSource<Int, Habit>

    // Lista hábitos Feitos
    @Query(""" SELECT h.*
        FROM habit h
        INNER JOIN habit_completions hc
            ON h.id = hc.habitId
        WHERE h.userId = :userId
          AND hc.date = :date
    """)
    fun getCompletedHabits(userId: Long, date: String): PagingSource<Int, Habit>

    // Lista Hábitos Pendentes
    @Query("""
    SELECT h.*
    FROM habit h
    WHERE h.userId = :userId
      AND h.id NOT IN (
          SELECT habitId
          FROM habit_completions
          WHERE date = :date
      )
""")
    fun getPendingHabits(userId: Long, date: String): PagingSource<Int, Habit>

    // Contabiliza todos os hábitos
    @Query("SELECT COUNT(*) FROM habit WHERE userId = :userId")
    fun countHabits(userId: Long): Flow<Int>
}
