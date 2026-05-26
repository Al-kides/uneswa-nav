package com.uneswa.nav

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Navy  = Color(0xFF003082)
private val Gold  = Color(0xFFFFCC00)
private val NavyD = Color(0xFF001A4D)

private val scheme = lightColorScheme(
    primary            = Navy,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFD6E3FF),
    onPrimaryContainer = NavyD,
    secondary          = Gold,
    onSecondary        = Color.Black
)

@Composable
fun AppTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, content = content)
