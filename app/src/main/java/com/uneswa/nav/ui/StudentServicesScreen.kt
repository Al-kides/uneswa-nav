package com.uneswa.nav.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
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
    var showError by remember { mutableStateOf<String?>(null) }

    val currentIsDark = isDark ?: androidx.compose.foundation.isSystemInDarkTheme()
    
    val prefs = remember { ctx.getSharedPreferences("uneswa_nav_prefs", Context.MODE_PRIVATE) }
    var showOnboarding by remember { mutableStateOf(prefs.getBoolean("first_time_services", true)) }

    val services = listOf(
        ServiceItem("Campus Navigator", "", Icons.Default.LocationOn), 
        ServiceItem("Laptop recommendation", "", Icons.Default.Star),
        ServiceItem("SIS Results", "https://sis.uneswa.ac.sz/", Icons.Default.Info),
        ServiceItem("Moodle", "https://learn.uneswa.ac.sz/", Icons.AutoMirrored.Filled.List),
        ServiceItem("Email", "https://kwmail.uneswa.ac.sz/", Icons.Default.Email),
        ServiceItem("iEnabler", "https://ienabler.uniswa.sz/pls/prodi04/w99pkg.mi_login?numtype=S", Icons.Default.AccountCircle),
        ServiceItem("Connect your devices to wifi", "", Icons.Default.Share)
    )

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
                                onCheckedChange = onToggleDark,
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
            },//to be honest, this is starting to feel like clay...
            containerColor = MaterialTheme.colorScheme.background
        ) { pad ->
            Column(Modifier.padding(pad).fillMaxSize()) {
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
                    items(services) { svc ->
                        ServiceCard(svc) {
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

        if (showOnboarding) {
            OnboardingOverlay {
                showOnboarding = false
                prefs.edit().putBoolean("first_time_services", false).apply()
            }
        }
    }
}

@Composable
private fun OnboardingOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Welcome to Uneswa Nav",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "This is for student services.\n\n" +
                       "Tap any card to open the link or tool.\n\n" +
                       "Use the Navigator to find your way around campus.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Got it!", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ServiceCard(svc: ServiceItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
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
