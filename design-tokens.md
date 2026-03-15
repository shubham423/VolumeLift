# Workout Tracker — Design Tokens & Component Specs

Use these EXACT values when building every Composable. Do NOT deviate from these colors, spacing, or typography values. This is the single source of truth for the app's visual design.

---

## Color Palette

```kotlin
// Background hierarchy (dark theme)
val Background = Color(0xFF1A1A1E)        // Main screen background
val Surface = Color(0xFF242428)            // Cards, inputs, elevated surfaces
val SurfaceVariant = Color(0xFF2A2A2E)     // Empty input fields, subtle borders
val Border = Color(0xFF3A3A3E)             // Borders on unfilled inputs

// Primary accent (teal)
val Primary = Color(0xFF2A6B5A)            // Buttons, CTAs, active nav, FAB background
val PrimaryLight = Color(0xFF6FD4AF)       // Accent text, highlights, active labels, progress bars
val PrimaryDark = Color(0xFF1A4A3E)        // Gradient end for CTA cards
val PrimaryContainer = Color(0xFF1A2E26)   // Tinted badge/tag background for "on target"

// Text colors
val TextPrimary = Color(0xFFF0F0F0)        // Headings, primary content
val TextSecondary = Color(0xFF8E8E93)      // Subtitles, labels, timestamps
val TextTertiary = Color(0xFF6B6B70)       // Hints, placeholders, disabled text

// Semantic: Volume status
val OnTarget = Color(0xFF6FD4AF)           // Green — volume on target
val OnTargetBg = Color(0xFF1A2E26)         // Green tinted background
val UnderTarget = Color(0xFFE8A94F)        // Amber — volume under target
val UnderTargetBg = Color(0xFF2E2A1A)      // Amber tinted background
val OverTarget = Color(0xFFE85D5D)         // Red — volume over target
val OverTargetBg = Color(0xFF2E1A1A)       // Red tinted background

// Muscle group category colors (for tags)
val MuscleChestColor = Color(0xFFE8A94F)
val MuscleChestBg = Color(0xFF3A2A1A)
val MuscleBackColor = Color(0xFF6FD4AF)
val MuscleBackBg = Color(0xFF1A2A2A)
val MuscleLegsColor = Color(0xFFC490D4)
val MuscleLegsBg = Color(0xFF2A1A2A)
val MuscleSecondaryColor = Color(0xFF9090D4)  // Secondary muscle tags
val MuscleSecondaryBg = Color(0xFF1A1A2E)
```

## Typography

```kotlin
// All text uses default system sans-serif (Material 3 default)
// NO decorative fonts. Only these sizes and weights:

// Screen titles
val TitleLarge = 24.sp, FontWeight.W500, TextPrimary
// Example: "Good morning", "Weekly volume", "Exercises"

// Section headers
val TitleMedium = 20.sp, FontWeight.W500, TextPrimary

// Card titles, exercise names
val BodyLarge = 14.sp, FontWeight.W500, TextPrimary
// Example: "Bench press", "Push day"

// Subtitles, descriptions
val BodyMedium = 12.sp, FontWeight.W400, TextSecondary
// Example: "Yesterday · 58 min · 12,450 kg"

// Small labels, tags, hints
val LabelSmall = 11.sp, FontWeight.W400, TextSecondary

// Tiny labels (tag text, column headers)
val LabelTiny = 10.sp, FontWeight.W400, TextTertiary

// Overline (date headers, section labels)
val Overline = 11.sp, FontWeight.W500, TextTertiary, letterSpacing = 0.5.sp
// ALWAYS uppercase. Example: "SATURDAY, MAR 14", "THIS WEEK"

// Large numbers (stats)
val DisplayNumber = 22.sp, FontWeight.W500, TextPrimary
// With unit suffix at 13.sp, FontWeight.W400, TextSecondary
```

## Spacing & Layout

