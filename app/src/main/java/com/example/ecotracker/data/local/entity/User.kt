package com.example.ecotracker.data.local.entity

import android.provider.ContactsContract
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val dataNascimento: String,
    val email: String,
    val bio: String,
    val createdAt: Long = System.currentTimeMillis()
)