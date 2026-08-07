package com.uneswa.nav
//b
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val UneswaRed      = Color(0xFFB71C1C)
val UneswaRed2     = Color(0xFF8E1111)
val UneswaGold     = Color(0xFFF9A825)
val UneswaGoldSoft = Color(0x33F9A825)
val DarkBg         = Color(0xFF101214)
val DarkCard       = Color(0xFF171A1F)
val DarkCard2      = Color(0xFF1D2128)
val TextSoft       = Color(0xFFCED4DA)
val LineSoft       = Color.White.copy(alpha = 0.08f)

private val DarkScheme = darkColorScheme(
    primary = UneswaRed,
    onPrimary = Color.White,
    primaryContainer = UneswaRed2,
    onPrimaryContainer = Color.White,
    secondary = UneswaGold,
    onSecondary = Color.Black,
    background = DarkBg,
    surface = DarkCard,
    onSurface = Color.White,
    surfaceVariant = DarkCard2,
    onSurfaceVariant = TextSoft
)

private val LightScheme = lightColorScheme(
    primary            = UneswaRed,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFFFEBEE),
    onPrimaryContainer = Color(0xFF7F0000),
    secondary          = UneswaGold,
    onSecondary        = Color.Black,
    surfaceVariant     = Color(0xFFF5F5F5),
    onSurfaceVariant   = Color(0xFF49454F)
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkScheme else LightScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