```kotlin
// Screen padding
val ScreenHorizontalPadding = 16.dp
val ScreenTopPadding = 12.dp

// Card specs
val CardBackground = Surface                    // 0xFF242428
val CardCornerRadius = 12.dp
val CardPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
val CardSpacing = 6.dp                          // Between stacked cards

// Large CTA card (like "Start Workout")
val CTACornerRadius = 16.dp
val CTAGradient = Brush.linearGradient(listOf(Primary, PrimaryDark))  // 135 degrees
val CTAPadding = 16.dp

// Chips / filter pills
val ChipCornerRadius = 8.dp
val ChipPaddingH = 12.dp
val ChipPaddingV = 5.dp
val ChipFontSize = 11.sp
val ChipActiveBackground = Primary              // 0xFF2A6B5A
val ChipInactiveBackground = Surface            // 0xFF242428

// Tag / badge (muscle groups)
val TagCornerRadius = 4.dp                      // Small tags: 4.dp
val TagPaddingH = 6.dp
val TagPaddingV = 2.dp
val TagFontSize = 9.sp
// Larger status pills
val PillCornerRadius = 10.dp
val PillPaddingH = 10.dp
val PillPaddingV = 6.dp
val PillFontSize = 11.sp

// Touch targets
val MinTouchTarget = 48.dp
val ButtonCornerRadius = 10.dp
val FABSize = 48.dp
val FABCornerRadius = 14.dp

// Input fields (set logging)
val SetInputHeight = 34.dp
val SetInputCornerRadius = 8.dp
val SetInputBackground = SurfaceVariant         // 0xFF2A2A2E (empty)
val SetInputFilledBackground = Surface          // 0xFF242428 (with value)
val SetInputFontSize = 13.sp
val SetInputFontWeight = FontWeight.W500

// Checkmark button (complete set)
val CheckButtonSize = 28.dp
val CheckButtonRadius = 8.dp
val CheckButtonActiveBackground = Primary       // 0xFF2A6B5A
val CheckButtonInactiveBorder = Border          // 0xFF3A3A3E

// Dividers
val DividerColor = SurfaceVariant               // 0xFF2A2A2E
val DividerThickness = 0.5.dp
```

## Bottom Navigation Bar

```kotlin
val NavBarBackground = Background               // 0xFF1A1A1E
val NavBarBorderColor = SurfaceVariant          // 0xFF2A2A2E top border, 0.5.dp
val NavBarPaddingBottom = 24.dp                 // For gesture navigation safe area
val NavBarPaddingTop = 8.dp
val NavBarIconSize = 22.dp
val NavBarLabelSize = 9.sp
val NavBarActiveColor = PrimaryLight            // 0xFF6FD4AF
val NavBarInactiveColor = TextTertiary          // 0xFF6B6B70

// 4 tabs: Home, History, Volume, Profile
// Icons: Home (house), History (clock), Volume (bar chart), Profile (person)
```

## Component Specifications

### Search Bar
```
Background: Surface (0xFF242428)
Corner radius: 12.dp
Padding: 10.dp vertical, 14.dp horizontal
Icon: 16x16 magnifying glass, color TextTertiary
Placeholder text: 13.sp, TextTertiary
```

### Volume Progress Bar (key feature)
```
Track height: 8.dp
Track background: Surface (0xFF242428)
Track corner radius: 4.dp
Fill: horizontal gradient
  - On target: 0xFF2A6B5A → 0xFF6FD4AF
  - Under target: 0xFF8A6520 → 0xFFE8A94F
  - Over target: 0xFFA33030 → 0xFFE85D5D
Fill corner radius: 4.dp

Layout per muscle group (vertical stack, 12.dp between groups):
  Row 1: Muscle name (14sp/W500/TextPrimary) ... Volume value (12sp/W500/StatusColor) + Arrow indicator (10sp/StatusColor)
  Row 2: Progress bar
  Row 3: Target label (9sp/TextTertiary) ... Percentage (9sp/TextTertiary)
```

### Rest Timer Banner
```
Background: 0xFF2A2520 (warm dark brown)
Corner radius: 12.dp
Padding: 10.dp vertical, 14.dp horizontal
Label: "REST TIMER" — 10.sp, UnderTarget color, uppercase, 0.5sp letter spacing
Time display: 20.sp, W500, TextPrimary
Control buttons: 28x28.dp, corner radius 8.dp, background 0xFF3A3530
  Text: 12.sp, UnderTarget color
  Dismiss button: background UnderTarget at 20% opacity
```

