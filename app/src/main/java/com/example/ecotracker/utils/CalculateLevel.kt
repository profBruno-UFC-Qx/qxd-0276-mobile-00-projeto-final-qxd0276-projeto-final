package com.example.ecotracker.utils

fun calculateLevel(points: Int): Int{
    return when{
        points < 50 -> 0
        points < 100 -> 1
        points < 150 -> 2
        else -> 3
    }
}