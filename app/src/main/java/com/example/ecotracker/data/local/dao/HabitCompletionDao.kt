package com.example.ecotracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecotracker.data.local.entity.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun removeCompletion(habitId: Long, date: String)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM habit_completions
            WHERE habitId = :habitId AND date = :date
        )
    """)
    fun isHabitCompleted(habitId: Long, date: String): Flow<Boolean>

    @Query("""
        SELECT COUNT(DISTINCT hc.habitId)
        FROM habit_completions AS hc
        INNER JOIN habit AS h ON hc.habitId = h.id
        WHERE hc.date = :date AND h.userId = :userId
    """)
    fun countCompletedToday(userId: Long, date: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM habit_completions
        WHERE habitId = :habitId
    """)
    fun getCompletionCount(habitId: Long): Flow<Int>
}