package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGamingColorScheme = lightColorScheme(
    primary = RoyalPurple,
    onPrimary = PureWhite,
    primaryContainer = ElectricIndigo.copy(alpha = 0.1f),
    onPrimaryContainer = RoyalPurple,
    secondary = GoldenYellow,
    onSecondary = DarkCharcoal,
    tertiary = GemCyan,
    background = PureWhite,
    onBackground = DarkCharcoal,
    surface = PureWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = SurfaceCardVariant,
    onSurfaceVariant = DarkCharcoal,
    outline = LightBorder,
    error = CrimsonRed
)

@Composable
fun WebGamingTheme(
    darkTheme: Boolean = false, // Forced Light Theme per requirement
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightGamingColorScheme,
        typography = Typography,
        content = content
    )
}
