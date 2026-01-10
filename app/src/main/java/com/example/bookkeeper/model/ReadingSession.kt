package com.example.bookkeeper.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reading_sessions")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val startTime: Long,
    val endTime: Long,
    val pagesRead: Int,
    val durationSeconds: Long
)