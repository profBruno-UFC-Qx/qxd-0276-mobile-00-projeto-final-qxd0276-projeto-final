package com.example.ecotracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme
val LightColorScheme = lightColorScheme(
    primary = MainLightText,
    onPrimary = Color.White,

    secondary = HighlightedLightText,
    onSecondary = Color.White,

    tertiary = DetailLightText,
    onTertiary = HighlightedLightText,

    background = LigthBackground,
    onBackground = HighlightedLightText,

    surface = SuperficialGreen,
    onSurface = HighlightedLightText,

    surfaceVariant = SuperficialGreen.copy(alpha = 0.85f),
    onSurfaceVariant = HighlightedLightText,

    primaryContainer = SuperficialGreen,
    onPrimaryContainer = HighlightedLightText
)

// Dark Theme
val DarkColorScheme = darkColorScheme(
    primary = HighlightedText,
    onPrimary = Color.Black,

    secondary = DetailText,
    onSecondary = Color.Black,

    tertiary = MainText,
    onTertiary = Color.Black,

    background = BlueBackground,
    onBackground = MainText,

    surface = SuperficialBlue,
    onSurface = MainText,

    surfaceVariant = SuperficialBlue.copy(alpha = 0.85f),
    onSurfaceVariant = MainText,

    primaryContainer = SuperficialBlue,
    onPrimaryContainer = MainText
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