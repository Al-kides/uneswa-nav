package com.uneswa.nav

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val UneswaRed = Color(0xFFB71C1C)
val UneswaRedDark = Color(0xFF8E1111)
val UneswaGold = Color(0xFFF9A825)

private val DarkScheme = darkColorScheme(
    primary = UneswaRed,
    onPrimary = Color.White,
    primaryContainer = UneswaRedDark,
    onPrimaryContainer = Color.White,
    secondary = UneswaGold,
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFCED4DA),
    outline = Color(0xFF49454F)
)

private val LightScheme = lightColorScheme(
    primary = UneswaRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = Color(0xFF7F0000),
    secondary = UneswaGold,
    onSecondary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkScheme else LightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AppShapes,
        content = content
    )
}
