package com.uneswa.nav.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uneswa.nav.data.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: HomeVM, onPick: (String) -> Unit) {
    val q       by vm.q.collectAsState()
    val results by vm.results.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UNESWA Campus Guide") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor      = MaterialTheme.colorScheme.primary,
                    titleContentColor   = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {

            OutlinedTextField(
                value         = q,
                onValueChange = vm::onSearch,
                placeholder   = { Text("Search — MPH, warden, commerce…") },
                leadingIcon   = { Icon(Icons.Default.Search, null) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (results.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No results for \"$q\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results, key = { it.id }) { LocCard(it) { onPick(it.id) } }
                }
            }
        }
    }
}

@Composable
private fun LocCard(loc: Location, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

            // Abbreviation badge
            Surface(
                color    = MaterialTheme.colorScheme.primaryContainer,
                shape    = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text     = loc.abbr,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style    = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color    = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column {
                Text(loc.name,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(loc.desc,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2)
            }
        }
    }
}
