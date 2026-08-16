package com.uneswa.nav.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectionsScreen(vm: DirectionsVM, onBack: () -> Unit) {
    val loc = vm.loc
    val idx by vm.idx.collectAsState()
    val haptic = LocalHapticFeedback.current

    if (loc == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Location not found.") }
        return
    }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("uneswa_nav_prefs", Context.MODE_PRIVATE) }
    var showBanner by remember { mutableStateOf(prefs.getBoolean("first_time_directions", true)) }
    
    var completedSteps by rememberSaveable(loc.id, idx) { mutableStateOf(setOf<Int>()) }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "file:///android_asset/drawable/logo.webp",
                            contentDescription = "UNESWA Logo",
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(loc.abbr)
                    }
                },
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
        },
        bottomBar = {
            val steps = loc.routes.getOrNull(idx)?.steps ?: emptyArray()
            if (steps.isNotEmpty()) {
                Surface(tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val progress = completedSteps.size.toFloat() / steps.size
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.weight(1f).height(8.dp),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Step ${completedSteps.size} of ${steps.size}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
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
                            Text("Navigation Tip", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Swipe photos for angles. Tap numbers to mark steps as done.", style = MaterialTheme.typography.bodyMedium)
                        }
                        TextButton(onClick = {
                            showBanner = false
                            prefs.edit().putBoolean("first_time_directions", false).apply()
                        }) {
                            Text("Got it")
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                loc.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                loc.desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (loc.photos.isNotEmpty()) {
                    item {
                        Text(
                            "Visual Aids",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(loc.photos.size) { Photo(loc.photos[it]) }
                        }
                    }
                }

                if (loc.routes.size > 1) {
                    item {
                        Text(
                            "Start Position:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            loc.routes.forEachIndexed { i, route ->
                                FilterChip(
                                    selected = i == idx,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        vm.pick(i)
                                    },
                                    label = { Text(route.from) },
                                    shape = MaterialTheme.shapes.medium
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Route Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val steps = loc.routes.getOrNull(idx)?.steps ?: emptyArray()
                itemsIndexed(steps) { i, step ->
                    Step(
                        n = i + 1,
                        step = step,
                        isDone = completedSteps.contains(i),
                        onToggle = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            completedSteps = if (completedSteps.contains(i)) {
                                completedSteps - i
                            } else {
                                completedSteps + i
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Photo(name: String) {
    val ctx = LocalContext.current
    val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
    val webpPath = "file:///android_asset/drawable/$name.webp"

    Card(
        modifier = Modifier.size(width = 280.dp, height = 180.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        AsyncImage(
            model = ImageRequest.Builder(ctx)
                .data(if (resId != 0) resId else webpPath)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun Step(
    n: Int,
    step: com.uneswa.nav.data.Step,
    isDone: Boolean,
    onToggle: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().clickable { onToggle() },
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (isDone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    if (isDone) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondary)
                    } else {
                        Text(
                            "$n",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                step.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp),
                color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
            )
        }

        if (step.image != null) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp)
                    .height(220.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                val ctx = LocalContext.current
                val resId = ctx.resources.getIdentifier(step.image, "drawable", ctx.packageName)
                val webpPath = "file:///android_asset/drawable/${step.image}.webp"

                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(if (resId != 0) resId else webpPath)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().let { if (isDone) it.background(Color.Black.copy(alpha = 0.1f)) else it }
                )
            }
        }
    }
}
