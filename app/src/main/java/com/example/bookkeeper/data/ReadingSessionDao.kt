package com.example.bookkeeper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookkeeper.model.ReadingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {

    @Insert
    suspend fun insertSession(session: ReadingSession)

    @Query("SELECT * FROM reading_sessions WHERE bookId = :bookId ORDER BY startTime DESC")
    fun getSessionsForBook(bookId: Int): Flow<List<ReadingSession>>

    @Query("SELECT AVG(durationSeconds / pagesRead) FROM reading_sessions WHERE bookId = :bookId AND pagesRead > 0")
    fun getAverageSecondsPerPage(bookId: Int): Flow<Float?>
}