package com.example.ecotracker.utils

fun hashPassword(password: String): String {
    return password.hashCode().toString()
}