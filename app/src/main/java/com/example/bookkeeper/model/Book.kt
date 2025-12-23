package com.example.bookkeeper.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val author: String,
    val description: String = "",
    val coverUrl: String? = null,
    val status: String = "Quero Ler",
    val isFavorite: Boolean = false,
    val coverColorHex: Long = 0xFF4E342E
)