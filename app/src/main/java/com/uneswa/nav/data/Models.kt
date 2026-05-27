package com.uneswa.nav.data

data class Step(
    val text: String,
    val image: String? = null // name of image in assets/drawable, no extension
)

data class Approach(
    val from: String,
    val steps: Array<Step>
)

data class Location(
    val id: String,
    val name: String,
    val abbr: String,
    val codes: Array<String>,   // all timetable codes that map to this place
    val desc: String,
    val photos: Array<String>,  // drawable resource names, no extension
    val routes: Array<Approach>
) {
    // single string we run search against — built once, reused
    val corpus: String get() = "$name $abbr ${codes.joinToString(" ")} $desc".lowercase()
}
