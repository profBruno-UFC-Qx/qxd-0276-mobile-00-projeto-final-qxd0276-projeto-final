package com.example.ecotracker.data.local.dao

import android.R
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecotracker.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Crud User
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User): Int

    @Delete
    suspend fun deleteUser(user: User): Int

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserByID(userId: Long): Int

    @Query("DELETE FROM users")
    suspend fun logOut(): Int

    // Querys
    @Query("SELECT * FROM users")
    fun getLoggedUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE LOWER(name) LIKE '%'|| LOWER(:name) || '%'")
    fun getUserByName(name: String): PagingSource<Int, User>

    @Query("SELECT * FROM users WHERE email = :userEmail")
    fun getUserByEmail(userEmail: String): Flow<User?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): PagingSource<Int, User>

}