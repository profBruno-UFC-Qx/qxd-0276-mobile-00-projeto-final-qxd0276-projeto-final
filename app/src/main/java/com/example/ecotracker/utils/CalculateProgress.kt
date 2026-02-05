package com.example.ecotracker.utils

fun calculateProgress(points: Int): Float {
    val (min, max) = when {
        points < 50 -> 0 to 50
        points < 100 -> 50 to 100
        points < 150 -> 100 to 150
        else -> 150 to 150
    }
    return ((points - min).toFloat() / (max - min)).coerceIn(0f, 1f)
}