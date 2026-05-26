package com.uneswa.nav.data

// ── Levenshtein ──────────────────────────────────────────────────────────────

fun levenshtein(a: String, b: String): Int {
    val m = a.length; val n = b.length
    val dp = Array(m + 1) { IntArray(n + 1) }
    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j
    for (i in 1..m) for (j in 1..n) {
        dp[i][j] = if (a[i-1] == b[j-1]) dp[i-1][j-1]
                   else 1 + minOf(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
    }
    return dp[m][n]
}

fun fuzzy_match(query: String, target: String): Boolean {
    val q = query.lowercase().trim()
    val t = target.lowercase()
    if (q.isEmpty()) return true
    if (t.contains(q)) return true
    val threshold = (q.length / 4).coerceAtLeast(1)
    var i = 0
    while (i <= t.length - q.length) {
        if (levenshtein(q, t.substring(i, i + q.length)) <= threshold) return true
        i++
    }
    return false
}

// ── Models ───────────────────────────────────────────────────────────────────

data class Approach(val from: String, val steps: Array<String>)

data class Location(
    val id: String,
    val full_name: String,
    val abbreviation: String,
    val alt_codes: Array<String>,
    val description: String,
    val photos: Array<String>,          // filenames in res/drawable, no extension
    val approaches: Array<Approach>
) {
    fun search_corpus() =
        "$full_name $abbreviation ${alt_codes.joinToString(" ")} $description".lowercase()
}

// ── Repository ───────────────────────────────────────────────────────────────

class LocationRepository {

    val locations: Array<Location> = arrayOf(

        Location(
            id = "ide",
            full_name = "Institute of Distance Education",
            abbreviation = "IDE",
            alt_codes = arrayOf("IDE", "Distance Education", "Distance Learning"),
            description = "The Institute of Distance Education. Located just left of the " +
                "main gate as you enter campus, next to the sports ground.",
            photos = arrayOf("ide_1", "ide_2", "ide_3", "ide_4"),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate.",
                        "Immediately turn left — the IDE building is right there " +
                            "next to the sports ground.",
                        "It is the first building on your left as you enter."
                    )
                )
            )
        ),

        Location(
            id = "engineering",
            full_name = "Engineering Block",
            abbreviation = "EE",
            alt_codes = arrayOf("EE", "Engineering", "EE 2.1", "EE 2.2", "EE 2.3",
                "EE 2.4", "EE 2.7"),
            description = "Houses the Faculty of Engineering. Contains EE lecture rooms " +
                "and labs. Located along the red pillar path before the Sports Emporium.",
            photos = arrayOf("engineering_1", "engineering_2"),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk toward the red pillars.",
                        "Follow the red pillar path straight ahead.",
                        "The Engineering block is on your right before you reach " +
                            "the Sports Emporium."
                    )
                ),
                Approach(
                    from = "Administration",
                    steps = arrayOf(
                        "From Admin, walk toward the red pillars.",
                        "Follow the pillar path — Engineering is along this route " +
                            "before the Sports Emporium."
                    )
                )
            )
        ),

        Location(
            id = "se",
            full_name = "Ligcabho LemaSwati Sports Emporium",
            abbreviation = "SE",
            alt_codes = arrayOf("SE", "Sports Emporium", "Ligcabho", "Emporium"),
            description = "The main indoor sports facility on campus. Used for the gym, " +
                "indoor courts, and large events. The soccer field (known as the Butchery) " +
                "is just beside it. Timetable code: SE.",
            photos = arrayOf(),   // placeholder — photos not yet taken
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and follow the red pillar path.",
                        "Walk past the Engineering block on your right.",
                        "The Sports Emporium is the large building at the end of " +
                            "the pillar path."
                    )
                ),
                Approach(
                    from = "Administration",
                    steps = arrayOf(
                        "From Admin, head toward the red pillars.",
                        "Follow the pillar path past Engineering.",
                        "The Sports Emporium is at the end of this path."
                    )
                )
            )
        ),

        Location(
            id = "butchery",
            full_name = "The Butchery (Soccer Field)",
            abbreviation = "Butchery",
            alt_codes = arrayOf("Butchery", "Soccer Field", "Football Field",
                "Sports Ground", "Field"),
            description = "The main soccer field on campus, affectionately known as " +
                "the Butchery. Located just beside the Sports Emporium.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Sports Emporium",
                    steps = arrayOf(
                        "Stand facing the Sports Emporium entrance.",
                        "The soccer field (Butchery) is immediately to the side of " +
                            "the building — you can see it from the entrance."
                    )
                ),
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Follow the red pillar path past Engineering to the Sports Emporium.",
                        "The Butchery soccer field is just beside the Emporium."
                    )
                )
            )
        ),

        Location(
            id = "chapel",
            full_name = "University Chapel",
            abbreviation = "Chapel",
            alt_codes = arrayOf("Chapel", "Church"),
            description = "The university chapel used for religious services and official " +
                "ceremonies. Located past the Sports Emporium along the red pillar path.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and follow the red pillar path.",
                        "Walk past Engineering, then past the Sports Emporium.",
                        "The Chapel is the next building along — look for the cross."
                    )
                ),
                Approach(
                    from = "Sports Emporium",
                    steps = arrayOf(
                        "With the Sports Emporium behind you, continue along the path.",
                        "The Chapel is a short walk ahead — recognisable by the cross " +
                            "on its facade."
                    )
                )
            )
        ),

        Location(
            id = "admin",
            full_name = "Administration Block",
            abbreviation = "Admin",
            alt_codes = arrayOf("Admin", "Administration", "Registry", "Registrar"),
            description = "The main administration building. Houses the Registrar, " +
                "student records, and other administrative offices. Visible from the " +
                "main road through campus.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate.",
                        "Walk straight ahead along the main campus road.",
                        "The Administration block is the large building on your right, " +
                            "clearly signed."
                    )
                ),
                Approach(
                    from = "Library",
                    steps = arrayOf(
                        "Stand facing the Library entrance.",
                        "Administration is the building immediately to your right."
                    )
                )
            )
        ),

        Location(
            id = "finance",
            full_name = "Finance Office / Cash Office",
            abbreviation = "Finance",
            alt_codes = arrayOf("Finance", "Cash Office", "Fees", "Bursary", "Cash"),
            description = "Handles fee payments, financial clearance, and bursary queries. " +
                "A short walk from the Admin block.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Administration",
                    steps = arrayOf(
                        "From the Admin block, the Finance Office is just a short " +
                            "distance away — roughly a stone's throw.",
                        "Walk out of Admin and head toward the Finance Office sign."
                    )
                ),
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk to the Admin block.",
                        "The Finance/Cash Office is a short walk from Admin — " +
                            "follow the signs."
                    )
                )
            )
        ),

        Location(
            id = "ref",
            full_name = "Refectory / Tuck Shop (Bamboo Area)",
            abbreviation = "Ref",
            alt_codes = arrayOf("Ref", "Refectory", "Tuck Shop", "Bamboo",
                "Canteen", "Food"),
            description = "The main student eating area. The refectory and tuck shop " +
                "bench area runs parallel to the Admin building. Bamboo is behind " +
                "the bench/ref area.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Administration",
                    steps = arrayOf(
                        "From the Admin block, walk straight past the Finance Office.",
                        "After about 15 seconds, turn right.",
                        "The refectory bench area (Bamboo) is right there, " +
                            "running parallel to Admin."
                    )
                )
            )
        ),

        Location(
            id = "library",
            full_name = "University Library",
            abbreviation = "Lib",
            alt_codes = arrayOf("Library", "Lib", "Books"),
            description = "The main university library. The green-roofed building at the " +
                "centre of campus. Hard to miss.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk straight ahead.",
                        "The Library is the large building with the distinctive green roof " +
                            "at the centre of campus — you can see it from the main road."
                    )
                ),
                Approach(
                    from = "Administration",
                    steps = arrayOf(
                        "Stand facing Admin and turn left.",
                        "The Library is the green-roofed building directly ahead of you."
                    )
                )
            )
        ),

        Location(
            id = "commerce",
            full_name = "Commerce Block",
            abbreviation = "Com",
            alt_codes = arrayOf("Com", "Commerce", "G Block", "G.001", "G.005",
                "G.006", "G.102", "G.103", "G.105", "G.106"),
            description = "Houses the Faculty of Commerce. All G.xxx timetable rooms " +
                "(G.001, G.102, G.103, G.105, G.106 etc.) are in this building. " +
                "Used for ACF, BUS, ECO, LAW and commerce modules.",
            photos = arrayOf("commerce_1", "commerce_2", "commerce_3"),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk along the main campus road.",
                        "The Commerce block is in the science and engineering cluster " +
                            "on the right side of campus.",
                        "Look for the G-block signs. G.001 is on the ground floor, " +
                            "higher numbers are upstairs."
                    )
                ),
                Approach(
                    from = "Administration",
                    steps = arrayOf(
                        "From Admin, head toward the science and engineering side of campus.",
                        "The Commerce block is in that cluster — follow signs for G-block."
                    )
                )
            )
        ),

        Location(
            id = "n_classes",
            full_name = "N-Block Classrooms (N1, N2, N3)",
            abbreviation = "N",
            alt_codes = arrayOf("N1", "N2", "N3", "N Block", "N Classes", "N class"),
            description = "Three lecture rooms N1, N2, N3 in the same building. " +
                "Located near MFAC and the Warden's Office. " +
                "Note: not to be confused with N-block girls hostel nearby.",
            photos = arrayOf("n_class_1", "n_class_2", "n_class_3",
                             "n_class_4", "n_class_5"),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk toward the left side of campus.",
                        "Head toward the MFAC building — N classes are right next to it.",
                        "The building is also close to the Warden's Office."
                    )
                ),
                Approach(
                    from = "Library",
                    steps = arrayOf(
                        "From the Library, head to the left (west) side of campus.",
                        "Walk toward the Warden's Office area.",
                        "N classes are the classroom building next to MFAC."
                    )
                )
            )
        ),

        Location(
            id = "mfac",
            full_name = "Motsepe Foundation Academic Complex",
            abbreviation = "MFAC",
            alt_codes = arrayOf("MFAC", "Motsepe", "MFAC Room 1", "MFAC Room 2",
                "MFAC Room 3", "MFAC Room 4", "MFAC Room 5"),
            description = "New academic complex funded by South African billionaire and " +
                "UNESWA alumnus Patrice Motsepe (R10 million donation; built 2023-2024). " +
                "Contains 5 lecture rooms heavily used by Law, CS, and BSc IS. " +
                "Located next to N-block girls hostel and the Warden's Office.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and head to the left side of campus.",
                        "Walk toward the Warden's Office / N-area cluster.",
                        "MFAC is the newer building in that cluster — " +
                            "next to the N-block girls hostel."
                    )
                ),
                Approach(
                    from = "N Classes",
                    steps = arrayOf(
                        "MFAC is right next to the N classes building.",
                        "It is the newer building immediately adjacent."
                    )
                )
            )
        ),

        Location(
            id = "warden",
            full_name = "Warden's Office",
            abbreviation = "Warden",
            alt_codes = arrayOf("Warden", "Warden's", "Accommodation", "Hostel Office"),
            description = "Handles student accommodation, hostel allocation, and " +
                "residence matters. Located in the N-area cluster near N classes and MFAC.",
            photos = arrayOf("warden_1", "warden_2", "warden_3",
                             "warden_4", "warden_5", "warden_6"),
            approaches = arrayOf(
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and head left toward the N-area.",
                        "Walk past or alongside N classes and MFAC.",
                        "The Warden's Office is in that same cluster — look for the sign."
                    )
                ),
                Approach(
                    from = "N Classes",
                    steps = arrayOf(
                        "From N classes, the Warden's Office is right in the same area.",
                        "It is a short walk — look for the Warden sign."
                    )
                )
            )
        ),

        Location(
            id = "clinic",
            full_name = "Campus Clinic",
            abbreviation = "Clinic",
            alt_codes = arrayOf("Clinic", "Health", "Medical", "Nurse", "Doctor"),
            description = "The student health clinic. Located next to the Warden's Office " +
                "in the N-area cluster.",
            photos = arrayOf(),
            approaches = arrayOf(
                Approach(
                    from = "Warden's Office",
                    steps = arrayOf(
                        "The clinic is directly next to the Warden's Office.",
                        "Look for the red cross sign."
                    )
                ),
                Approach(
                    from = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and head left toward the N-area.",
                        "Find the Warden's Office — the clinic is right beside it."
                    )
                )
            )
        )
    )

    // ── Search ───────────────────────────────────────────────────────────────

    fun search(raw_query: String): Array<Location> {
        val q = raw_query.trim()
        if (q.isEmpty()) return locations

        val scored = locations.mapNotNull { loc ->
            val corpus = loc.search_corpus()
            val score = when {
                loc.alt_codes.any { it.lowercase() == q.lowercase() } -> 0
                corpus.contains(q.lowercase()) -> 1
                fuzzy_match(q, corpus) -> 2
                else -> return@mapNotNull null
            }
            Pair(score, loc)
        }

        return scored.sortedBy { it.first }.map { it.second }.toTypedArray()
    }

    fun get_by_id(id: String): Location? = locations.find { it.id == id }
}
