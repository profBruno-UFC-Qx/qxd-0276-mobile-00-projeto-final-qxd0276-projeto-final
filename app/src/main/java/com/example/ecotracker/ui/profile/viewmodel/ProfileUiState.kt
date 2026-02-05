package com.example.ecotracker.ui.profile.viewmodel

import com.example.ecotracker.data.local.entity.User

data class ProfileUiState(
    val user: User? = null,
    val points: Int = 0
)