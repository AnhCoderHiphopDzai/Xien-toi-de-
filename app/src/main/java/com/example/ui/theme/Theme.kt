package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = darkColorScheme(
  primary = SkewerOrange,
  onPrimary = Color.Black,
  primaryContainer = SizzlingRed,
  onPrimaryContainer = Color.White,
  secondary = GoldenCrisp,
  onSecondary = Color.Black,
  secondaryContainer = SidewalkGray,
  onSecondaryContainer = TextPrimary,
  tertiary = MoneyGreen,
  onTertiary = Color.Black,
  background = DeepNight,
  onBackground = TextPrimary,
  surface = DarkAlley,
  onSurface = TextPrimary,
  surfaceVariant = CardBgDark,
  onSurfaceVariant = TextSecondary,
  error = PoliceRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = GameColorScheme,
    typography = Typography,
    content = content
  )
}
