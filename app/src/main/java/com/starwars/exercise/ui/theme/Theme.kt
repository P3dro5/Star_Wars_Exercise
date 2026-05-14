package com.starwars.exercise.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = StarWarsYellow,
    onPrimary = StarWarsBlack,
    secondary = StarWarsWhite,
    onSecondary = StarWarsBlack,
    background = StarWarsBlack,
    onBackground = StarWarsWhite,
    surface = StarWarsSurface,
    onSurface = StarWarsOnSurface,
    error = StarWarsError,
    onError = StarWarsWhite
)

private val LightColors = lightColorScheme(
    primary = StarWarsYellow,
    onPrimary = StarWarsBlack,
    secondary = StarWarsBlack,
    onSecondary = StarWarsWhite,
    background = StarWarsWhite,
    onBackground = StarWarsBlack,
    surface = StarWarsSurfaceBright,
    onSurface = StarWarsWhite,
    error = StarWarsError,
    onError = StarWarsWhite
)

@Composable
fun StarWarsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = StarWarsTypography,
        content = content
    )
}
