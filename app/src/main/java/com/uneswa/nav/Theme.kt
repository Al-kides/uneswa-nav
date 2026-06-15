package com.uneswa.nav
//b
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UneswaRed  = Color(0xFFB71C1C)
private val UneswaGold = Color(0xFFF9A825)
private val DarkRed    = Color(0xFF7F0000)

private val scheme = lightColorScheme(
    primary            = UneswaRed,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFFFEBEE),
    onPrimaryContainer = DarkRed,
    secondary          = UneswaGold,
    onSecondary        = Color.Black,
    surfaceVariant     = Color(0xFFF5F5F5)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, content = content)
