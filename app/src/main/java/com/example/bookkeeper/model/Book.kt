package com.example.bookkeeper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // O dono do livro

    val title: String,
    val author: String,
    val description: String = "",
    val pageCount: Int = 0,
    val coverUrl: String? = null,

    val status: String = "Quero Ler",
    val rating: Int = 0,
    val startDate: Long? = null,
    val finishDate: Long? = null,

    val coverColorHex: Long = 0xFF4E342E,

    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val review: String = ""
)