### Workout Set Row
```
Height: ~34.dp per row
Layout (horizontal, 4.dp gap):
  [Set number: 32.dp wide, centered, 12.sp, TextSecondary]
  [Previous: flex, centered, 11.sp, TextTertiary, format "80 × 10"]
  [Weight input: 56.dp wide, SetInput specs]
  [Reps input: 44.dp wide, SetInput specs]
  [Check button: 28.dp, CheckButton specs]

Column headers above rows:
  Same widths, 10.sp, TextTertiary
  Labels: "Set", "Previous", "kg", "Reps", (blank for check)
```

### Workout History Card
```
Same as base card specs
Title: exercise name, BodyLarge
Subtitle: "58 min · 12,450 kg · 5 exercises" — BodyMedium
Muscle tags: row of Tags with category-specific colors
Right side: chevron icon (16x16, TextTertiary)
Spacing: 2.dp between title and subtitle, 6.dp between subtitle and tags
```

### Week Summary Card (History screen)
```
Background: Surface
Corner radius: 14.dp (slightly larger)
Padding: 14.dp
Overline: "THIS WEEK"
Stats row: 3 columns, centered
  Number: DisplayNumber size
  Label: 10.sp, TextTertiary
Day dots row:
  Active dot: 8x8.dp circle, PrimaryLight fill
  Inactive dot: 8x8.dp circle, SurfaceVariant fill
  Today dot: SurfaceVariant fill with Border stroke
  Label: 8.sp, TextTertiary (today label in PrimaryLight)
```

### Muscle Heatmap Body Figure
```
Use Canvas drawing in Compose.
Body parts are simple ellipses and rounded paths.
Fill color based on volume status:
  - On target: OnTarget at 0.6 opacity
  - Under target: UnderTarget at 0.5 opacity
  - Over target: OverTarget at 0.7 opacity
  - No data: SurfaceVariant fill with Border stroke, 0.5.dp
Background: none (transparent, inherits screen background)
Size: roughly 180x320.dp centered
```

### Exercise Library Item
```
Same base card
Left side:
  Title: exercise name, BodyLarge
  Tags row (4.dp gap, 4.dp top margin):
    Primary muscle: PrimaryContainer bg + PrimaryLight text
    Secondary muscles: MuscleSecondaryBg + MuscleSecondaryColor
Right side (right-aligned):
  PR line: "PR: 100kg" — 11.sp, PrimaryLight
  Last used: "Last: 2 days ago" — 9.sp, TextTertiary
```

---

## Screen-by-Screen Layout Reference

### Home Screen
1. Top: Overline date + Title "Good morning"
2. Stats row: 2 cards side by side (flex, 8.dp gap) with DisplayNumber + unit
3. CTA gradient card: "Start workout" with 2 pill buttons ("+ Empty", "From template")
4. Section: "Recent workouts" label + 3 history cards stacked

### Active Workout Screen
1. Header: workout name (18.sp/W500) + live timer with pulsing green dot
2. "Finish" button: Primary bg, 12.sp/W500, 10.dp corner radius
3. Rest timer banner (when active)
4. Exercise sections: name + muscle label, set table with headers, "+ Add set" link
5. Divider between exercises
6. "+ Add exercise" card at bottom

### Volume Dashboard
1. Title + week navigator (date range + arrow buttons)
2. Status summary pills row (on target count, under count, over count)
3. Muscle group volume bars (scrollable list)
4. Mini bar chart: 8 weeks, current week highlighted in Primary

### Exercise Library
1. Title
2. Search bar
3. Filter chips (horizontal scroll)
4. Exercise list cards
5. FAB bottom-right

### Workout History
1. Title
2. Week summary card with day dots
3. Date-grouped workout cards

### Muscle Heatmap
1. Title + subtitle
2. Front/Back toggle (segmented control)
3. SVG/Canvas body figure
4. Legend row
5. Quick stats card with muscle mini-cards
