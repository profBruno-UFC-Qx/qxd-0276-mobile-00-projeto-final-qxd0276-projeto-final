package com.example.ecotracker.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {

    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}