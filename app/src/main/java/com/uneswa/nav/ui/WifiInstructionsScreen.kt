package com.uneswa.nav.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiInstructionsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf<OsType?>(null) }
    var zoomImage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WiFi Setup Guide") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { RegistrationCard(onImageClick = { zoomImage = it }) }

            if (selectedTab == null) {
                item {
                    Text(
                        "Pick your device type:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(OsType.entries) { type ->
                    OsSelectionCard(type) { selectedTab = type }
                }
            } else {
                item {
                    TextButton(onClick = { selectedTab = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Back to device list")
                    }
                }
                
                val steps = when (selectedTab) {
                    OsType.WINDOWS -> WindowsInstructions
                    OsType.ANDROID -> AndroidInstructions
                    OsType.LINUX -> LinuxInstructions
                    null -> emptyList()
                }

                items(steps) { step ->
                    InstructionStep(step, onImageClick = { zoomImage = it })
                }

                item { TroubleshootingSection(selectedTab!!, onImageClick = { zoomImage = it }) }
            }
        }
        
        zoomImage?.let { img ->
            ImageZoomDialog(image = img, onDismiss = { zoomImage = null })
        }
    }
}

@Composable
private fun RegistrationCard(onImageClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text("Mandatory: Registration", fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 16.dp)) {
                    Text("After connecting, you MUST register your device to get internet access.")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "http://kwnetreg.uniswa.sz",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://kwnetreg.uniswa.sz")))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    InstructionStep(
                        WifiStep("Registration Portal", "This is the page you'll see. Log in with your student details.", "screenshot_2026_08_07_125026"),
                        onImageClick = onImageClick
                    )
                }
            }
            
            if (!expanded) {
                Text(
                    "Register at kwnetreg.uniswa.sz after connecting.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun OsSelectionCard(type: OsType, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(
                type.label,
                Modifier.padding(start = 24.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InstructionStep(step: WifiStep, onImageClick: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            step.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(step.body, style = MaterialTheme.typography.bodyMedium)
        
        if (step.image != null) {
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clickable { onImageClick(step.image) },
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                AsyncImage(
                    model = "file:///android_asset/drawable/${step.image}.webp",
                    contentDescription = "Tap to zoom",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().background(Color.DarkGray)
                )
            }
        }
    }
}

@Composable
private fun TroubleshootingSection(os: OsType, onImageClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    
    val title = when(os) {
        OsType.ANDROID -> "Apps not loading? Use Psiphon"
        OsType.WINDOWS -> "Browsing Issues? Proxy & Reset"
        OsType.LINUX -> "Terminal / DNF Proxy Setup"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 16.dp)) {
                    when (os) {
                        OsType.ANDROID -> {
                            Text("If WhatsApp or other apps won't load even when connected, Psiphon Pro can bypass the proxy restrictions.")
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val intent = ctx.packageManager.getLaunchIntentForPackage("com.psiphon3.subscription")
                                    if (intent != null) {
                                        ctx.startActivity(intent)
                                    } else {
                                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.psiphon3.subscription")))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val isInstalled = ctx.packageManager.getLaunchIntentForPackage("com.psiphon3.subscription") != null
                                Text(if (isInstalled) "Open Psiphon Pro" else "Install Psiphon Pro")
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Configuration:", fontWeight = FontWeight.Bold)
                            Text("• Proxy Settings -> Connect through an HTTP Proxy: ON\n" +
                                 "• Host Address: proxy02.uniswa.sz\n" +
                                 "• Port: 3128")
                            Spacer(Modifier.height(16.dp))
                            InstructionStep(
                                WifiStep("Configure Psiphon", "Set the proxy host and port in Psiphon's settings as shown.", "screenshot_20260807_130154_psiphon_pro"),
                                onImageClick = onImageClick
                            )
                        }
                        OsType.WINDOWS -> {
                            WindowsTroubleshooting.forEach { step ->
                                InstructionStep(step, onImageClick = onImageClick)
                            }
                        }
                        OsType.LINUX -> {
                            LinuxTroubleshooting.forEach { step ->
                                InstructionStep(step, onImageClick = onImageClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageZoomDialog(image: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.9f)
        ) {
            Box(Modifier.fillMaxSize()) {
                var scale by remember { mutableFloatStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                AsyncImage(
                    model = "file:///android_asset/drawable/$image.webp",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset += pan
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

enum class OsType(val label: String) {
    WINDOWS("Windows"),
    ANDROID("Android"),
    LINUX("Linux (Fedora)")
}

data class WifiStep(val title: String, val body: String, val image: String? = null)

val WindowsInstructions = listOf(
    WifiStep("Open network icon", "On the desktop, right-click the wireless icon in the bottom-right taskbar.", "img31"),
    WifiStep("Open Sharing Center", "Click 'Open Network and Sharing Center'.", "img32"),
    WifiStep("Set up new connection", "Click 'Set up a new connection or network'.", "img33"),
    WifiStep("Choose manual setup", "Select 'Manually connect to a wireless network', then Next.", "img34"),
    WifiStep("Enter network details", "Name: uniswawifi-students\nSecurity: WPA2-Enterprise\nEncryption: AES", "img35"),
    WifiStep("Edit settings", "On success screen, click 'Change connection settings'.", "img36"),
    WifiStep("Set authentication", "Security tab -> Method: Microsoft: Protected EAP (PEAP). Click Settings.", "img37"),
    WifiStep("Disable validation", "UNCHECK 'Verify the server's identity by validating the certificate'. Click Configure.", "img38"),
    WifiStep("Disable auto-fill", "UNCHECK 'Automatically use my Windows logon name and password'. OK, OK.", "img39"),
    WifiStep("Advanced settings", "Click Advanced settings. Check 'Specify authentication mode' and choose 'User authentication'.", "img310"),
    WifiStep("Connect", "Click the wireless icon again, select uniswawifi-students.", "img311"),
    WifiStep("Sign in", "Enter your student username and password.", "img312"),
    WifiStep("Confirm connection", "Check the network flyout — status should show 'Connected' under uniswawifi-students.", "img313")
)

val WindowsTroubleshooting = listOf(
    WifiStep("Open Settings", "Press Windows key, open Settings, then click Network & Internet.", "screenshot_2026_08_07_124733"),
    WifiStep("Check status", "On the Status page you'll likely see 'No Internet access' — this is expected. Click Proxy.", "screenshot_2026_08_07_124751"),
    WifiStep("Setup Script (PAC)", "Under Automatic proxy setup, turn 'Automatically detect settings' ON and 'Use setup script' ON. Enter: www.uniswa.sz/uniswa/uniswaproxy.pac", "screenshot_2026_08_07_124814"),
    WifiStep("Network Reset", "If you still can't browse, scroll back to the Status page and click Network reset -> Reset now. This restarts the PC.", "screenshot_2026_08_07_124841")
)

val AndroidInstructions = listOf(
    WifiStep("Select Network", "Settings -> Wi-Fi -> Tap 'uniswawifi-students'.", "screenshot_20260807_125659_one_ui_home"),
    WifiStep("Identity", "Identity: Your Student Number\nCA Certificate: Don't validate", "screenshot_20260807_125721_settings"),
    WifiStep("EAP Settings", "EAP Method: PEAP\nPhase 2: MSCHAPV2", "screenshot_20260807_125732_settings")
)

val LinuxInstructions = listOf(
    WifiStep("Open Wi-Fi", "Settings -> Wi-Fi -> uniswawifi-students.", "screenshot_from_2026_08_07_13_08_10"),
    WifiStep("Authentication", "Security: WPA & WPA2 Enterprise\nAuth: Protected EAP (PEAP)\nCA Cert: Not required\nInner Auth: MSCHAPv2", "screenshot_from_2026_08_07_13_09_44")
)

val LinuxTroubleshooting = listOf(
    WifiStep("Shell Config", "Open a terminal and edit your shell's rc file, e.g. hx ~/.bashrc", "screenshot_from_2026_08_07_13_11_21"),
    WifiStep("Proxy Variables", "Add: export http_proxy='proxy02.uniswa.sz:3128' etc. source ~/.bashrc to apply.", "screenshot_from_2026_08_07_13_13_14"),
    WifiStep("DNF Config", "For dnf specifically: sudo vim /etc/dnf/dnf.conf", "screenshot_from_2026_08_07_13_13_54"),
    WifiStep("Proxy Line", "Add: proxy=http://proxy02.uniswa.sz:3128 under [main].", "screenshot_from_2026_08_07_13_14_04")
)
