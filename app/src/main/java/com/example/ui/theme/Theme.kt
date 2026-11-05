package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClassicNeonColorScheme =
  darkColorScheme(
    primary = ArcadePrimary,
    secondary = ArcadeSecondary,
    tertiary = ArcadeTertiary,
    background = ArcadeBackground,
    surface = ArcadeSurface,
    surfaceVariant = ArcadeSurfaceVariant,
    onBackground = ArcadeOnBackground,
    onSurface = ArcadeOnSurface,
    onPrimary = ArcadeBackground,
    onSecondary = ArcadeBackground,
  )

private val VintageCabinetColorScheme =
  lightColorScheme(
    primary = VintagePrimary,
    secondary = VintageSecondary,
    tertiary = VintageTertiary,
    background = VintageBackground,
    surface = VintageSurface,
    surfaceVariant = VintageSurfaceVariant,
    onBackground = VintageOnBackground,
    onSurface = VintageOnSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
  )

@Composable
fun ClassicArcadeTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) ClassicNeonColorScheme else VintageCabinetColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}
