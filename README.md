# VolumeLift

A modern Android workout tracking app focused on **per-muscle weekly volume tracking** — the metric that matters most for hypertrophy.

## Features

### Active Workout Logging
- Start an empty workout or from a saved template
- Add exercises from a searchable, filterable library
- Log sets with weight, reps, and set type (Working, Warmup, Dropset, Failure)
- Running workout timer and per-set rest timer (auto-starts on set completion)
- Reorder exercises via drag-and-drop
- Save completed workouts and optionally save as templates

### Weekly Muscle Volume Dashboard
The core differentiator. Track training volume per muscle group across the week:
- Volume calculated as `weight × reps` summed per muscle group (primary = 100%, secondary = 50%)
- Progress bars showing current volume vs your weekly targets
- Color coding: amber (under target), green (on target), red-orange (over target)
- Week-over-week comparison with percentage change indicators
- Muscle heatmap — front/back body view color-coded by volume status
- Browse past weeks; resets every Monday

### Exercise Library
- Pre-populated with ~40–50 common exercises mapped to primary and secondary muscle groups
- Create custom exercises with custom muscle group assignments
- Search and filter by muscle group
- Tap any exercise to view its history and personal records

### Workout History
- All past workouts sorted by date
- Each entry shows: date, duration, total volume, exercises, and muscle groups hit
- View full workout detail with every set logged
- Copy a past workout as a new session or delete/edit entries

### Templates & Routines
- Create, edit, and delete workout templates
- Pre-built templates: Push/Pull/Legs, Upper/Lower, Full Body
- Quick-start a workout directly from a template

### Progress & Stats
- Personal records: estimated 1RM, best set (weight × reps) per exercise
- Total volume trends: weekly and monthly charts
- Workout frequency tracking
- Body weight logging with a line chart

### Settings
- Unit preference: kg / lbs
- Default rest timer duration
- Weekly volume targets per muscle group
- Theme: Light / Dark (system default)
- Export / import workout data as JSON

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Async | Coroutines + Flow |
| Navigation | Navigation Compose |
| Preferences | DataStore |
| Serialization | Gson |

## Architecture

```
com.solostackdev.volumelift/
├── di/                     # Hilt modules
├── data/
│   ├── local/
│   │   ├── db/             # AppDatabase, TypeConverters
│   │   ├── entity/         # Room entities
│   │   ├── dao/            # DAOs
│   │   └── mapper/         # Entity ↔ Domain mappers
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Domain models
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Use cases
├── presentation/
│   ├── navigation/         # NavGraph, Screen routes
│   ├── theme/              # Color, Typography, Theme
│   ├── components/         # Reusable Composables
│   ├── home/               # Home / active workout
│   ├── history/            # Workout history
│   ├── exercises/          # Exercise library
│   ├── volume/             # Weekly volume dashboard
│   ├── templates/          # Template management
│   ├── progress/           # Stats & charts
│   └── settings/           # Settings screen
└── util/                   # Extensions, date helpers, constants
```

## Requirements

- **Android**: 8.0+ (API 26)
- **Target SDK**: 34
- Fully offline — no internet permission required

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/shubham423/VolumeLift.git
   cd VolumeLift
   ```

2. Open in Android Studio (Meerkat or newer recommended).

3. Build and run on a device or emulator running Android 8.0+:
   ```bash
   ./gradlew assembleDebug
   ```

## License

This project is open source. See [LICENSE](LICENSE) for details.
