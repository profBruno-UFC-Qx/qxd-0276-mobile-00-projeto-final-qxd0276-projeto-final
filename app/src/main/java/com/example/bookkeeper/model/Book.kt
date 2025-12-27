package com.example.bookkeeper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val title: String,
    val author: String,
    val description: String = "",
    val coverUrl: String? = null,
    val status: String = "Quero Ler",
    val isFavorite: Boolean = false,
    val coverColorHex: Long = 0xFF4E342E
)