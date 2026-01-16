package com.example.ecotracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Theme
val LightColorScheme = lightColorScheme(
    primary = BluePrimaryContainerDark,
    onPrimary = Color.White,
    tertiary = Teal,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondaryContainer = BluePrimaryContainer,
    onSecondaryContainer = BluePrimary,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    onSurface = Color.Black
)

// Dark Theme
val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    secondary = GreenDark,
    tertiary = BluePrimaryContainerDark,
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = Color.White,
    onSecondary = Color.White,
    secondaryContainer = BluePrimaryContainerDark,
    onSecondaryContainer = Teal,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSurface = Color.Black
)


@Composable
fun EcoTrackerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme){
        DarkColorScheme
    }else{
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}