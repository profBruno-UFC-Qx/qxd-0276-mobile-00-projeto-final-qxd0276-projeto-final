package com.example.bookkeeper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val totalPages: Int,
    val currentPage: Int = 0,
    val status: String,
    val review: String = "",
    val userNotes: String = "",
    val userId: Int,
    val coverUrl: String? = null,
    val coverColorHex: Long? = null
)