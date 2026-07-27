package com.uneswa.nav.data

data class Step(
    val text: String,
    val image: String? = null
)

data class Approach(
    val from: String,
    val steps: Array<Step>
)

data class Location(
    val id: String,
    val name: String,
    val abbr: String,
    val codes: Array<String>,
    val desc: String,
    val photos: Array<String>,
    val routes: Array<Approach>
) {
    val corpus: String get() = "$name $abbr ${codes.joinToString(" ")} $desc".lowercase()
}
