package com.example.bookkeeper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bookkeeper.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY id DESC")
    fun getBooksForUser(userId: Int): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE status = :status")
    fun getBooksByStatus(status: String): Flow<List<Book>>


    @Query("SELECT COUNT(*) FROM books WHERE status = 'Lido'")
    fun getFinishedBooksCount(): Flow<Int>
}