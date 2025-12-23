package com.example.bookkeeper.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkAcademiaScheme = lightColorScheme(
    primary = LeatherBrown,
    onPrimary = Color.White,
    secondary = GoldAccent,
    onSecondary = LeatherBrown,
    background = OldPaper,
    surface = OldPaper,
    onBackground = InkBlack,
    onSurface = InkBlack,
)

@Composable
fun BookKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkAcademiaScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}