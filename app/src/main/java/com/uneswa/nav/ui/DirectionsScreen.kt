package com.uneswa.nav.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectionsScreen(vm: DirectionsVM, onBack: () -> Unit) {
    val loc = vm.loc
    val idx by vm.idx.collectAsState()

    if (loc == null) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Location not found.") }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(loc.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor           = MaterialTheme.colorScheme.primary,
                    titleContentColor        = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { pad ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(pad),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header card: abbreviation expanded
            item {
                Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(loc.abbr,
                                style      = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.primary)
                            Text("  =  ${loc.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(loc.desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            // Photo strip — skipped entirely if no photos available
            if (loc.photos.isNotEmpty()) {
                item {
                    Text("Photos", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(loc.photos.size) { Photo(loc.photos[it]) }
                    }
                }
            }

            // Approach selector — only rendered when there are multiple routes
            if (loc.routes.size > 1) {
                item {
                    Text("Coming from:", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        loc.routes.forEachIndexed { i, route ->
                            FilterChip(
                                selected = i == idx,
                                onClick  = { vm.pick(i) },
                                label    = { Text(route.from) }
                            )
                        }
                    }
                }
            }

            item {
                Text("Directions", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
            }

            val steps = loc.routes.getOrNull(idx)?.steps ?: emptyArray()
            itemsIndexed(steps) { i, step -> Step(i + 1, step) }
        }
    }
}

@Composable
private fun Photo(name: String) {
    val ctx   = LocalContext.current
    val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)

    Card(
        modifier  = Modifier.size(width = 240.dp, height = 160.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (resId != 0) {
            AsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(resId)
                    .decoderFactory(ImageDecoderDecoder.Factory()) // HEIC on API 28+
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("📷 Photo coming soon",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun Step(n: Int, text: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Surface(
            shape    = MaterialTheme.shapes.extraLarge,
            color    = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        ) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("$n", style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp))
    }
}
