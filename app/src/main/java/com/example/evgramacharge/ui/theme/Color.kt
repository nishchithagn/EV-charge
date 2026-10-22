package com.example.evgramacharge.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val ElectricBlue = Color(0xFF0099FF)
val ElectricGreen = Color(0xFF00C853)
val DarkBg = Color(0xFF0B1020)
val CardBg = Color(0xFF161D31)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0B0B0)

val BlueGradient = Brush.verticalGradient(
    colors = listOf(ElectricBlue, Color(0xFF0052D4))
)

val GreenGradient = Brush.verticalGradient(
    colors = listOf(ElectricGreen, Color(0xFF008E3C))
)

val DarkGradient = Brush.verticalGradient(
    colors = listOf(DarkBg, Color(0xFF1A1F35))
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(ElectricBlue.copy(alpha = 0.2f), Color.Transparent)
)
