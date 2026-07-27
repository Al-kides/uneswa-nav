package com.uneswa.nav.ui
// yoinked stuff from prospectus and other things from memory with the people I helped
//import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uneswa.nav.*
import kotlin.math.cos
import kotlin.math.sin

private data class FacultyProfile(
    val name: String,
    val description: String,
    val campus: String,
    val programmes: List<String>
)

private data class ProgrammeProfile(
    val name: String,
    val faculty: String,
    val summary: String,
    val software: List<String>,
    val components: List<SpecComponent>,
    val relatedCourses: List<String>,
    val budget: String
)

private data class SpecComponent(
    val name: String,
    val min: String,
    val rec: String,
    val technicalReason: String,
    val laymanReason: String,
    val weight: Float
)

private data class HexStat(
    val label: String,
    val value: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaptopRecommenderScreen(
    onBack: () -> Unit = {}
) {
    var selectedFaculty by remember { mutableStateOf<FacultyProfile?>(null) }
    var selectedProgramme by remember { mutableStateOf<String?>(null) }
    var showLayman by remember { mutableStateOf(false) }

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
            )
        )
    }

    val programme = selectedProgramme?.let { getProfile(it) } ?: getGeneralProfile(selectedProgramme ?: "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            selectedProgramme != null -> selectedProgramme!!
                            selectedFaculty != null -> selectedFaculty!!.name
                            else -> "UNESWA Laptop Guide"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            selectedProgramme != null -> {
                                selectedProgramme = null
                                showLayman = false
                            }
                            selectedFaculty != null -> selectedFaculty = null
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
                selectedProgramme != null -> ProgrammeDetailScreen(
                    profile = programme,
                    showLayman = showLayman,
                    onToggleLayman = { showLayman = it },
                    onBackToFaculty = {
                        selectedProgramme = null
                        showLayman = false
                    }
                )

                selectedFaculty != null -> ProgrammeListScreen(
                    faculty = selectedFaculty!!,
                    onSelect = { selectedProgramme = it }
                )

                else -> FacultyLandingScreen(
                    faculties = faculties,
                    onSelectFaculty = { selectedFaculty = it }
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
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard()
        }

        item {
            StatsStrip(
                items = listOf(
                    "8 faculties",
                    "60+ programmes",
                    "Kwaluseni focus",
                    "Prospectus-based"
                )
            )
        }

        item {
            SectionTitle("Choose a faculty")
        }

        items(faculties) { faculty ->
            FacultyCard(faculty = faculty, onClick = { onSelectFaculty(faculty) })
        }
    }
}

