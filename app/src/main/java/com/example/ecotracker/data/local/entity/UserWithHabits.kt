package com.example.ecotracker.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithHabits(
    @Embedded val user: User,
    @Relation(
            parentColumn = "id",
            entityColumn = "userId"
    )
    val habits: List<Habit>
)