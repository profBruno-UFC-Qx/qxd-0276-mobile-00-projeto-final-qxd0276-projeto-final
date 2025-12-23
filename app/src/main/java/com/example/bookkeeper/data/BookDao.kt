package com.example.bookkeeper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bookkeeper.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY title ASC")
    fun getBooksByUser(userId: String): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(book: Book)

    @Delete
    suspend fun delete(book: Book)
}