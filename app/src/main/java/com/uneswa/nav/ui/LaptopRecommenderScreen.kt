package com.uneswa.nav.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uneswa.nav.*
import kotlin.math.cos
import kotlin.math.sin

data class FacultyProfile(
    val name: String,
    val description: String,
    val campus: String,
    val programmes: List<String>
)

data class ProgrammeProfile(
    val name: String,
    val faculty: String,
    val summary: String,
    val software: List<String>,
    val components: List<SpecComponent>,
    val relatedCourses: List<String>,
    val budget: PriceBand
)

data class SpecComponent(
    val name: String,
    val min: String,
    val rec: String,
    val technicalReason: String,
    val laymanReason: String,
    val weight: Float
)

data class PriceBand(
    val localNewMin: Int,
    val localNewMax: Int,
    val saOnlineMin: Int,
    val saOnlineMax: Int,
    val usedMin: Int = 0,
    val usedMax: Int = 0,
    val currency: String = "SZL/ZAR"
)

private data class HexStat(
    val label: String,
    val value: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaptopRecommenderScreen(
    onBack: () -> Unit = {},
    vm: LaptopRecommenderVM = viewModel(factory = VMFactory(com.uneswa.nav.data.LocationRepo()))
) {
    val haptic = LocalHapticFeedback.current

    val faculties = remember {
        listOf(
            FacultyProfile(
                name = "Faculty of Commerce",
                description = "Business, accounting, management, marketing, and taxation.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "Bachelor of Commerce",
                    "Post-Graduate Diploma in Taxation",
                    "Post-Graduate Diploma in Public Sector Accounting"
                )
            ),
            FacultyProfile(
                name = "Faculty of Education",
                description = "Teacher training and professional education pathways.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "Bachelor of Education Primary",
                    "Bachelor of Education Secondary",
                    "Post-Graduate Certificate in Education"
                )
            ),
            FacultyProfile(
                name = "Faculty of Humanities",
                description = "Language, communication, and media-focused study.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "Bachelor of Arts in Journalism and Mass Communication",
                    "Bachelor of Arts in Humanities"
                )
            ),
            FacultyProfile(
                name = "Faculty of Science and Engineering",
                description = "Science, computing, engineering, GIS, and mathematics.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "Bachelor of Science",
                    "Bachelor of Engineering (Electrical and Electronic)",
                    "Bachelor of Science in Computer Science Education",
                    "Bachelor of Science in Information Technology",
                    "Bachelor of Science in Information Science",
                    "Bachelor of Science in Geographic Information Science",
                    "Bachelor of Science in Actuarial and Financial Mathematics"
                )
            ),
            FacultyProfile(
                name = "Faculty of Social Sciences",
                description = "Social science, social work, and law-related programmes.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "Bachelor of Arts in Social Science",
                    "Bachelor of Social Work",
                    "Bachelor of Laws (LLB)"
                )
            ),
            FacultyProfile(
                name = "Faculty of Health Sciences",
                description = "Nursing, environmental health, and related health programmes.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "Bachelor of Nursing Science",
                    "Bachelor of Science in Environmental Health Science",
                    "Bachelor of Science in Environmental Management"
                )
            ),
            FacultyProfile(
                name = "Faculty of Consumer Sciences",
                description = "Food science, nutrition, consumer science, and textile design.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "B.Sc. in Food Science, Nutrition and Technology",
                    "B.Sc. in Consumer Science",
                    "B.Sc. in Consumer Science Education",
                    "B.Sc. in Textile and Apparel Design"
                )
            ),
            FacultyProfile(
                name = "Institute of Distance Education",
                description = "Flexible learning programmes across multiple disciplines.",
                campus = "Kwaluseni",
                programmes = listOf(
                    "B.Com (IDE)",
                    "B.Ed (IDE)",
                    "B.Sc IT (IDE)",
                    "B.Sc Computer Science Education (IDE)",
                    "Bachelor of Nursing Science (IDE)"
                )
            )
        )
    }

    val selectedFacultyProfile = faculties.find { it.name == vm.selectedFaculty }
    val programme = remember(vm.selectedProgramme) {
        vm.selectedProgramme?.let { getProfile(it) } ?: getGeneralProfile(vm.selectedProgramme ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            vm.selectedProgramme != null -> vm.selectedProgramme!!
                            vm.selectedFaculty != null -> vm.selectedFaculty!!
                            else -> "UNESWA Laptop Guide"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        when {
                            vm.selectedProgramme != null -> {
                                vm.selectedProgramme = null
                                vm.showLayman = false
                            }
                            vm.selectedFaculty != null -> vm.selectedFaculty = null
                            else -> onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Box(modifier = Modifier.fillMaxSize().padding(pad)) {
            when {
                vm.selectedProgramme != null -> ProgrammeDetailScreen(
                    profile = programme,
                    showLayman = vm.showLayman,
                    onToggleLayman = { vm.showLayman = it },
                    onBackToFaculty = {
                        vm.selectedProgramme = null
                        vm.showLayman = false
                    }
                )

                vm.selectedFaculty != null && selectedFacultyProfile != null -> ProgrammeListScreen(
                    faculty = selectedFacultyProfile,
                    onSelect = { vm.selectedProgramme = it }
                )

                else -> FacultyLandingScreen(
                    faculties = faculties,
                    onSelectFaculty = { vm.selectedFaculty = it.name }
                )
            }
        }
    }
}

@Composable
private fun FacultyLandingScreen(
    faculties: List<FacultyProfile>,
    onSelectFaculty: (FacultyProfile) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeroCard() }
        item { WhyPersonalLaptopCard() }
        item {
            StatsStrip(
                items = listOf(
                    "8 faculties",
                    "60+ programmes",
                    "Kwaluseni focus",
                    "2026 pricing"
                )
            )
        }
        item { SectionTitle("Choose a faculty") }
        items(faculties, key = { it.name }) { faculty ->
            FacultyCard(faculty = faculty, onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSelectFaculty(faculty)
            })
        }
    }
}

