# Claude CLI Prompt — Workout Tracker Android App

Use this prompt in Claude CLI. You can paste it as-is, or save it as a file and reference it.

---

## The Prompt

```
Build a complete Android workout tracking app using Kotlin and Jetpack Compose. The app is fully offline — no server, no network calls. All data is persisted locally using Room DB. The standout feature is per-muscle weekly volume tracking, which no mainstream app does well.

## Architecture & Code Quality

Follow modern Android architecture guidelines strictly:
- **MVVM with Clean Architecture layers**: data (Room entities, DAOs, repository implementations), domain (use cases, repository interfaces, domain models), presentation (ViewModels, Compose screens)
- **Single Activity architecture** with Jetpack Navigation Compose for all screen transitions
- **Dependency injection** using Hilt throughout
- **Kotlin Coroutines + Flow** for all async operations and reactive data from Room
- **UiState sealed classes** in every ViewModel — Loading, Success, Error states
- **Repository pattern**: ViewModels never touch DAOs directly
- Proper separation of concerns — no business logic in Composables, no UI logic in ViewModels
- Use `collectAsStateWithLifecycle()` for Flow collection in Compose

## Data Layer (Room Database)

Design the Room database with these core entities:

**Exercise** — id, name, primaryMuscleGroup (enum), secondaryMuscleGroups (list), isCustom (boolean), notes
**MuscleGroup enum** — Chest, Back, Shoulders, Biceps, Triceps, Quads, Hamstrings, Glutes, Calves, Abs, Forearms, Traps, Lats (cover all major groups)
**WorkoutTemplate** — id, name, exerciseIds (ordered list), notes
**WorkoutSession** — id, templateId (nullable), startTime, endTime, notes, isCompleted
**ExerciseLog** — id, sessionId, exerciseId, order
**SetLog** — id, exerciseLogId, setNumber, weight (Double), reps (Int), setType (enum: Working, Warmup, Dropset, Failure), isCompleted, restTimerSeconds
**BodyWeight** — id, weight, date

Key relationships:
- WorkoutSession has many ExerciseLogs, each ExerciseLog has many SetLogs
- Use TypeConverters for lists and enums
- Add proper indices on sessionId, exerciseId, date columns for query performance
- Pre-populate database with ~40-50 common exercises mapped to correct primary/secondary muscle groups

Write efficient DAO queries especially for:
- Weekly volume calculation: SUM of (weight × reps) per muscle group for a given week, accounting for both primary (100% volume) and secondary (50% volume) muscle contributions
- Historical progress: best sets, total volume over time per exercise
- Workout history with all nested data (session → exercise logs → sets)

## Core Features

### 1. Quick Start Workout
- Start an empty workout or from a saved template
- Add exercises from the exercise library (searchable, filterable by muscle group)
- For each exercise: add sets, log weight/reps, mark set type, mark complete
- Running workout timer at the top
- Per-set rest timer with configurable defaults (auto-starts when a set is completed)
- Reorder exercises via drag-and-drop
- Save workout when done; option to save as template

### 2. Exercise Library
- Pre-populated exercises organized by muscle group
- Each exercise shows primary and secondary muscles it targets
- Users can create custom exercises and assign muscle groups
- Search and filter functionality
- Tap an exercise to see its history (past performances, PR tracking)

### 3. Workout History
- List of past workouts sorted by date (most recent first)
- Each entry shows: date, duration, total volume, exercise count, muscle groups hit
- Tap to view full workout detail with all sets
- Ability to copy a past workout as a new session
- Delete/edit past workouts

### 4. Templates / Routines
- Create, edit, delete workout templates
- A template is an ordered list of exercises with optional target sets/reps
- Quick-start a workout from any template
- Suggested templates: Push/Pull/Legs, Upper/Lower, Full Body (pre-built)

### 5. Weekly Muscle Volume Dashboard (THE KEY FEATURE)
This is the differentiator. Build this thoughtfully:

- **Volume calculation**: For each muscle group, sum (weight × reps) across all completed sets in the current week (Monday–Sunday). Primary muscle gets 100% credit, secondary muscles get 50%.
- **Dashboard screen** showing all muscle groups with:
  - Current week's volume (in kg or lbs based on user preference)
  - A horizontal progress bar showing current volume vs user-defined weekly target
  - Color coding: under target (amber/yellow), at target (green), over target (red/orange)
  - Comparison to last week's volume (↑12% or ↓5% type indicators)
- **Weekly trends**: A simple bar chart (last 4-8 weeks) per muscle group so users can see if they're progressively overloading
- **Muscle heatmap view**: A body outline (front + back) where muscles are color-coded by volume status — at a glance see which muscles are under/over-trained this week
- Users can set custom volume targets per muscle group
- Week resets every Monday; user can browse past weeks

### 6. Progress & Stats
- Personal records (1RM estimated, best set weight × reps) per exercise
- Total volume lifted over time (weekly/monthly chart)
- Workout frequency (workouts per week trend)
- Body weight tracking with simple line chart

### 7. Settings
- Unit preference: kg / lbs (affects all displays and inputs)
- Default rest timer duration
- Weekly volume targets per muscle group
- Theme: Light / Dark (follow system by default)
- Export workout data as JSON backup
- Import from JSON backup

## UI / Design Guidelines

The design should be **clean, minimal, and functional** — think of a well-designed fitness tool, not a flashy social app.

- **Color palette**: Use a neutral dark theme as default (dark grey backgrounds, not pure black). One accent color (a muted teal or blue-green) for CTAs and highlights. Use semantic colors for volume status: green (on target), amber (under), red-orange (over).
- **Typography**: Use the default Material 3 type scale. Bold headings, regular body. Good hierarchy. No decorative fonts.
- **Material 3 with Material You**: Use Material 3 components throughout (TopAppBar, NavigationBar, Cards, Chips, FloatingActionButton). Support dynamic color theming on Android 12+.
- **Bottom navigation**: 4 tabs — Home (today/active workout), History, Volume (the weekly dashboard), Profile/Settings
- **Spacing and touch targets**: Generous padding. All tappable elements minimum 48dp. Workout logging UI must be thumb-friendly since users interact mid-workout.
- **Animations**: Keep them subtle — shared element transitions between screens, smooth list animations. Nothing flashy.
- **Empty states**: Thoughtful empty states with illustration placeholders and clear CTAs (e.g., "No workouts yet — start your first one!")
- **Number input**: Use a custom number pad or stepper for weight/reps entry — not a full keyboard. This is critical for UX during a workout.

## Project Structure

```
com.workouttracker.app/
├── di/                          # Hilt modules
├── data/
│   ├── local/
│   │   ├── db/                  # AppDatabase, TypeConverters
│   │   ├── entity/              # Room entities
│   │   ├── dao/                 # DAOs
│   │   └── mapper/              # Entity ↔ Domain mappers
│   └── repository/              # Repository implementations
├── domain/
│   ├── model/                   # Domain models
│   ├── repository/              # Repository interfaces
│   └── usecase/                 # Use cases
├── presentation/
│   ├── navigation/              # NavGraph, Screen routes
│   ├── theme/                   # Color, Typography, Theme
│   ├── components/              # Reusable Composables
│   ├── home/                    # Home screen
│   ├── workout/                 # Active workout screen
│   ├── history/                 # History list + detail
│   ├── exercises/               # Exercise library
│   ├── volume/                  # Weekly volume dashboard
│   ├── templates/               # Template management
│   ├── progress/                # Stats & progress
│   └── settings/                # Settings screen
└── util/                        # Extensions, date helpers, constants
```

## Technical Requirements

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34
- **Compose BOM**: latest stable
- **Room version**: latest stable
- **Kotlin**: 2.0+
- **Gradle**: Kotlin DSL (build.gradle.kts)
- Proper ProGuard/R8 rules for Room
- No network permissions in AndroidManifest

## What to Generate

Generate the complete, working source code for this app. Every file should be production-quality — not stubs, not TODOs, not placeholders. Prioritize in this order if you need to manage scope:

1. Data layer complete (all entities, DAOs, database, repositories, pre-populated exercises)
2. Active workout flow (start workout, add exercises, log sets, complete workout)
3. Weekly muscle volume dashboard with progress bars and week-over-week comparison
4. Exercise library with search/filter
5. Workout history
6. Templates
7. Progress/stats charts
8. Settings and data export
9. Muscle heatmap visualization
10. Body weight tracking

Make sure the app compiles and runs. Use proper error handling, not just happy-path code.
```

---

## Tips for Using This in Claude CLI

1. **Save this prompt** to a file like `prompt.md` and run:
   ```bash
   cat prompt.md | claude
   ```
   Or paste it directly into an interactive Claude CLI session.

2. **Expect iterative work.** This is a large app. Claude CLI will likely generate it in chunks. You can follow up with:
   - "Now generate the data layer — all entities, DAOs, the database class, and type converters"
   - "Now generate the active workout screen and its ViewModel"
   - "Now build the weekly volume dashboard"

3. **If Claude truncates**, just say: "Continue from where you stopped" or "Generate the remaining files".

4. **For the muscle heatmap**, Claude may use a Canvas-based Composable drawing a simplified body outline. If you want SVG-based, mention that explicitly.

5. **After generation**, ask Claude to also generate:
   - `build.gradle.kts` (app-level and project-level) with all dependencies
   - `AndroidManifest.xml`
   - The Room database migration strategy
