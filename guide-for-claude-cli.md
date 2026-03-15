# How to Get Claude CLI to Build a Good-Looking UI

## The Core Problem

Claude CLI cannot reliably extract design values from screenshots. When you attach
a screenshot, it sees "a dark card with green text" but doesn't know it's exactly
#242428 background with #6FD4AF text at 14sp with 12dp padding. That's why the
output always looks "close but off" — wrong spacing, wrong shades, wrong font sizes.

## The Solution: Don't Use Screenshots. Use a Design Spec File.

### Step 1: Add design-tokens.md to your project root

Copy the design-tokens.md file into your project. This file has every exact color,
spacing value, font size, corner radius, and component spec the app needs.

### Step 2: Reference it in EVERY prompt to Claude CLI

When asking Claude CLI to build any screen, always say:

```
Read design-tokens.md first. Use ONLY the exact values from that file for all
colors, spacing, typography, and component styles. Do not improvise any visual values.

Now build the [Home / Active Workout / Volume Dashboard / etc.] screen.
```

### Step 3: Build ONE screen at a time

Don't ask for the whole app at once. Go screen by screen:

```
Prompt 1: "Read design-tokens.md. Build the app's Theme.kt, Color.kt, and Type.kt
           files using the exact values from the design tokens."

Prompt 2: "Read design-tokens.md. Build reusable composable components:
           - WorkoutCard
           - MuscleTag (with category color variants)
           - VolumeProgressBar (with on-target/under/over states)
           - SetInputRow
           - RestTimerBanner
           Use the exact specs from the component specifications section."

Prompt 3: "Read design-tokens.md. Now build the Home screen composable using
           the WorkoutCard and other components you just created. Follow the
           screen layout reference exactly."
```

### Step 4: Build the Theme First

This is the #1 thing people skip. Before any screens, have Claude generate:

```kotlin
// Color.kt — paste the EXACT hex values from the tokens
// Type.kt — paste the EXACT sizes and weights
// Theme.kt — wire them into MaterialTheme
// Shape.kt — define the corner radius values
// Dimens.kt — define all spacing constants
```

Once these exist, every screen automatically picks up the right values through
MaterialTheme instead of hardcoding random values.

### Step 5: Build Reusable Components Second

The reason AI-generated UIs look bad is every screen reinvents the card, the tag,
the button — with slightly different values each time. Force consistency:

```
Build these shared components in presentation/components/:
1. WorkoutCard.kt — follows "Workout History Card" spec exactly
2. MuscleTag.kt — takes MuscleGroup enum, returns colored tag
3. VolumeProgressBar.kt — takes current/target/status, renders gradient bar
4. SetLogRow.kt — one row of set logging inputs
5. StatCard.kt — the small stat display (number + label)
6. SectionHeader.kt — overline-style section label
```

### Step 6: Validate Each Screen Before Moving On

After each screen, ask:

```
Compare what you generated against design-tokens.md:
- Are all colors exact hex matches?
- Are font sizes using the defined sp values?
- Are padding/margin values using the defined dp values?
- Are corner radii matching the spec?
Fix any deviations.
```

## Alternative Approaches (If Tokens Alone Aren't Enough)

### Approach A: Generate Compose code here, paste into project

Ask me (Claude on claude.ai) to generate the actual Kotlin Compose code for each
screen. I created the design, so I know exactly what values to use. Then paste that
code into your project and have Claude CLI integrate it with your data layer.

### Approach B: Use a .claude/commands/ custom command

Create a file at `.claude/commands/design.md` in your project:

```markdown
When building ANY UI composable, you MUST:
1. Read /design-tokens.md first
2. Use ONLY colors from the defined palette — never Color.Gray, Color.White, etc.
3. Use ONLY the defined spacing values — never arbitrary padding
4. Use ONLY the defined typography styles
5. Every card uses Surface (#242428) background with 12.dp corner radius
6. Every screen uses Background (#1A1A1E) as its background
7. Primary accent is teal (#6FD4AF for text, #2A6B5A for buttons)
8. Status colors: green=#6FD4AF, amber=#E8A94F, red=#E85D5D
```

Then in Claude CLI, type: /design before each UI task.

### Approach C: Build a reference screen first, then say "match this"

Have Claude CLI build the Home screen perfectly (using the tokens), then for
subsequent screens say:

```
Look at HomeScreen.kt. Match the exact same visual style — same card composable,
same colors, same spacing patterns. Now build HistoryScreen.kt.
```

This works because Claude CLI can read and match actual code much better than
screenshots.

## Common Pitfalls to Avoid

1. **Don't say "make it look like the screenshot"** — say "use the values from design-tokens.md"
2. **Don't ask for all screens at once** — build theme → components → screens one by one
3. **Don't let Claude use Material default colors** — override everything in Theme.kt
4. **Don't skip the shared components step** — this is what makes the app look consistent
5. **Don't use Color.Black for backgrounds** — always use the defined Background color
6. **Don't let Claude add shadows/elevation** — this design uses flat surfaces with subtle borders
