package com.rgcastrof.trustcam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val wasLocationEnabled: Boolean = false
)