@Composable
private fun WhyPersonalLaptopCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Why own a laptop?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Computer labs are often full or closed when you need them most. Having your own laptop means you can work anywhere, anytime. A strong laptop matching your programme's needs is a massive advantage for effective study.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ProgrammeListScreen(
    faculty: FacultyProfile,
    onSelect: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { FacultyBanner(faculty) }
        item {
            Text(
                "Programmes",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(faculty.programmes, key = { it }) { programme ->
            val specific = remember(programme) { getProfile(programme) != null }
            ProgrammeCard(programme, specific) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSelect(programme)
            }
        }
    }
}

@Composable
private fun ProgrammeDetailScreen(
    profile: ProgrammeProfile,
    showLayman: Boolean,
    onToggleLayman: (Boolean) -> Unit,
    onBackToFaculty: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hexStats = remember(profile) {
        val cpu = profile.components.firstOrNull { it.name == "CPU" }?.weight ?: 0.6f
        val ram = profile.components.firstOrNull { it.name == "RAM" }?.weight ?: 0.6f
        val storage = profile.components.firstOrNull { it.name == "Storage" }?.weight ?: 0.6f
        val gpu = profile.components.firstOrNull { it.name == "GPU" }?.weight ?: 0.5f
        val port = if (profile.name.contains("Engineering", true) || profile.name.contains("Computer", true)) 0.7f else 0.4f
        val battery = if (profile.name.contains("Education", true) || profile.name.contains("Humanities", true)) 0.8f else 0.55f
        listOf(
            HexStat("CPU", cpu),
            HexStat("RAM", ram),
            HexStat("GPU", gpu),
            HexStat("STOR", storage),
            HexStat("PORT", port),
            HexStat("BAT", battery)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DetailHero(profile, showLayman, onToggleLayman) }
        item {
            RadarHexCard(
                stats = hexStats,
                title = if (showLayman) "Simple Profile" else "Technical Profile",
                subtitle = if (showLayman) "Bigger shape = higher specs needed." else "Workload distribution map."
            )
        }
        item { BudgetAndNotes(profile, showLayman) }
        item { StorageTechCard() }
        item { SectionTitle(if (showLayman) "What this means" else "Specs & Reasons") }
        items(profile.components, key = { it.name }) { component ->
            ComponentDetailCard(component, showLayman)
        }
        item { SoftwareCard(profile.software) }
        item { CourseCard(profile.relatedCourses) }
        item {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBackToFaculty()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Back to programmes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StorageTechCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("HDD vs SSD: Speed is everything", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(
                "• HDD (Hard Drive): Slow, noisy, and prone to breaking if dropped. Cheaper for huge space, but makes Windows feel sluggish.\n" +
                "• SSD (Solid State Drive): 10x faster than HDD. Makes your laptop boot in seconds and apps open instantly. ALWAYS pick a laptop with an SSD for university work.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Text(
                    "UNESWA • Kwaluseni",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "Pick a laptop that actually fits your programme.",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Compare technical specs and simple explanations side-by-side to make the best choice for your studies.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun StatsStrip(items: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun FacultyBanner(faculty: FacultyProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(faculty.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(faculty.description, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(faculty.campus)
                TagChip("${faculty.programmes.size} programmes")
            }
        }
    }
}

@Composable
private fun FacultyCard(faculty: FacultyProfile, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    faculty.name.first().toString(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(faculty.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(faculty.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(faculty.campus, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun ProgrammeCard(programme: String, specific: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (specific) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (specific) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(programme, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    if (specific) "Specific profile available" else "General student profile",
                    color = if (specific) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (specific) {
                Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), shape = CircleShape) {
                    Text(
                        "SPECIFIC",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailHero(
    profile: ProgrammeProfile,
    showLayman: Boolean,
    onToggleLayman: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.Center
            ) {
                TagChip(profile.faculty)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showLayman) "Simple" else "Technical", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = showLayman,
                        onCheckedChange = onToggleLayman,
                        modifier = Modifier.scale(0.8f).height(24.dp)
                    )
                }
            }
            Text(profile.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(profile.summary, style = MaterialTheme.typography.bodyLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                profile.software.take(4).forEach { TagChip(it) }
            }
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BudgetAndNotes(profile: ProgrammeProfile, showLayman: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(" Indicative price (2026, Kwaluseni focus)", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("Local new: SZL ${profile.budget.localNewMin.toLocale()}–${profile.budget.localNewMax.toLocale()}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("SA online: R ${profile.budget.saOnlineMin.toLocale()}–${profile.budget.saOnlineMax.toLocale()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            if (profile.budget.usedMin > 0) {
                Text("Used market: SZL ${profile.budget.usedMin.toLocale()}–${profile.budget.usedMax.toLocale()} (limited warranty)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                if (showLayman) {
                    "Rough price range for a comfortable experience. Local shops like HiFi Corp and PC Systems stock these."
                } else {
                    "Based on 2026 pricing from local (HiFi Corp, PC Systems) and SA online vendors. Prices may change."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Int.toLocale(): String = this.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")

@Composable
private fun ComponentDetailCard(component: SpecComponent, showLayman: Boolean) {
    val animatedWeight by animateFloatAsState(targetValue = component.weight, label = "weight")
    val accent = if (component.weight >= 0.75f) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(component.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(color = accent.copy(alpha = 0.1f), shape = CircleShape) {
                    Text(if (showLayman) "EASY" else "TECH", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = accent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecRow("Minimum", component.min)
                SpecRow("Recommended", component.rec, highlight = true)
            }
            LinearProgressIndicator(
                progress = { animatedWeight },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = accent,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            Text(
                text = if (showLayman) component.laymanReason else component.technicalReason,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String, highlight: Boolean = false) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text(
            "$label:",
            color = if (highlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(100.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Text(value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SoftwareCard(software: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Software used in this field", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                software.forEach { TagChip(it) }
            }
        }
    }
}

@Composable
private fun CourseCard(courses: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Core courses", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(courses.joinToString(" • "), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun RadarHexCard(stats: List<HexStat>, title: String, subtitle: String) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Box(modifier = Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
                val accentColor = MaterialTheme.colorScheme.primary
                val pointColor = MaterialTheme.colorScheme.secondary
                
                val animatedProgress = remember { Animatable(0f) }
                LaunchedEffect(stats) {
                    animatedProgress.snapTo(0f)
                    animatedProgress.animateTo(1f, tween(1000))
                }

                val labelPaint = remember(textColor) {
                    android.graphics.Paint().apply {
                        color = textColor.toArgb()
                        textSize = 32f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        isAntiAlias = true
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val radius = minOf(size.width, size.height) * 0.25f
                    val outer = radius * 1.2f
                    val points = 6
                    val step = (2 * Math.PI / points).toFloat()

                    repeat(points) { i ->
                        val angle = step * i - Math.PI.toFloat() / 2f
                        drawLine(textColor.copy(alpha = 0.1f), Offset(cx, cy), Offset(cx + cos(angle) * outer, cy + sin(angle) * outer), 1f)
                    }

                    listOf(0.35f, 0.7f, 1f).forEach { mult ->
                        val ringPath = Path().apply {
                            val startAngle = -Math.PI.toFloat() / 2f
                            moveTo(cx + cos(startAngle) * outer * mult, cy + sin(startAngle) * outer * mult)
                            (1 until points).forEach { i ->
                                val angle = step * i - Math.PI.toFloat() / 2f
                                lineTo(cx + cos(angle) * outer * mult, cy + sin(angle) * outer * mult)
                            }
                            close()
                        }
                        drawPath(ringPath, textColor.copy(alpha = 0.05f), style = Stroke(1f))
                    }

                    val fillPoints = stats.mapIndexed { index, stat ->
                        val angle = step * index - Math.PI.toFloat() / 2f
                        val len = outer * (0.2f + (0.8f * stat.value)) * animatedProgress.value
                        Offset(cx + cos(angle) * len, cy + sin(angle) * len)
                    }

                    drawPath(Path().apply {
                        moveTo(fillPoints.first().x, fillPoints.first().y)
                        fillPoints.drop(1).forEach { lineTo(it.x, it.y) }
                        close()
                    }, accentColor.copy(alpha = 0.3f))

                    fillPoints.forEachIndexed { index, pt ->
                        drawCircle(pointColor, 6f, pt)
                        val labelAngle = step * index - Math.PI.toFloat() / 2f
                        val lx = cx + cos(labelAngle) * (outer * 1.35f)
                        val ly = cy + sin(labelAngle) * (outer * 1.35f)
                        drawContext.canvas.nativeCanvas.drawText(
                            stats[index].label, lx, ly + 10f, labelPaint
                        )
                    }
                }
            }
        }
    }
}

private fun getProfile(programme: String): ProgrammeProfile? = when (programme) {
    "Bachelor of Commerce" -> ProgrammeProfile(
        name = "Bachelor of Commerce",
        faculty = "Faculty of Commerce",
        summary = "A four-year full-time programme with major routes in Accounting and Finance, Management, and Marketing.",
        software = listOf("Moodle", "Office 365", "Excel", "QuickBooks", "Sage", "Library databases"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Business workloads are mostly spreadsheets, browser tabs, and office apps.", "A decent brain is enough; it should open files quickly and not freeze on normal work.", 0.55f),
            SpecComponent("RAM", "8GB", "16GB", "Commerce work gets better with extra memory once spreadsheets and browser tabs grow.", "More memory means less slowing down when many things are open.", 0.6f),
            SpecComponent("GPU", "Integrated", "Integrated", "No serious graphics load here.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "SSD speed helps the laptop feel responsive through long semesters.", "Fast storage makes the whole laptop feel less tired.", 0.75f)
        ),
        relatedCourses = listOf("Accounting", "Finance", "Management", "Marketing"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    "Bachelor of Arts in Journalism and Mass Communication" -> ProgrammeProfile(
        name = "Bachelor of Arts in Journalism and Mass Communication",
        faculty = "Faculty of Humanities",
        summary = "A four-year undergraduate degree focused on journalism and media, combining theory with practical training.",
        software = listOf("Moodle", "Adobe Premiere Pro", "Adobe Photoshop", "Adobe InDesign", "Adobe Audition", "Office 365"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Editing and export work are much smoother with stronger multi-core performance.", "The brain matters a lot when editing video or audio.", 0.85f),
            SpecComponent("RAM", "16GB", "32GB", "Adobe apps and multitasking chew memory fast.", "More memory helps editing feel smoother.", 0.9f),
            SpecComponent("GPU", "Integrated", "RTX 3050 / 4050", "A dedicated GPU helps with timelines, playback, and rendering.", "A graphics card helps when you're working with video or visuals.", 0.8f),
            SpecComponent("Storage", "512GB SSD", "1TB SSD", "Media files fill storage quickly.", "Video and audio files eat space fast.", 0.92f)
        ),
        relatedCourses = listOf("Reporting", "Media Production", "Layout", "Video Editing"),
        budget = PriceBand(13000, 20000, 15000, 25000, 8000, 14000)
    )

    "Bachelor of Arts in Social Science" -> ProgrammeProfile(
        name = "Bachelor of Arts in Social Science",
        faculty = "Faculty of Social Sciences",
        summary = "A degree covering key disciplines like sociology, political science, social work, economics, demography, and statistics.",
        software = listOf("Moodle", "Office 365", "SPSS", "RStudio", "Stata", "Library databases"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i5 / Ryzen 7", "Data handling and analysis benefit from a balanced modern chip.", "A good brain keeps data work moving without lag.", 0.7f),
            SpecComponent("RAM", "8GB", "16GB", "More memory is useful when working with datasets and research tabs.", "Memory helps when many things are open.", 0.68f),
            SpecComponent("GPU", "Integrated", "Integrated", "This is not graphics-heavy.", "No special graphics card needed.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage makes loading files and apps easier.", "An SSD makes the laptop feel quicker.", 0.7f)
        ),
        relatedCourses = listOf("Sociology", "Political Science", "Economics", "Statistics"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    "Bachelor of Science in Information Technology" -> ProgrammeProfile(
        name = "Bachelor of Science in Information Technology",
        faculty = "Faculty of Science and Engineering",
        summary = "An undergraduate programme focused on computing, software development, and information systems.",
        software = listOf("Moodle", "VS Code", "Visual Studio", "Blender", "Git", "Python", "MySQL"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Software development and 3D modeling in Blender require strong multi-core performance.", "The brain needs to be powerful for coding and rendering.", 0.82f),
            SpecComponent("RAM", "16GB", "32GB", "Visual Studio and Blender are memory-hungry; 16GB is the modern baseline for IT students.", "More memory keeps the laptop fast when multiple heavy tools are open.", 0.88f),
            SpecComponent("GPU", "Integrated", "RTX 3050 / 4050", "Blender and game development tools benefit significantly from a dedicated graphics card.", "A graphics card speeds up rendering and 3D work.", 0.65f),
            SpecComponent("Storage", "512GB SSD", "1TB NVMe SSD", "Large IDEs, projects, and assets require plenty of fast storage.", "Fast storage helps the laptop feel snappy and fits all your tools.", 0.85f)
        ),
        relatedCourses = listOf("Programming", "3D Modelling", "Databases", "Networking"),
        budget = PriceBand(11000, 18000, 13000, 22000, 7000, 12000)
    )

    "Bachelor of Science in Computer Science Education" -> ProgrammeProfile(
        name = "Bachelor of Science in Computer Science Education",
        faculty = "Faculty of Science and Engineering",
        summary = "A programme combining computer science principles with education modules for future technology educators.",
        software = listOf("Moodle", "VS Code", "Visual Studio", "Android Studio", "MySQL", "Python", "Java"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Compilers and emulators need decent sustained power.", "A stronger brain helps code build faster.", 0.78f),
            SpecComponent("RAM", "8GB", "16GB", "Android Studio and browser tabs can become heavy.", "More memory means fewer slowdowns.", 0.86f),
            SpecComponent("GPU", "Integrated", "RTX 3050", "Optional unless you do heavier media or 3D work.", "A graphics card is nice, but not the main need.", 0.4f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Development tools and school files take space.", "An SSD is the fast cupboard for your files.", 0.82f)
        ),
        relatedCourses = listOf("Programming", "Data Structures", "Computer Systems", "Education Modules"),
        budget = PriceBand(9000, 15000, 11000, 18000, 6000, 10000)
    )

    "Bachelor of Engineering (Electrical and Electronic)" -> ProgrammeProfile(
        name = "Bachelor of Engineering (Electrical and Electronic)",
        faculty = "Faculty of Science and Engineering",
        summary = "A five-year professional engineering programme covering electrical systems, electronics, and signals.",
        software = listOf("Moodle", "MATLAB", "Simulink", "Proteus", "KiCad", "Arduino IDE", "Office 365"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Simulation and calculation tasks benefit from stronger sustained CPU performance.", "The brain should be quick enough to keep up with engineering work.", 0.88f),
            SpecComponent("RAM", "8GB", "16GB - 32GB", "Engineering tools run better with more memory.", "More memory helps with heavy software.", 0.9f),
            SpecComponent("GPU", "Integrated", "RTX 3050 or better", "A dedicated GPU is useful but not always required.", "A graphics card can help, but memory and CPU matter first.", 0.55f),
            SpecComponent("Storage", "512GB SSD", "1TB SSD", "Engineering files and tools are large.", "You'll want plenty of room for projects.", 0.95f)
        ),
        relatedCourses = listOf("Circuits", "Electronics", "Signals", "Simulation"),
        budget = PriceBand(11000, 18000, 13000, 22000, 7000, 12000)
    )

    "Bachelor of Science in Information Science" -> ProgrammeProfile(
        name = "Bachelor of Science in Information Science",
        faculty = "Faculty of Science and Engineering",
        summary = "A programme balanced between information management, research, and data tools.",
        software = listOf("Moodle", "Office 365", "Database tools", "Research tools", "Library databases"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Information science is usually balanced between reading, research, and data tools.", "A normal brain is fine, just not too weak.", 0.52f),
            SpecComponent("RAM", "8GB", "16GB", "Research and database work feel much smoother with more memory.", "More memory helps when you have many documents and tabs open.", 0.68f),
            SpecComponent("GPU", "Integrated", "Integrated", "Not graphics-heavy.", "You do not need a strong graphics card here.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage helps with documents and software.", "An SSD keeps the laptop from feeling sluggish.", 0.7f)
        ),
        relatedCourses = listOf("Information Management", "Databases", "Research Methods"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    "Bachelor of Science in Geographic Information Science" -> ProgrammeProfile(
        name = "Bachelor of Science in Geographic Information Science",
        faculty = "Faculty of Science and Engineering",
        summary = "A specialized degree focused on spatial analysis, cartography, and remote sensing.",
        software = listOf("Moodle", "ArcGIS Pro", "QGIS", "Remote Sensing Tools", "Python", "Office 365"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "GIS workloads can be CPU-heavy when processing spatial layers and maps.", "The brain should be strong enough for map work.", 0.85f),
            SpecComponent("RAM", "16GB", "32GB", "GIS datasets can become very memory heavy.", "More memory helps maps open and move faster.", 0.95f),
            SpecComponent("GPU", "Integrated", "RTX 3050 / 4050", "A dedicated GPU helps with display responsiveness and some spatial work.", "A graphics card makes map work smoother.", 0.8f),
            SpecComponent("Storage", "512GB SSD", "1TB SSD", "Map files, rasters, and project data grow quickly.", "You need a lot of room because maps eat storage.", 0.94f)
        ),
        relatedCourses = listOf("GIS", "Remote Sensing", "Cartography", "Spatial Analysis"),
        budget = PriceBand(13000, 20000, 15000, 25000, 8000, 14000)
    )

    "Bachelor of Science in Actuarial and Financial Mathematics" -> ProgrammeProfile(
        name = "Bachelor of Science in Actuarial and Financial Mathematics",
        faculty = "Faculty of Science and Engineering",
        summary = "A degree focusing on risk modelling, financial mathematics, and advanced statistics.",
        software = listOf("Moodle", "Excel", "R", "Python", "Stata", "Mathematica"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i5 / Ryzen 7", "Mathematical and statistical work benefits from a modern responsive processor.", "The brain should be quick enough for calculations.", 0.7f),
            SpecComponent("RAM", "8GB", "16GB", "Statistical tools and spreadsheets feel much better with more memory.", "Memory helps when lots of numbers are open at once.", 0.72f),
            SpecComponent("GPU", "Integrated", "Integrated", "No graphics-heavy workload here.", "No special graphics card needed.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage is enough for models and files.", "An SSD keeps the laptop fast and tidy.", 0.6f)
        ),
        relatedCourses = listOf("Probability", "Statistics", "Financial Mathematics", "Risk Modelling"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    "Bachelor of Nursing Science" -> ProgrammeProfile(
        name = "Bachelor of Nursing Science",
        faculty = "Faculty of Health Sciences",
        summary = "A professional nursing programme preparing students for clinical practice and healthcare.",
        software = listOf("Moodle", "Office 365", "Library databases", "Reference management tools"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Nursing work is mostly research, essays, and online learning.", "A normal brain is fine for nursing studies.", 0.55f),
            SpecComponent("RAM", "8GB", "16GB", "More memory helps with research and multiple tabs.", "Memory helps when many things are open.", 0.65f),
            SpecComponent("GPU", "Integrated", "Integrated", "No graphics-heavy workload.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage is enough for nursing files.", "An SSD keeps the laptop fast.", 0.7f)
        ),
        relatedCourses = listOf("Anatomy", "Physiology", "Clinical Practice", "Community Health"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    "Bachelor of Education Primary" -> ProgrammeProfile(
        name = "Bachelor of Education Primary",
        faculty = "Faculty of Education",
        summary = "Teacher training programme for primary education with focus on pedagogy and subject knowledge.",
        software = listOf("Moodle", "Office 365", "Google Classroom", "Library databases"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Education work is mostly essays, lesson planning, and online learning.", "A normal brain is fine for education studies.", 0.55f),
            SpecComponent("RAM", "8GB", "16GB", "More memory helps with research and multiple tabs.", "Memory helps when many things are open.", 0.65f),
            SpecComponent("GPU", "Integrated", "Integrated", "No graphics-heavy workload.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage is enough for education files.", "An SSD keeps the laptop fast.", 0.7f)
        ),
        relatedCourses = listOf("Pedagogy", "Child Development", "Curriculum Studies", "Teaching Practice"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    "Bachelor of Arts in Humanities" -> ProgrammeProfile(
        name = "Bachelor of Arts in Humanities",
        faculty = "Faculty of Humanities",
        summary = "A broad humanities degree covering language, literature, history, and cultural studies.",
        software = listOf("Moodle", "Office 365", "Library databases", "Reference management tools"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Humanities work is mostly reading, essays, and research.", "A normal brain is fine for humanities.", 0.52f),
            SpecComponent("RAM", "8GB", "16GB", "More memory helps with research and multiple tabs.", "Memory helps when many things are open.", 0.65f),
            SpecComponent("GPU", "Integrated", "Integrated", "No graphics-heavy workload.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage is enough for humanities files.", "An SSD keeps the laptop fast.", 0.7f)
        ),
        relatedCourses = listOf("Literature", "History", "Languages", "Cultural Studies"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )

    else -> null
}

private fun getGeneralProfile(programmeName: String): ProgrammeProfile {
    val safeName = programmeName.ifBlank { "General Student Use" }
    return ProgrammeProfile(
        name = safeName,
        faculty = "General",
        summary = "A practical fallback profile for student work like browsing, Office apps, reading, online classes, and everyday assignments.",
        software = listOf("Moodle", "Office 365", "MS Teams", "Zoom", "Web browser", "Library databases"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Basic student work does not need a beast, but it should still feel responsive.", "The brain. Enough for essays and classes.", 0.55f),
            SpecComponent("RAM", "8GB", "16GB", "8GB works, but 16GB makes the laptop feel calmer.", "Memory helps when many tabs are open.", 0.65f),
            SpecComponent("GPU", "Integrated", "Integrated", "No serious graphics work.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "SSD speed matters more than huge hard drives.", "Fast storage makes everything feel quicker.", 0.7f)
        ),
        relatedCourses = listOf("General coursework"),
        budget = PriceBand(7000, 11000, 9000, 14000, 4000, 7000)
    )
}
