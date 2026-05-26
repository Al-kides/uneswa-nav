package com.uneswa.nav.data

// Standard DP Levenshtein — O(m*n) time, O(m*n) space.
// Fine for short query strings; not worth optimising here.
private fun lev(a: String, b: String): Int {
    val m = a.length; val n = b.length
    val d = Array(m + 1) { IntArray(n + 1) }
    for (i in 0..m) d[i][0] = i
    for (j in 0..n) d[0][j] = j
    for (i in 1..m) for (j in 1..n)
        d[i][j] = if (a[i-1] == b[j-1]) d[i-1][j-1]
                  else 1 + minOf(d[i-1][j], d[i][j-1], d[i-1][j-1])
    return d[m][n]
}

// Substring check first (fast path), then sliding-window Levenshtein.
// Threshold: 1 edit per 4 chars — tight enough to avoid false positives.
private fun matches(q: String, target: String): Boolean {
    if (target.contains(q)) return true
    val thr = (q.length / 4).coerceAtLeast(1)
    var i = 0
    while (i <= target.length - q.length) {
        if (lev(q, target.substring(i, i + q.length)) <= thr) return true
        i++
    }
    return false
}

class LocationRepo {

    val all: Array<Location> = arrayOf(

        Location(
            id     = "ide",
            name   = "Institute of Distance Education",
            abbr   = "IDE",
            codes  = arrayOf("IDE", "Distance Education"),
            desc   = "Handles distance learning programmes. First building on the left as you enter through the main gate, beside the sports ground.",
            photos = arrayOf("ide_1", "ide_2", "ide_3", "ide_4"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate.",
                        "Turn immediately left — IDE is the first building, right beside the sports ground."
                    )
                )
            )
        ),

        Location(
            id     = "engineering",
            name   = "Engineering Block",
            abbr   = "EE",
            codes  = arrayOf("EE", "EE 2.1", "EE 2.2", "EE 2.3", "EE 2.4", "EE 2.7"),
            desc   = "Faculty of Engineering lecture rooms and labs. Along the red pillar path, before the Sports Emporium.",
            photos = arrayOf("engineering_1", "engineering_2"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk toward the red pillars.",
                        "Follow the pillar path — Engineering is on your right before the Sports Emporium."
                    )
                ),
                Approach(
                    from  = "Admin",
                    steps = arrayOf(
                        "From Admin, walk toward the red pillars.",
                        "Follow the path — Engineering comes before the Sports Emporium."
                    )
                )
            )
        ),

        Location(
            id     = "se",
            name   = "Ligcabho LemaSwati Sports Emporium",
            abbr   = "SE",
            codes  = arrayOf("SE", "Sports Emporium", "Emporium"),
            desc   = "Main indoor sports facility — gym, courts, large events. The soccer field (the Butchery) is right beside it.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and follow the red pillar path.",
                        "Walk past the Engineering block.",
                        "The Sports Emporium is the large building at the end of the pillar path."
                    )
                ),
                Approach(
                    from  = "Chapel",
                    steps = arrayOf(
                        "With the Chapel behind you, walk back along the path.",
                        "The Sports Emporium is the building just before Engineering."
                    )
                )
            )
        ),

        Location(
            id     = "butchery",
            name   = "The Butchery (Soccer Field)",
            abbr   = "Butchery",
            codes  = arrayOf("Butchery", "Soccer Field", "Football Field", "Field"),
            desc   = "The main soccer field — nicknamed the Butchery. Right beside the Sports Emporium.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Follow the red pillar path past Engineering to the Sports Emporium.",
                        "The Butchery is the open field immediately beside the Emporium."
                    )
                )
            )
        ),

        Location(
            id     = "chapel",
            name   = "University Chapel",
            abbr   = "Chapel",
            codes  = arrayOf("Chapel", "Church"),
            desc   = "University chapel for services and ceremonies. Past the Sports Emporium along the red pillar path. Look for the cross.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter and follow the red pillar path.",
                        "Pass Engineering, then pass the Sports Emporium.",
                        "The Chapel is the next building — recognisable by the cross on its facade."
                    )
                ),
                Approach(
                    from  = "Sports Emporium",
                    steps = arrayOf(
                        "Continue along the path past the Emporium.",
                        "The Chapel is a short walk ahead."
                    )
                )
            )
        ),

        Location(
            id     = "admin",
            name   = "Administration Block",
            abbr   = "Admin",
            codes  = arrayOf("Admin", "Administration", "Registry", "Registrar"),
            desc   = "Main administration building — Registrar, student records, general admin. Clearly signed, visible from the main campus road.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and walk straight ahead along the main road.",
                        "Admin is the large signed building on your right."
                    )
                ),
                Approach(
                    from  = "Library",
                    steps = arrayOf(
                        "Face the Library entrance and turn right.",
                        "Admin is immediately to your right."
                    )
                )
            )
        ),

        Location(
            id     = "finance",
            name   = "Finance / Cash Office",
            abbr   = "Finance",
            codes  = arrayOf("Finance", "Cash Office", "Fees", "Bursary"),
            desc   = "Fee payments, financial clearance, bursaries. A short walk from the Admin block.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Admin",
                    steps = arrayOf(
                        "Walk out of Admin — Finance is a stone's throw away.",
                        "Follow the signs for Finance / Cash Office."
                    )
                ),
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Walk to the Admin block first.",
                        "Finance is a short signed walk from there."
                    )
                )
            )
        ),

        Location(
            id     = "ref",
            name   = "Refectory / Bamboo",
            abbr   = "Ref",
            codes  = arrayOf("Ref", "Refectory", "Bamboo", "Tuck Shop", "Canteen"),
            desc   = "Main student eating area. The bench and tuck shop area runs parallel to Admin. Bamboo is behind the bench area.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Admin",
                    steps = arrayOf(
                        "Walk straight past the Finance Office.",
                        "After about 15 seconds turn right.",
                        "The ref bench area is right there, parallel to Admin. Bamboo is behind it."
                    )
                )
            )
        ),

        Location(
            id     = "library",
            name   = "University Library",
            abbr   = "Lib",
            codes  = arrayOf("Library", "Lib"),
            desc   = "Main library — the large green-roofed building at the centre of campus. Hard to miss.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Walk straight in from the gate.",
                        "The Library is the large green-roofed building at the centre — visible from the main road."
                    )
                ),
                Approach(
                    from  = "Admin",
                    steps = arrayOf(
                        "Face Admin and turn left.",
                        "The Library is directly ahead."
                    )
                )
            )
        ),

        Location(
            id     = "commerce",
            name   = "Commerce Block",
            abbr   = "Com",
            codes  = arrayOf(
                "Com", "Commerce", "G Block",
                "G.001", "G.005", "G.006", "G.102", "G.103", "G.105", "G.106"
            ),
            desc   = "Faculty of Commerce. All G.xxx timetable rooms are here — G.001 is ground floor, higher numbers upstairs. ACF, BUS, ECO, LAW modules.",
            photos = arrayOf("commerce_1", "commerce_2", "commerce_3"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter and walk along the main road.",
                        "Commerce is in the science/engineering cluster on the right side of campus.",
                        "Follow G-block signs. Ground floor is G.001; upper rooms are numbered upward."
                    )
                ),
                Approach(
                    from  = "Admin",
                    steps = arrayOf(
                        "From Admin, head toward the science and engineering side.",
                        "Commerce (G-block) is in that cluster."
                    )
                )
            )
        ),

        Location(
            id     = "n_class",
            name   = "N-Block Classrooms",
            abbr   = "N",
            codes  = arrayOf("N1", "N2", "N3", "N Block", "N Classes"),
            desc   = "Three lecture rooms N1, N2, N3 in the same building. Near MFAC and the Warden's Office. Not the same as N-block girls hostel.",
            photos = arrayOf("n_class_1", "n_class_2", "n_class_3", "n_class_4", "n_class_5"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter and head to the left side of campus.",
                        "Walk toward the MFAC building — N classes are right next to it.",
                        "Rooms are labelled N1, N2, N3 on the doors."
                    )
                ),
                Approach(
                    from  = "Library",
                    steps = arrayOf(
                        "From the Library head left (west) toward the Warden's area.",
                        "N classes are the classroom block beside MFAC."
                    )
                )
            )
        ),

        Location(
            id     = "mfac",
            name   = "Motsepe Foundation Academic Complex",
            abbr   = "MFAC",
            codes  = arrayOf(
                "MFAC", "Motsepe",
                "MFAC Room 1", "MFAC Room 2", "MFAC Room 3", "MFAC Room 4", "MFAC Room 5"
            ),
            desc   = "New complex funded by UNESWA alumnus Patrice Motsepe (R10M donation, built 2023–24). 5 lecture rooms used heavily by Law, CS, and BSc IS. Next to N-block girls hostel and Warden's Office.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter and head left toward the N-area / Warden's cluster.",
                        "MFAC is the newer building there, next to the N-block girls hostel."
                    )
                ),
                Approach(
                    from  = "N Classes",
                    steps = arrayOf(
                        "MFAC is immediately adjacent to the N classes building."
                    )
                )
            )
        ),

        Location(
            id     = "warden",
            name   = "Warden's Office",
            abbr   = "Warden",
            codes  = arrayOf("Warden", "Accommodation", "Hostel Office"),
            desc   = "Accommodation, hostel allocation, residence matters. In the N-area cluster near N classes and MFAC.",
            photos = arrayOf("warden_1", "warden_2", "warden_3", "warden_4", "warden_5", "warden_6"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter and head left toward the N-area.",
                        "Walk past or alongside N classes and MFAC.",
                        "The Warden's Office is in that same cluster."
                    )
                ),
                Approach(
                    from  = "N Classes",
                    steps = arrayOf(
                        "From N classes, the Warden's Office is a short walk in the same cluster."
                    )
                )
            )
        ),

        Location(
            id     = "clinic",
            name   = "Campus Clinic",
            abbr   = "Clinic",
            codes  = arrayOf("Clinic", "Health", "Medical", "Nurse"),
            desc   = "Student health clinic. Directly beside the Warden's Office in the N-area cluster.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Warden's Office",
                    steps = arrayOf(
                        "The clinic is the building directly next to the Warden's Office.",
                        "Look for the red cross sign."
                    )
                ),
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Head left into the N-area cluster.",
                        "Find the Warden's Office — the clinic is right beside it."
                    )
                )
            )
        ),

        Location(
            id     = "weather",
            name   = "Weather Station (Stevenson Screen)",
            abbr   = "Weather Station",
            codes  = arrayOf("Weather Station", "Stevenson Screen", "Weather"),
            desc   = "Meteorological station with a Stevenson screen. Near the IDE and sports ground, below the tents area.",
            photos = arrayOf(),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        "Enter through the main gate and turn left toward IDE.",
                        "The weather station is near IDE, below the tents area beside the sports ground."
                    )
                )
            )
        )
    )

    fun byId(id: String) = all.find { it.id == id }

    fun search(raw: String): Array<Location> {
        val q = raw.trim().lowercase()
        if (q.isEmpty()) return all

        // Score: 0 = exact code match, 1 = corpus substring, 2 = fuzzy
        val hits = all.mapNotNull { loc ->
            val score = when {
                loc.codes.any { it.lowercase() == q } -> 0
                loc.corpus.contains(q)                -> 1
                matches(q, loc.corpus)                -> 2
                else                                  -> return@mapNotNull null
            }
            score to loc
        }

        return hits.sortedBy { it.first }.map { it.second }.toTypedArray()
    }
}
