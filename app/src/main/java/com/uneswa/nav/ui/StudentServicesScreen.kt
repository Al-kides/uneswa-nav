package com.uneswa.nav.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ServiceItem(
    val title: String,
    val url: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentServicesScreen(
    onNavigate: () -> Unit,
    onLaptops: () -> Unit,
    onWifi: () -> Unit,
    isDark: Boolean?,
    onToggleDark: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showError by remember { mutableStateOf<String?>(null) }

    val currentIsDark = isDark ?: androidx.compose.foundation.isSystemInDarkTheme()
    
    val prefs = remember { ctx.getSharedPreferences("uneswa_nav_prefs", Context.MODE_PRIVATE) }
    var showBanner by remember { mutableStateOf(prefs.getBoolean("first_time_services", true)) }

    val services = listOf(
        ServiceItem("Campus Navigator", "", Icons.Default.LocationOn), 
        ServiceItem("Laptop recommendation", "", Icons.Default.Star),
        ServiceItem("SIS Results", "https://sis.uneswa.ac.sz/", Icons.Default.Info),
        ServiceItem("Moodle", "https://learn.uneswa.ac.sz/", Icons.AutoMirrored.Filled.List),
        ServiceItem("Email", "https://kwmail.uneswa.ac.sz/", Icons.Default.Email),
        ServiceItem("iEnabler", "https://ienabler.uniswa.sz/pls/prodi04/w99pkg.mi_login?numtype=S", Icons.Default.AccountCircle),
        ServiceItem("Connect your devices to wifi", "", Icons.Default.Share)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UNESWA Student Services") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            if (currentIsDark) "Dark" else "Light",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = currentIsDark,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onToggleDark(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.3f),
                                uncheckedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            AnimatedVisibility(
                visible = showBanner,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Welcome to Uneswa Nav", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Tap any card to open student tools. Use the Navigator to find your way.", style = MaterialTheme.typography.bodyMedium)
                        }
                        TextButton(onClick = {
                            showBanner = false
                            prefs.edit().putBoolean("first_time_services", false).apply()
                        }) {
                            Text("Got it")
                        }
                    }
                }
            }

            if (showError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Text(showError!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(services, key = { it.title }) { svc ->
                    ServiceCard(svc) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showError = null
                        when (svc.title) {
                            "Campus Navigator" -> onNavigate()
                            "Laptop recommendation" -> onLaptops()
                            "Connect your devices to wifi" -> onWifi()
                            "Moodle" -> {
                                val intent = ctx.packageManager.getLaunchIntentForPackage("com.moodle.moodlemobile")
                                if (intent != null) {
                                    ctx.startActivity(intent)
                                } else {
                                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(svc.url)))
                                }
                            }
                            else -> {
                                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(svc.url)))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(svc: ServiceItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                svc.icon,
                null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            Text(
                svc.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}