@Composable
private fun ProgrammeListScreen(
    faculty: FacultyProfile,
    onSelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            FacultyBanner(faculty)
        }

        item {
            Text(
                "Programmes",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(faculty.programmes) { programme ->
            val specific = getProfile(programme) != null
            ProgrammeCard(programme, specific) { onSelect(programme) }
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
    val cpu = profile.components.firstOrNull { it.name == "CPU" }?.weight ?: 0.6f
    val ram = profile.components.firstOrNull { it.name == "RAM" }?.weight ?: 0.6f
    val storage = profile.components.firstOrNull { it.name == "Storage" }?.weight ?: 0.6f
    val gpu = profile.components.firstOrNull { it.name == "GPU" }?.weight ?: 0.5f
    val port = if (profile.name.contains("Engineering", true) || profile.name.contains("Computer", true)) 0.7f else 0.4f
    val battery = if (profile.name.contains("Education", true) || profile.name.contains("Humanities", true)) 0.8f else 0.55f

    val hexStats = listOf(
        HexStat("CPU", cpu),
        HexStat("RAM", ram),
        HexStat("GPU", gpu),
        HexStat("STOR", storage),
        HexStat("PORT", port),
        HexStat("BAT", battery)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DetailHero(profile, showLayman, onToggleLayman)
        }

        item {
            RadarHexCard(
                stats = hexStats,
                title = if (showLayman) "Simple shape view" else "Technical shape view",
                subtitle = if (showLayman) "Bigger shape = more need." else "This gives a quick workload profile."
            )
        }

        item {
            BudgetAndNotes(profile, showLayman)
        }

        item {
            SectionTitle(if (showLayman) "What this means" else "Component details")
        }

        items(profile.components) { component ->
            ComponentDetailCard(component, showLayman)
        }

        item {
            SoftwareCard(profile.software)
        }

        item {
            CourseCard(profile.relatedCourses)
        }

        item {
            Button(
                onClick = onBackToFaculty,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Back to programmes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HeroCard() {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDark) {
                        Brush.linearGradient(colors = listOf(UneswaRed2, MaterialTheme.colorScheme.surface, DarkCard2))
                    } else {
                        Brush.linearGradient(colors = listOf(Color(0xFFFFEBEE), Color.White))
                    }
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "UNESWA • Kwaluseni",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "Pick a laptop that actually fits your programme.",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                )
                Text(
                    "Compare technical specs and simple explanations side-by-side to make the best choice for your studies.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun StatsStrip(items: List<String>) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant),
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp,
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
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(faculty.name, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(faculty.description, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(faculty.campus)
                TagChip("${faculty.programmes.size} programmes")
            }
        }
    }
}

@Composable
private fun FacultyCard(faculty: FacultyProfile, onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
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
                Text(faculty.name, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(faculty.description, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Text(
                    faculty.campus,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun ProgrammeCard(programme: String, specific: Boolean, onClick: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (specific) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (specific) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(programme, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    if (specific) "Specific laptop profile available" else "General student profile",
                    color = if (specific) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (specific) {
                Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
                    Text(
                        "SPECIFIC",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp,
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
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isDark) {
                        Brush.radialGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), MaterialTheme.colorScheme.surface),
                            radius = 1100f
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), Color.White)
                        )
                    }
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.Center
                ) {
                    TagChip(profile.faculty)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            if (showLayman) "Simple view" else "Technical view",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = showLayman,
                            onCheckedChange = onToggleLayman,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.scale(0.8f).height(24.dp)
                        )
                    }
                }
                Text(
                    profile.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                )
                Text(
                    profile.summary,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    profile.software.take(4).forEach { TagChip(it) }
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BudgetAndNotes(profile: ProgrammeProfile, showLayman: Boolean) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Estimated budget",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelLarge
            )
            Text(profile.budget, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                if (showLayman) {
                    "This is the rough price range for a laptop that should feel comfortable for this programme."
                } else {
                    "Use this as a practical buying band, not a strict number."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ComponentDetailCard(component: SpecComponent, showLayman: Boolean) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    val animatedWeight by animateFloatAsState(targetValue = component.weight, label = "weight")
    val accent = if (component.weight >= 0.75f) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    component.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = accent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        if (showLayman) "EASY" else "TECH",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecRow("Minimum", component.min)
                SpecRow("Recommended", component.rec, highlight = true)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { animatedWeight },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = accent,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                )
            }

            Text(
                text = if (showLayman) component.laymanReason else component.technicalReason,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            "$label:",
            color = if (highlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.width(110.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = if (highlight) FontWeight.Medium else FontWeight.Normal,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SoftwareCard(software: List<String>) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                "Common software",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                software.forEach { TagChip(it) }
            }
        }
    }
}

@Composable
private fun CourseCard(courses: List<String>) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                "Relevant courses",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                courses.joinToString("  •  "),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun RadarHexCard(stats: List<HexStat>, title: String, subtitle: String) {
    val isDark = MaterialTheme.colorScheme.background == DarkBg
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isDark) LineSoft else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                title,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelLarge
            )
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            
            // Increased height to prevent clipping and give labels room
            Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                val accentColor = MaterialTheme.colorScheme.primary
                val pointColor = MaterialTheme.colorScheme.secondary
                val textColor = MaterialTheme.colorScheme.onSurface
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    
                    // Reduced radius slightly to give more room for labels at the edges
                    val radius = minOf(size.width, size.height) * 0.28f
                    val outer = radius * 1.3f
                    val points = 6
                    val step = (2 * Math.PI / points).toFloat()

                    // Draw axis lines
                    repeat(points) { i ->
                        val angle = step * i - Math.PI.toFloat() / 2f
                        val x = cx + cos(angle) * outer
                        val y = cy + sin(angle) * outer
                        drawLine(if (isDark) LineSoft else textColor.copy(alpha = 0.1f), Offset(cx, cy), Offset(x, y), 1.5f)
                    }

                    // Draw web rings
                    val rings = listOf(0.35f, 0.65f, 1f)
                    rings.forEach { mult ->
                        val ring = (0 until points).map { i ->
                            val angle = step * i - Math.PI.toFloat() / 2f
                            Offset(cx + cos(angle) * outer * mult, cy + sin(angle) * outer * mult)
                        }
                        for (i in ring.indices) {
                            drawLine(
                                color = if (isDark) Color.White.copy(alpha = 0.08f) else textColor.copy(alpha = 0.05f),
                                start = ring[i],
                                end = ring[(i + 1) % ring.size],
                                strokeWidth = 2f
                            )
                        }
                    }

                    // Calculate data points
                    val fillPoints = stats.mapIndexed { index, stat ->
                        val angle = step * index - Math.PI.toFloat() / 2f
                        val len = outer * (0.2f + (0.8f * stat.value))
                        Offset(cx + cos(angle) * len, cy + sin(angle) * len)
                    }

                    // Draw filled area
                    drawPath(
                        path = Path().apply {
                            moveTo(fillPoints.first().x, fillPoints.first().y)
                            fillPoints.drop(1).forEach { lineTo(it.x, it.y) }
                            close()
                        },
                        color = accentColor.copy(alpha = 0.25f)
                    )

                    // Draw points and labels
                    fillPoints.forEachIndexed { index, pt ->
                        drawCircle(pointColor, radius = 7f, center = pt)
                        
                        val labelAngle = step * index - Math.PI.toFloat() / 2f
                        // Move labels further out so they don't overlap the graph
                        val labelDistance = outer * 1.35f 
                        val lx = cx + cos(labelAngle) * labelDistance
                        val ly = cy + sin(labelAngle) * labelDistance
                        
                        drawContext.canvas.nativeCanvas.drawText(
                            stats[index].label,
                            lx,
                            ly + 10f, // Centering adjustment
                            android.graphics.Paint().apply {
                                color = textColor.toArgb()
                                textSize = 34f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HexGlowMini() {
    val accent = MaterialTheme.colorScheme.secondary
    val primary = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = size.minDimension * 0.33f
        drawCircle(color = accent.copy(alpha = 0.12f), radius = radius)
        drawCircle(color = primary.copy(alpha = 0.12f), radius = radius * 0.7f)
        repeat(6) { i ->
            val angle = (Math.PI * 2 / 6 * i - Math.PI / 2).toFloat()
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(cx, cy),
                end = Offset(cx + cos(angle) * radius, cy + sin(angle) * radius),
                strokeWidth = 2f
            )
        }
    }
}

private fun getProfile(programme: String): ProgrammeProfile? = when (programme) {
    "Bachelor of Commerce" -> ProgrammeProfile(
        name = "Bachelor of Commerce",
        faculty = "Faculty of Commerce",
        summary = "A four-year full-time programme with major routes in Accounting and Finance, Management, and Marketing.",
        software = listOf("Excel", "QuickBooks", "Sage", "Browser"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Business workloads are mostly spreadsheets, browser tabs, and office apps, so you want a fast everyday chip.", "A decent brain is enough; it should open files quickly and not freeze on normal work.", 0.55f),
            SpecComponent("RAM", "8GB", "16GB", "Commerce work gets better with extra memory once spreadsheets and browser tabs grow.", "More memory means less slowing down when many things are open.", 0.6f),
            SpecComponent("GPU", "Integrated", "Integrated", "No serious graphics load here.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "SSD speed helps the laptop feel responsive through long semesters.", "Fast storage makes the whole laptop feel less tired.", 0.75f)
        ),
        relatedCourses = listOf("Accounting", "Finance", "Management", "Marketing"),
        budget = "SZL 5,500 - SZL 11,500"
    )

    "Bachelor of Arts in Journalism and Mass Communication" -> ProgrammeProfile(
        name = "Bachelor of Arts in Journalism and Mass Communication",
        faculty = "Faculty of Humanities",
        summary = "A four-year undergraduate degree focused on journalism and media, combining theory with practical training.",
        software = listOf("Premiere Pro", "Photoshop", "InDesign", "Audition"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Editing and export work are much smoother with stronger multi-core performance.", "The brain matters a lot when editing video or audio.", 0.85f),
            SpecComponent("RAM", "16GB", "16GB - 32GB", "Adobe apps and multitasking chew memory fast.", "More memory helps editing feel smoother.", 0.9f),
            SpecComponent("GPU", "Integrated", "RTX 3050 / 4050", "A dedicated GPU helps with timelines, playback, and rendering.", "A graphics card helps when you’re working with video or visuals.", 0.8f),
            SpecComponent("Storage", "512GB SSD", "1TB SSD", "Media files fill storage quickly.", "Video and audio files eat space fast.", 0.92f)
        ),
        relatedCourses = listOf("Reporting", "Media Production", "Layout", "Video Editing"),
        budget = "SZL 14,000 - SZL 25,000+"
    )

    "Bachelor of Arts in Social Science" -> ProgrammeProfile(
        name = "Bachelor of Arts in Social Science",
        faculty = "Faculty of Social Sciences",
        summary = "A degree covering key disciplines like sociology, political science, social work, economics, demography, and statistics.",
        software = listOf("SPSS", "RStudio", "Stata", "Excel"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i5 / Ryzen 7", "Data handling and analysis benefit from a balanced modern chip.", "A good brain keeps data work moving without lag.", 0.7f),
            SpecComponent("RAM", "8GB", "16GB", "More memory is useful when working with datasets and research tabs.", "Memory helps when many things are open.", 0.68f),
            SpecComponent("GPU", "Integrated", "Integrated", "This is not graphics-heavy.", "No special graphics card needed.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage makes loading files and apps easier.", "An SSD makes the laptop feel quicker.", 0.7f)
        ),
        relatedCourses = listOf("Sociology", "Political Science", "Economics", "Statistics"),
        budget = "SZL 5,500 - SZL 11,500"
    )

    "Bachelor of Science in Information Technology" -> ProgrammeProfile(
        name = "Bachelor of Science in Information Technology",
        faculty = "Faculty of Science and Engineering",
        summary = "An undergraduate programme focused on computing, software development, and information systems.",
        software = listOf("VS Code", "Visual Studio", "Blender", "Git", "Python", "MySQL"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Software development and 3D modeling in Blender require strong multi-core performance.", "The brain needs to be powerful for coding and rendering.", 0.82f),
            SpecComponent("RAM", "16GB", "16GB - 32GB", "Visual Studio and Blender are memory-hungry; 16GB is the modern baseline for IT students.", "More memory keeps the laptop fast when multiple heavy tools are open.", 0.88f),
            SpecComponent("GPU", "Integrated", "RTX 3050 / 4050", "Blender and game development tools benefit significantly from a dedicated graphics card.", "A graphics card speeds up rendering and 3D work.", 0.65f),
            SpecComponent("Storage", "512GB SSD", "1TB NVMe SSD", "Large IDEs, projects, and assets require plenty of fast storage.", "Fast storage helps the laptop feel snappy and fits all your tools.", 0.85f)
        ),
        relatedCourses = listOf("Programming", "3D Modelling", "Databases", "Networking"),
        budget = "SZL 12,500 - SZL 22,500"
    )

    "Bachelor of Science in Computer Science Education" -> ProgrammeProfile(
        name = "Bachelor of Science in Computer Science Education",
        faculty = "Faculty of Science and Engineering",
        summary = "A programme combining computer science principles with education modules for future technology educators.",
        software = listOf("VS Code", "Visual Studio", "Android Studio", "MySQL"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Compilers and emulators need decent sustained power.", "A stronger brain helps code build faster.", 0.78f),
            SpecComponent("RAM", "8GB", "16GB", "Android Studio and browser tabs can become heavy.", "More memory means fewer slowdowns.", 0.86f),
            SpecComponent("GPU", "Integrated", "RTX 3050", "Optional unless you do heavier media or 3D work.", "A graphics card is nice, but not the main need.", 0.4f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Development tools and school files take space.", "An SSD is the fast cupboard for your files.", 0.82f)
        ),
        relatedCourses = listOf("Programming", "Data Structures", "Computer Systems", "Education Modules"),
        budget = "SZL 9,000 - SZL 16,500"
    )

    "Bachelor of Engineering (Electrical and Electronic)" -> ProgrammeProfile(
        name = "Bachelor of Engineering (Electrical and Electronic)",
        faculty = "Faculty of Science and Engineering",
        summary = "A five-year professional engineering programme covering electrical systems, electronics, and signals.",
        software = listOf("MATLAB", "Simulink", "Proteus", "KiCad", "Arduino IDE"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "Simulation and calculation tasks benefit from stronger sustained CPU performance.", "The brain should be quick enough to keep up with engineering work.", 0.88f),
            SpecComponent("RAM", "8GB", "16GB - 32GB", "Engineering tools run better with more memory.", "More memory helps with heavy software.", 0.9f),
            SpecComponent("GPU", "Integrated", "RTX 3050 or better", "A dedicated GPU is useful but not always required.", "A graphics card can help, but memory and CPU matter first.", 0.55f),
            SpecComponent("Storage", "512GB SSD", "1TB SSD", "Engineering files and tools are large.", "You’ll want plenty of room for projects.", 0.95f)
        ),
        relatedCourses = listOf("Circuits", "Electronics", "Signals", "Simulation"),
        budget = "SZL 9,000 - SZL 16,500"
    )

    "Bachelor of Science in Information Science" -> ProgrammeProfile(
        name = "Bachelor of Science in Information Science",
        faculty = "Faculty of Science and Engineering",
        summary = "A programme balanced between information management, research, and data tools.",
        software = listOf("Office 365", "Databases", "Research tools", "Browser"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Information science is usually balanced between reading, research, and data tools.", "A normal brain is fine, just not too weak.", 0.52f),
            SpecComponent("RAM", "8GB", "16GB", "Research and database work feel much smoother with more memory.", "More memory helps when you have many documents and tabs open.", 0.68f),
            SpecComponent("GPU", "Integrated", "Integrated", "Not graphics-heavy.", "You do not need a strong graphics card here.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage helps with documents and software.", "An SSD keeps the laptop from feeling sluggish.", 0.7f)
        ),
        relatedCourses = listOf("Information Management", "Databases", "Research Methods"),
        budget = "SZL 5,500 - SZL 11,500"
    )

    "Bachelor of Science in Geographic Information Science" -> ProgrammeProfile(
        name = "Bachelor of Science in Geographic Information Science",
        faculty = "Faculty of Science and Engineering",
        summary = "A specialized degree focused on spatial analysis, cartography, and remote sensing.",
        software = listOf("ArcGIS Pro", "QGIS", "Remote Sensing Tools", "Python"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i7 / Ryzen 7", "GIS workloads can be CPU-heavy when processing spatial layers and maps.", "The brain should be strong enough for map work.", 0.85f),
            SpecComponent("RAM", "16GB", "32GB", "GIS datasets can become very memory heavy.", "More memory helps maps open and move faster.", 0.95f),
            SpecComponent("GPU", "Integrated", "RTX 3050 / 4050", "A dedicated GPU helps with display responsiveness and some spatial work.", "A graphics card makes map work smoother.", 0.8f),
            SpecComponent("Storage", "512GB SSD", "1TB SSD", "Map files, rasters, and project data grow quickly.", "You need a lot of room because maps eat storage.", 0.94f)
        ),
        relatedCourses = listOf("GIS", "Remote Sensing", "Cartography", "Spatial Analysis"),
        budget = "SZL 14,000 - SZL 25,000+"
    )

    "Bachelor of Science in Actuarial and Financial Mathematics" -> ProgrammeProfile(
        name = "Bachelor of Science in Actuarial and Financial Mathematics",
        faculty = "Faculty of Science and Engineering",
        summary = "A degree focusing on risk modelling, financial mathematics, and advanced statistics.",
        software = listOf("Excel", "R", "Python", "Stata", "Mathematica"),
        components = listOf(
            SpecComponent("CPU", "Core i5 / Ryzen 5", "Core i5 / Ryzen 7", "Mathematical and statistical work benefits from a modern responsive processor.", "The brain should be quick enough for calculations.", 0.7f),
            SpecComponent("RAM", "8GB", "16GB", "Statistical tools and spreadsheets feel much better with more memory.", "Memory helps when lots of numbers are open at once.", 0.72f),
            SpecComponent("GPU", "Integrated", "Integrated", "No graphics-heavy workload here.", "No special graphics card needed.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "Fast storage is enough for models and files.", "An SSD keeps the laptop fast and tidy.", 0.6f)
        ),
        relatedCourses = listOf("Probability", "Statistics", "Financial Mathematics", "Risk Modelling"),
        budget = "SZL 5,500 - SZL 11,500"
    )

    else -> null
}

private fun getGeneralProfile(programmeName: String): ProgrammeProfile {
    val safeName = programmeName.ifBlank { "General Student Use" }
    return ProgrammeProfile(
        name = safeName,
        faculty = "General",
        summary = "A practical fallback profile for student work like browsing, Office apps, reading, online classes, and everyday assignments.",
        software = listOf("Office 365", "MS Teams", "Zoom", "Browser"),
        components = listOf(
            SpecComponent("CPU", "Core i3 / Ryzen 3", "Core i5 / Ryzen 5", "Basic student work does not need a beast, but it should still feel responsive.", "The brain. Enough for essays and classes.", 0.55f),
            SpecComponent("RAM", "8GB", "16GB", "8GB works, but 16GB makes the laptop feel calmer.", "Memory helps when many tabs are open.", 0.65f),
            SpecComponent("GPU", "Integrated", "Integrated", "No serious graphics work.", "Built-in graphics are fine.", 0.2f),
            SpecComponent("Storage", "256GB SSD", "512GB SSD", "SSD speed matters more than huge hard drives.", "Fast storage makes everything feel quicker.", 0.7f)
        ),
        relatedCourses = listOf("General coursework"),
        budget = "SZL 5,500 - SZL 11,500"
    )
}