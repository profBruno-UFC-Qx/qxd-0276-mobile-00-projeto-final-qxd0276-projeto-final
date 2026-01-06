package com.marcos.myspentapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    background = colorFundo,
    surface = colorSurface,
    onBackground = colorText,
    onSurface = colorText,
    primary = colorLogo1,
    secondary = colorAccent,
    error = colorNegativo
)

private val DarkColors = darkColorScheme(
    background = colorFundoDark,
    surface = colorSurfaceDark,
    onBackground = colorTextDark,
    onSurface = colorTextDark,
    primary = colorPrimaryDark,
    secondary = colorAccentDark,
    error = colorNegativoDark
)

@Composable
fun MySpentAppTheme(
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors =
        if (isDarkMode) DarkColors
        else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
