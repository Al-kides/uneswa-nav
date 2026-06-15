# UNESWA Location Guidance App

Android navigation app for the UNESWA Kwaluseni campus. Works fully offline.

## Project Structure

```
app/src/main/
├── assets/
│   └── locations.json          ← ALL location data lives here
├── res/
│   └── drawable/               ← Add your campus photos here
└── java/com/uneswa/nav/
    ├── MainActivity.kt         ← Entry point + navigation graph
    ├── Theme.kt                ← UNESWA colours (navy + gold)
    ├── data/
    │   ├── Models.kt           ← Location, Approach data classes
    │   └── LocationRepository  ← Loads + searches locations
    └── ui/
        ├── ViewModels.kt       ← HomeViewModel, DirectionsViewModel
        ├── HomeScreen.kt       ← Search + location list
        └── DirectionsScreen.kt ← Step-by-step directions + photos
```

## How to Add a New Location

Edit `assets/locations.json`. Copy an existing entry and change the fields.
No code changes needed — the app reads everything from the JSON at startup.

```json
{
  "id": "library",
  "fullName": "University Library",
  "abbreviation": "Lib",
  "description": "Main library with study rooms and computer lab.",
  "photos": ["library_exterior"],
  "approaches": [
    {
      "from": "Main Gate",
      "steps": [
        "Walk straight from the gate...",
        "Turn left at the flagpole..."
      ]
    }
  ]
}
```

## How to Add Photos

1. Take a photo on campus
2. Resize it to roughly 800×600px (keeps APK small)
3. Save as a `.jpg` or `.png` in `app/src/main/res/drawable/`
4. Name it exactly what you put in the JSON `photos` array
   - e.g. JSON says `"mph_exterior"` → file is `drawable/mph_exterior.jpg`

The app shows a "Photo coming soon" placeholder for any missing photos,
so you can add them incrementally.

## Building

Open in Android Studio (Ladybug or newer), sync Gradle, run on device or emulator.

Minimum Android version: 8.0 (API 26) — covers virtually all phones on campus that are android.

## Two-Week Plan
Ended up scraping this and tossing this into facebook, and getting google forms lol.
