package com.uneswa.nav.data

// Standard DP Levenshtein — O(m*n) time, O(m*n) space.
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
            id     = "admin",
            name   = "Administrator Block",
            abbr   = "Admin",
            codes  = arrayOf("Admin", "Academics", "Academics Office", "Administration"),
            desc   = "Main administration building — houses the Academics Office, student records, and general university admin.",
            photos = arrayOf("admin_1", "admin_2", "admin_3", "admin_4", "admin_5", "admin_6", "admin_7"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter from the main gate."),
                        Step("Take the path on the right and walk straight, past buildings on the left."),
                        Step("Once you see a tall white building, turn left."),
                        Step("Descend the parking lot and stick to its left."),
                        Step("Walk down to the archway that leads toward the red and white buildings."),
                        Step("Follow the path and stay on the right."),
                        Step("The Admin office is opposite the bamboo plants.")
                    )
                )
            )
        ),

        Location(
            id     = "com",
            name   = "Commerce Building",
            abbr   = "Com",
            codes  = arrayOf("Com", "Commerce", "G Block"),
            desc   = "Faculty of Commerce lecture rooms and offices.",
            photos = arrayOf("com_1", "com_2", "com_3"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate and turn to your left and walk down."),
                        Step("Continue walking down the path until you find a parking lot."),
                        Step("That is the Commerce building; walk into the space under the archway.")
                    )
                )
            )
        ),

        Location(
            id     = "se",
            name   = "Sports Emporium",
            abbr   = "SE",
            codes  = arrayOf("SE", "Emporium", "Sports", "King Graduation Party", "Events", "Gym", "Pool"),
            desc   = "The largest indoor venue on campus. Houses the university gym, swimming pool, and is next to the courts. Primary venue for graduation after parties.",
            photos = arrayOf("se_1", "se_2", "se_3", "se_4"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate and take the path on the right."),
                        Step("Walk straight past the buildings and parking lots on the left."),
                        Step("Walk past the other parking lots."),
                        Step("The Emporium is the large white and grey building on your right.")
                    )
                )
            )
        ),

        Location(
            id     = "engineering",
            name   = "Engineering Block",
            abbr   = "Eng",
            codes  = arrayOf("EE", "Engineering", "Eng"),
            desc   = "Faculty of Engineering lecture rooms and laboratories.",
            photos = arrayOf("engineering_1", "engineering_2"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate and walk down the path on the right."),
                        Step("The Engineering Block is the first building to your left.")
                    )
                )
            )
        ),

        Location(
            id     = "finance",
            name   = "Finance Office",
            abbr   = "Finance",
            codes  = arrayOf("Finance", "Cash Office", "Fees"),
            desc   = "Fee payments, financial clearance, and bursary queries.",
            photos = arrayOf("finance_1", "finance_2", "finance_3", "finance_4", "finance_5", "finance_6", "finance_7", "finance_8"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Go down the long walk."),
                        Step("Enter the hall intersection."),
                        Step("Turn right and walk straight."),
                        Step("Follow the path."),
                        Step("Continue walking straight past the large gap."),
                        Step("Walk straight."),
                        Step("The Finance office is the white building to the left.")
                    )
                )
            )
        ),

        Location(
            id     = "fnb",
            name   = "FNB Cyberspace",
            abbr   = "FNB",
            codes  = arrayOf("FNB", "Cyberspace"),
            desc   = "A computer lab and internet hub for students, sponsored by FNB.",
            photos = arrayOf("fnb_1", "fnb_2", "fnb_3", "fnb_4", "fnb_5", "fnb_6", "fnb_7", "fnb_8"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Go down the long walk."),
                        Step("Enter the hall intersection."),
                        Step("Turn right and walk straight."),
                        Step("Follow the path."),
                        Step("Continue walking straight past the large gap."),
                        Step("Walk straight."),
                        Step("FNB Cyberspace is to your right; it has a green sign.")
                    )
                )
            )
        ),

        Location(
            id     = "ide",
            name   = "Institute of Distance Learning (IDE)",
            abbr   = "IDE",
            codes  = arrayOf("IDE", "Distance Learning"),
            desc   = "Handles distance learning programmes and administration.",
            photos = arrayOf("ide_1", "ide_2", "ide_3", "ide_4"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Head down the left path and walk straight down."),
                        Step("Keep walking straight down and ignore the first parking lot."),
                        Step("You are at IDE; it is to your right.")
                    )
                )
            )
        ),

        Location(
            id     = "library",
            name   = "University Library",
            abbr   = "Library",
            codes  = arrayOf("Library", "Lib"),
            desc   = "The university's main library, providing academic resources and study spaces. Has Anitta's grave.",
            photos = arrayOf("library_1", "library_2", "library_3", "library_4", "library_5", "library_6", "library_7", "library_8", "library_9", "library_10", "library_11", "library_12"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Go down the long walk."),
                        Step("Enter the hall intersection."),
                        Step("Turn right and walk straight."),
                        Step("Follow the path."),
                        Step("Continue walking straight past the large gap."),
                        Step("Walk straight."),
                        Step("Walk past the white building to your left."),
                        Step("Ignore the stairs on your right and keep walking."),
                        Step("Walk straight past those stairs and head down the hall."),
                        Step("You will see a glass door to your left, that is the library")
                    )
                )
            )
        ),

        Location(
            id     = "mfac",
            name   = "Motsepe Foundation Academic Complex (MFAC)",
            abbr   = "MFAC",
            codes  = arrayOf("MFAC", "Motsepe"),
            desc   = "Motsepe Academic Complex, a modern facility for lectures and academic activities.",
            photos = arrayOf("mfac_1", "mfac_2", "mfac_3", "mfac_4", "mfac_5", "mfac_6"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Go down the long walk in the middle path."),
                        Step("Enter the intersection and walk past it."),
                        Step("Walk past the white building towards some trees and a two-storey building."),
                        Step("Motsepe Academic Complex is the two-storey building to your right."),
                        Step("Welcome to MFAC.")
                    )
                )
            )
        ),

        Location(
            id     = "mph",
            name   = "Multi-Purpose Hall (MPH)",
            abbr   = "MPH",
            codes  = arrayOf("MPH", "Hall", "Basketball", "Volleyball"),
            desc   = "The second largest indoor venue on campus, sometimes used for major events and but typically basketball and volleyball.",
            photos = arrayOf("mph_1", "mph_2", "mph_3", "mph_4", "mph_5", "mph_6", "mph_7"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the main gate."),
                        Step("Head down the path in the middle."),
                        Step("You will see a large building with trees next to it; approach and hug those trees."),
                        Step("Walk along the trees."),
                        Step("Follow the path next to the trees."),
                        Step("MPH is in front of you.")
                    )
                )
            )
        ),

        Location(
            id     = "new_office",
            name   = "New Office",
            abbr   = "New Office",
            codes  = arrayOf("New Office"),
            desc   = "Recently constructed office space for university staff. Faculty of Science Tutor Resides there",
            photos = arrayOf("new_office_1", "new_office_2", "new_office_3", "new_office_4", "new_office_5", "new_office_6", "new_office_7", "new_office_8", "new_office_9", "new_office_10"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Go down the long walk."),
                        Step("Enter the hall intersection."),
                        Step("Turn right and walk straight."),
                        Step("Follow the path."),
                        Step("Continue walking straight past the large gap."),
                        Step("Walk straight."),
                        Step("The Finance office is the white building to the left."),
                        Step("Face the Finance door and turn around 180 degrees."),
                        Step("Walk down the path between the two buildings."),
                        Step("It is that building in front of you.")
                    )
                )
            )
        ),

        Location(
            id     = "n_class",
            name   = "N-Block Classrooms",
            abbr   = "N",
            codes  = arrayOf("N1", "N2", "N3", "N Block"),
            desc   = "Lecture rooms primarily used for undergraduate courses.",
            photos = arrayOf("n_class_1", "n_class_2", "n_class_3", "n_class_4", "n_class_5", "n_class_6", "n_class_7"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Go down the long walk in the middle path."),
                        Step("Enter the intersection and walk past it."),
                        Step("Walk past the white building towards some trees and a two-storey building."),
                        Step("N classes are that long white building."),
                        Step("Welcome to N.")
                    )
                )
            )
        ),

        Location(
            id     = "peer_counselling",
            name   = "Peer Counseling Office",
            abbr   = "PC",
            codes  = arrayOf("Peer Counseling", "Counselling"),
            desc   = "Provides confidential peer support and counselling services for students.",
            photos = arrayOf("peer_counselling_1", "peer_counselling_2", "peer_counselling_3", "peer_counselling_4", "peer_counselling_5", "peer_counselling_6", "peer_counselling_7"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the main gate."),
                        Step("Walk down the long walk (middle path)."),
                        Step("Enter the hall and turn right."),
                        Step("Walk straight."),
                        Step("Take the bend to the left and walk straight."),
                        Step("You will come across a gap."),
                        Step("Turn left and the office is the door on the left.")
                    )
                )
            )
        ),

        Location(
            id     = "science",
            name   = "Science Block",
            abbr   = "Science",
            codes  = arrayOf("Science", "BSc"),
            desc   = "Houses laboratories and offices for the Faculty of Science.",
            photos = arrayOf("science_1", "science_2", "science_3"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the gate."),
                        Step("Down the long walk."),
                        Step("It is the white two-storey building to your right.")
                    )
                )
            )
        ),

        Location(
            id     = "slt",
            name   = "Science Lecture Theater (SLT)",
            abbr   = "SLT",
            codes  = arrayOf("SLT", "SLT 1", "SLT 2"),
            desc   = "Moderate-sized lecture theaters (seating 50+ people) for science and general presentations.",
            photos = arrayOf("slt_1", "slt_2", "slt_3", "slt_4", "slt_5", "slt_6"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the main gate."),
                        Step("Go down the middle path."),
                        Step("Enter the hallway and turn right."),
                        Step("Walk straight until you see the hall bending left."),
                        Step("Turn right (the first is SLT 2)."),
                        Step("Head up the hall past SLT 2 to see SLT 1.")
                    )
                )
            )
        ),

        Location(
            id     = "tents",
            name   = "Tent 1 & 2",
            abbr   = "Tents",
            codes  = arrayOf("Tent 1", "Tent 2"),
            desc   = "Outdoor structures used as additional lecture rooms.",
            photos = arrayOf("tents_1", "tents_2", "tents_3", "tents_4", "tents_5", "tents_6", "tents_7", "tents_8", "tents_9", "tents_10", "tents_11"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the main gate."),
                        Step("Head down the path in the middle."),
                        Step("You will see a large building with trees next to it; approach and hug those trees."),
                        Step("Walk along the trees."),
                        Step("Follow the path next to the trees."),
                        Step("The MPH is in front of you."),
                        Step("Do not go down the stairs in front of you. Turn right and descend the stairs on your right."),
                        Step("You will find an intersection; turn right and walk forward."),
                        Step("You will see some sinks (IDE is to your right)."),
                        Step("Walk past the sinks and you will see the tents. Closest to you is Tent 1 and the other is Tent 2.")
                    )
                )
            )
        ),

        Location(
            id     = "warden",
            name   = "Warden's Office",
            abbr   = "Warden",
            codes  = arrayOf("Warden", "Accommodation"),
            desc   = "Handles student accommodation, hostel allocation, and residence matters.",
            photos = arrayOf("warden_1", "warden_2", "warden_3", "warden_4", "warden_5", "warden_6", "warden_7"),
            routes = arrayOf(
                Approach(
                    from  = "Main Gate",
                    steps = arrayOf(
                        Step("Enter the main gate."),
                        Step("Down the long walk."),
                        Step("Enter the intersection and walk past it."),
                        Step("Walk past the white buildings."),
                        Step("Walk towards the trees and the two-storey building."),
                        Step("Walk past them."),
                        Step("Ignore the building on the right."),
                        Step("The Warden's office is that small building in the middle.")
                    )
                )
            )
        )
    )

    fun byId(id: String) = all.find { it.id == id }

    fun search(raw: String): Array<Location> {
        val q = raw.trim().lowercase()
        if (q.isEmpty()) return all

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
