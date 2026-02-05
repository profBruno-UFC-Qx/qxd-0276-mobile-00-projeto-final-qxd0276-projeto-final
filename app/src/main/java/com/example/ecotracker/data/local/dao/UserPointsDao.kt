package com.example.ecotracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecotracker.data.local.entity.UserPoints
import kotlinx.coroutines.flow.Flow

@Dao
interface  UserPointsDao {
    @Query("SELECT points FROM user_points WHERE userId = :userId")
    fun getPointsByUser(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(points: UserPoints)

    @Query("""
        UPDATE user_points
        SET points = points + :delta,
            updatedAt = :timestamp
        WHERE userId = :userId
    """)
    suspend fun addPoints(
        userId: Long,
        delta: Int,
        timestamp: Long = System.currentTimeMillis()
    )
}