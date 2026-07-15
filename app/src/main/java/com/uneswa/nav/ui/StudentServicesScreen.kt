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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.uneswa.nav.utils.WifiHelper
import kotlinx.coroutines.launch

data class ServiceItem(
    val title: String,
    val url: String,
    val icon: ImageVector,
    val needsWifi: Boolean = false
)
// this is where the student will land when they open the app. it will show them goodies
//i.e registration for device, self help, student info system and what have you...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentServicesScreen(onNavigate: () -> Unit) {
    val ctx = LocalContext.current
    val wifiHelper = remember { WifiHelper(ctx) }
    val scope = rememberCoroutineScope()
    var showError by remember { mutableStateOf<String?>(null) }
    var showRegDialog by remember { mutableStateOf(false) }
    
    val prefs = remember { ctx.getSharedPreferences("uneswa_nav_prefs", Context.MODE_PRIVATE) }
    var showOnboarding by remember { mutableStateOf(prefs.getBoolean("first_time_services", true)) }

    val services = listOf(
        ServiceItem("Campus Navigator", "", Icons.Default.LocationOn), // entry point to our older implementation
        ServiceItem("Registre phone", "http://kwnetreg.uniswa.sz/", Icons.Default.Edit, true),
        ServiceItem("Connect your devices to wifi", "", Icons.Default.Share),
        ServiceItem("Laptop recommendation", "", Icons.Default.Star),
        ServiceItem("SIS Results", "https://sis.uneswa.ac.sz/", Icons.Default.Info),
        ServiceItem("Moodle", "https://learn.uneswa.ac.sz/", Icons.AutoMirrored.Filled.List),
        ServiceItem("Email", "https://kwmail.uneswa.ac.sz/", Icons.Default.Email),
        ServiceItem("iEnabler", "https://ienabler.uniswa.sz/pls/prodi04/w99pkg.mi_login?numtype=S", Icons.Default.AccountCircle),
        ServiceItem("Get Psiphon", "", Icons.Default.Lock)
    )

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("UNESWA Student Services") }
                )
            }
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
                                "Get Psiphon" -> wifiHelper.openPsiphonStore()
                                "Connect your devices to wifi", "Laptop recommendation" -> {
                                    showError = "Feature under construction. Use system settings."
                                }
                                "Registre phone" -> {
                                    if (!wifiHelper.isStudentsWifi()) {
                                        showError = "Connect to uniswawifi-students to register your device."
                                    } else {
                                        showRegDialog = true
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

        if (showRegDialog) {
            RegistrationDialog(
                onDismiss = { showRegDialog = false },
                onRegister = { id, bday ->
                    showRegDialog = false
                    scope.launch {
                        val (ok, msg) = wifiHelper.registerDevice(id, bday)
                        showError = msg
                    }
                }
            )
        }

        if (showOnboarding) {
            OnboardingOverlay {
                showOnboarding = false
                prefs.edit().putBoolean("first_time_services", false).apply()
            }
        }
    }
}

//todo account for peeps proly changing their passwords. But for now, doing birthday.
@Composable
fun RegistrationDialog(onDismiss: () -> Unit, onRegister: (String, String) -> Unit) {
    var studentId by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Device Registration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter details to register your phone on the network.")
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = birthday,
                    onValueChange = { birthday = it },
                    label = { Text("Birthday (ddmmyyyy)") },
                    placeholder = { Text("e.g. 12052001") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { onRegister(studentId, birthday) }) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
                text = "this is foor student servics.\n\n" +
                       "Tap any card to open the link or tool.\n\n" +
                       "use the Navigater to find your way around cammpus.",
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
                Text("Ggot it!", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ServiceCard(svc: ServiceItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(svc.icon, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(svc.title, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}
