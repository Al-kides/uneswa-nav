# UNESWA Location Guidance App
**A Digital Navigation Solution for the Kwaluseni Campus**

This Android application is a student-led project for **CSC300** by Lungelo Mhlanga and Vezokuhle Simelane, made to assist students and visitors navigate the campus using vivid landmarks combined with instructions.

---

## Key Features

- **Offline-First**: Zero dependence on GPS or Internet. All directions and images are bundled locally.
- **Landmark-Based Navigation**: Uses high-quality photographs and plain-language steps (e.g., "past the bamboo plants") to guide users.
- **Abbreviation Solver**: Instantly maps cryptic timetable codes (MPH, SE, SLT, Com) to their full names and functional descriptions.
- **Visual Recognition**: Every location features a "Wallpaper Thumbnail" on the search screen for instant identification.
- **High Performance**: Optimized using Jetpack Compose and Coil (with HEIC support) for near-instant image loading.
- **WIP**: Reducing apk size. Earlier wee used plain jpegs and PNGs and realized that their size would problematic so we changed to HEIC mid sessoin.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Image Loading**: Coil (Optimized for HEIC)
- **Minimum Android**: 9.0 (API 28) — Required for native HEIC decoding.
- **Theme**: Official UNESWA Red (`#B71C1C`) and Gold/Yellow (`#F9A825`).

---

## Project Structure

```
app/src/main/
├── assets/
│   ├── locations.json          ← ALL location, route, and abbreviation data
│   └── drawable/               ← Optimized campus photos (.heic)
├── res/
│   ├── drawable/               ← App logo and splash screen assets
│   └── values/                 ← UNESWA branding(if accepted by institution) and themes
└── java/com/uneswa/nav/
    ├── MainActivity.kt         ← Entry point + Splash Screen init
    ├── Theme.kt                ← Brand colors and typography
    ├── data/
    │   ├── Models.kt           ← Data classes (Location, Approach, Step)
    │   └── LocationRepo.kt     ← Search algorithm and data provider
    └── ui/
        ├── ViewModels.kt       ← State management
        ├── HomeScreen.kt       ← Search, list, and wallpaper cards
        └── DirectionsScreen.kt ← Interactive step-by-step guidance
```

---

# How to Add/Update Locations

The app is content-driven. To add a new location or change directions:

1.  **Edit the JSON**: Modify `app/src/main/assets/locations.json`.
2.  **Update the Repo**: Synchronize changes in `app/src/main/java/com/uneswa/nav/data/LocationRepo.kt`.
3.  **Add Photos**: Save new photos as `.heic` in `app/src/main/assets/drawable/` and name them following the pattern `locationid_X.heic`.

---

## Building

1.  Open the project in **Android Studio (Ladybug or newer)**.
2.  Sync Gradle dependencies.
3.  Run on a physical device or emulator running **Android 9.0+**.

*Note: For official release builds, ensure you sign the APK with a valid keystore.*

---

## 👥 Project Team

- **Simelane Vezokuhle** (202301485)
- **Mhlanga Lungelo** (202404067)
- **Supervisor**: Mr. Ndumiso Eugene Khumalo (MSc)

*Department of Computer Science, University of Eswatini.*

LICENSE