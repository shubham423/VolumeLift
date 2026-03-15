# Workout Tracker — Product & UX Guidelines

> This document is the product bible for the workout tracker app. Every design
> decision, every interaction, every screen must align with these principles.
> Read this BEFORE building any feature. When in doubt, refer back here.

---

## Product Vision

**One sentence:** The only workout app that tells you exactly which muscles are
undertrained and overtrained each week — so you never guess, never imbalance,
never plateau.

**The insight:** Every gym app lets you log sets. None of them answer the
question serious lifters actually care about: *"Am I hitting enough volume
per muscle group this week?"* They make you do the mental math yourself.
We do the math for them, beautifully, in real time.

**Who this is for:** Intermediate to advanced lifters (6+ months of training)
who understand progressive overload and train with intention. They've outgrown
note-taking apps and find mainstream apps bloated with social features they
don't want. They want a precision tool, not a social network.

**Who this is NOT for:** Complete beginners who need guided programs, people
looking for a social/community fitness experience, cardio-focused users.

---

## Core Product Principles

### 1. Volume is the hero metric — surface it everywhere

Weekly muscle volume is not a feature hidden behind three taps. It is THE
organizing principle of the entire app. Every screen should whisper
(or shout) volume data:

- **Home screen:** Show a mini volume summary — how many muscle groups are
  on target vs under vs over. The user should know their weekly status
  within 1 second of opening the app.
- **Active workout:** As the user logs sets, show a subtle real-time
  indicator of how this workout is contributing to their weekly volume.
  Example: after completing a bench press set, briefly flash "+800 kg
  chest volume" or show a small progress ring filling up.
- **Workout complete screen:** Show a before/after comparison of their
  weekly volume. "This workout added: Chest +3,200 kg, Shoulders +1,800 kg,
  Triceps +900 kg" with updated progress bars.
- **History screen:** Each past workout card shows which muscle groups it
  contributed to and how much volume.
- **Exercise library:** Each exercise shows estimated volume contribution
  per set so users can pick exercises strategically.

The user should NEVER have to navigate to a dedicated dashboard to understand
their volume. The dashboard exists for deep analysis, but the data leaks into
every surface.

### 2. Zero-friction logging — respect the mid-workout context

Users log sets BETWEEN sets, while sweating, breathing hard, with shaky hands.
Every interaction must respect this context:

- **One-hand operation:** All logging inputs must be reachable with the
  right thumb on a standard phone held in one hand. Place inputs in the
  lower 60% of the screen. Never put critical actions in the top-left corner.
- **Number entry is king:** Weight and reps are entered dozens of times per
  session. The input method must be faster than a keyboard:
  - Show the PREVIOUS set's values pre-filled. User just taps the checkmark
    if same weight/reps (most common case).
  - Use a compact number pad or stepper — NOT the system keyboard. The system
    keyboard covers half the screen and requires a dismiss action.
  - Allow quick increment/decrement: tap +2.5 / -2.5 for weight, +1 / -1
    for reps. These are the most common adjustments.
  - Support decimal weights (2.5 kg increments are standard).
- **Confirm with one tap:** Completing a set should be a single tap on the
  checkmark. No confirmation dialogs. No "are you sure?" The user is in flow.
- **Auto-advance:** After completing a set, auto-start the rest timer and
  scroll to the next set row. Reduce cognitive load.
- **Forgiveness over prevention:** Let users edit any logged set freely.
  Don't warn them about "unusual" weights. They know what they lifted.

### 3. Smart defaults — predict what the user will do

The app should feel like it knows the user's routine:

- **Pre-fill from last session:** When starting a workout from a template,
  pre-fill every set with the weight and reps from the last time they did
  that exercise. The user's job is just to match or beat their previous
  numbers.
- **Suggest next weight:** If the user hit all target reps last session,
  subtly suggest a small weight increase (e.g., show the weight field with
  a slightly higher value or a small "↑" indicator). This IS progressive
  overload made automatic.
- **Rest timer auto-starts:** When a set is marked complete, the rest timer
  begins immediately. No extra tap required. The timer should be dismissible
  but never require initiation.
- **Default rest times by exercise type:** Compound lifts (squat, deadlift,
  bench) default to 180s rest. Isolation exercises default to 90s. Let users
  override per exercise, and remember their preference.
- **Template from history:** If a user does the same exercises 3 times in
  similar order, offer to save it as a template. Don't make them set up
  templates manually.

### 4. Information density without clutter

Serious lifters want to see data. Don't hide it behind navigation. But don't
overwhelm either. The hierarchy:

- **Glanceable:** Volume status (on target / under / over) should be
  readable from arm's length. Use color coding aggressively — green, amber,
  red. The user looks at their phone on a rack, sees green/amber/red, knows
  where they stand.
- **Scannable:** Set history, workout history, exercise stats should be
  scannable in a vertical scroll. No horizontal pagination, no carousels,
  no tabs within tabs. Vertical scroll is the only navigation pattern
  within a screen.
- **Detailed on demand:** Tap any card to expand or navigate to detail.
  Charts, trends, PR history live one tap deeper. Never on the surface.

### 5. Offline-first is a feature, not a limitation

Most people work out in basements, garages, or gym areas with poor signal.
The app must:

- **Work fully offline.** No loading spinners. No "check your connection."
  Every screen renders instantly from local data.
- **Never lose data.** All writes go to Room immediately. There is no
  "sync" concept. The data IS local.
- **Start instantly.** Cold launch to usable home screen in under 1 second.
  No splash screens beyond the system-mandated brief flash. No onboarding
  flows that block usage.

---

## Feature Hierarchy & Prioritization

Not all features are equal. Build and polish in this order. If something from
tier 3 conflicts with something from tier 1, tier 1 wins every time.

### Tier 1 — The core loop (MUST be flawless)

These three features together form the complete user loop. If only these
three things existed, the app would still be valuable.

1. **Start workout → log sets → complete workout**
   - This is the heartbeat. It must feel instant, tactile, and satisfying.
   - The "workout complete" moment should feel like an achievement — show
     the user what they accomplished (total volume, duration, muscles hit,
     any PRs).

2. **Weekly muscle volume dashboard**
   - This is why they chose THIS app. The dashboard must load instantly
     and be immediately readable.
   - It answers: "Am I on track this week?" in under 2 seconds.

3. **Exercise library with muscle mapping**
   - The foundation of accurate volume tracking. Every exercise must map
     to correct primary and secondary muscles with appropriate volume
     attribution.

### Tier 2 — Retention features (make them come back)

4. **Pre-filled sets from previous sessions** — makes logging faster every time
5. **Templates / routines** — reduces "what should I do today" friction
6. **Workout history** — lets them see their consistency
7. **Progress tracking / PRs** — gives them a reason to push harder

### Tier 3 — Delight features (make them love it)

8. **Muscle heatmap body visualization** — the "wow" moment when showing friends
9. **Week-over-week volume trends** — for the data nerds
10. **Body weight tracking** — expected feature, not a differentiator
11. **Data export/import** — trust signal, rarely used but important
12. **Theme customization** — nice to have, low priority

---

## Screen-by-Screen UX Specifications

### Home Screen

**Purpose:** Answer "what's my status?" and "what should I do?" in 3 seconds.

**Layout priority (top to bottom):**

1. **Greeting + date** — personal touch, orients the user in time
2. **Weekly snapshot** — two stat cards side by side:
   - Workouts this week (number out of their usual frequency)
   - Total volume this week (with trend arrow vs last week)
3. **Volume alert (conditional)** — if any muscle group is significantly
   under target (below 40%) by Thursday or later, show a gentle nudge:
   "Your quads are at 35% of target — consider a leg session today."
   This is HIGH VALUE — it's personalized, actionable, and demonstrates
   the app's intelligence. Show max 1 alert to avoid nagging.
4. **Start workout CTA** — the primary action. Large, prominent, impossible
   to miss. Two options: empty workout or from template. The template option
   should show the user's most-used template name if they have one.
5. **Recent workouts** — last 3 sessions. Quick tap to view detail or
   repeat as new workout.

**What NOT to put on home:**
- No social feed, no "featured workouts," no tips, no motivational quotes.
  This is a tool. Respect the user's time and intelligence.

### Active Workout Screen

**Purpose:** Fastest possible set logging with ambient volume awareness.

**Critical UX decisions:**

- **Timer is always visible** at the top. It never scrolls away. Shows
  elapsed time in MM:SS format with a subtle pulsing dot to indicate active.
- **Rest timer takes over** the timer area when active. It's prominent
  (larger font, warm amber color) because the user glances at it repeatedly.
  Shows countdown. Quick-adjust buttons (+15s / -15s) because sometimes
  you need more rest, sometimes less.
- **Exercise sections are collapsible.** Once all sets for an exercise are
  complete, the section auto-collapses to a single summary line showing
  "Bench Press — 3 sets done, 2,400 kg volume." This keeps the screen
  focused on the CURRENT exercise.
- **"Add set" is always visible** below the last set of the current exercise.
  Users often decide mid-exercise to add an extra set.
- **"Add exercise" is always at the bottom** but scrolls with content.
  Opens the exercise picker (search + filter by muscle group).
- **Reorder via long-press drag.** No edit mode. Just grab and move.
- **Swipe to delete** a set or exercise. With undo snackbar (5 seconds).
  No confirmation dialog.

**The volume whisper (key differentiator UX):**

When a user completes a set, show a brief, subtle annotation near the set
row: "+640 kg chest" (calculated as weight × reps × muscle attribution).
This fades after 2 seconds. It's NOT a popup or dialog — it's a whisper
that builds subconscious awareness of volume contribution.

At the top of the screen (below the timer), show a thin row of mini progress
rings — one per muscle group being worked in this session. These fill in
real time as sets are completed. Tapping one shows the full volume breakdown
for that muscle this week.

**Workout completion flow:**

When the user taps "Finish":
1. Show a summary card (NOT a full screen takeover — a bottom sheet):
   - Duration
   - Total volume
   - Exercises completed
   - Per-muscle volume added
   - Any PRs hit (highlighted with a special indicator)
2. Option to save as template (if started from empty workout)
3. Option to add notes
4. "Done" dismisses and returns to home

### Weekly Volume Dashboard

**Purpose:** Deep analysis of weekly muscle balance. THE feature.

**Top section:**
- Week selector: current week shown as "Mar 10 – Mar 16" with left/right
  arrows to browse past weeks. Swipe gesture also works.
- Summary pills: "5 on target · 4 under · 2 over" — immediate status.

**Main content — muscle group list:**

Each muscle group gets a row containing:
1. **Muscle name** (left-aligned, bold)
2. **Current volume** (right-aligned, colored by status)
3. **Week-over-week change** (↑12% or ↓5%, right of volume)
4. **Progress bar** (full width, gradient-colored by status):
   - Green gradient: 70-110% of target (on target)
   - Amber gradient: below 70% of target (under)
   - Red gradient: above 110% of target (over)
5. **Target reference** (below bar, small text: "Target: 10,000 kg · 82%")

**Sort order matters:** Don't sort alphabetically. Sort by STATUS:
- Under-target muscles first (these need attention)
- On-target muscles second
- Over-target muscles last
Within each group, sort by percentage (lowest first for under, highest
first for over).

This ensures the user immediately sees what needs attention without
scrolling. The muscles they're neglecting are always at the top.

**Weekly trends section (below the list):**

A mini bar chart (last 8 weeks) for the currently selected muscle group
(user taps a muscle to see its trend). Bars are colored by that week's
status. This answers "am I progressively overloading?" at a glance.

**Muscle heatmap (tab or toggle):**

An alternate view of the same data — a simplified body outline (front/back
toggle) where each muscle region is color-filled based on status. This is
the "screenshot moment" — the view users will share with friends. Make it
beautiful and immediately intuitive.

The heatmap is NOT the primary view — the list with progress bars is more
actionable. The heatmap is the "feel" view, the list is the "think" view.

### Exercise Library

**Purpose:** Find exercises, understand what they target, check history.

**Search is the primary interaction.** The search bar is prominent and
always visible (not behind a tap). Users know what exercise they want —
they're searching by name.

**Filter by muscle group** is secondary — used when browsing ("what other
back exercises could I try?"). Horizontal chip row below search.

**Each exercise card shows:**
- Exercise name
- Primary muscle tag (green-tinted)
- Secondary muscle tags (purple-tinted) — this matters for volume accuracy
- Personal record (top set weight × reps, or estimated 1RM)
- Last performed date

**Tapping an exercise opens its detail page:**
- Full history of this exercise (every session where it was performed)
- PR tracking with visual indicator of when PRs were set
- Volume contribution chart (how much this exercise contributes to each
  muscle group's weekly volume)
- Notes (user can add form cues, tips for themselves)

**Custom exercises:** Users can create their own and assign primary +
secondary muscles. This is important because equipment varies by gym and
users have unique exercise names. Make creation fast — name + muscle
selection + done. Don't require a description or image.

### Workout History

**Purpose:** Review past workouts, track consistency, repeat sessions.

**Date-grouped list** (most recent first). Each entry is a card showing:
- Workout name (or "Workout" if unnamed)
- Date, duration, total volume, exercise count
- Muscle group tags (which groups were worked)
- Chevron indicating tap for detail

**Week summary card** at the top of each week group:
- Workouts completed that week
- Total time spent
- Total volume
- Day-of-week dots showing which days had workouts (visual consistency check)

**Detail view (tap into a workout):**
- Full exercise list with all sets, weights, reps
- Per-exercise volume contribution
- Option to "Repeat this workout" (starts a new session pre-filled with
  the same exercises and sets)
- Option to delete

### Templates

**Purpose:** Reduce friction for starting workouts.

**Keep it simple.** A template is just a name + an ordered list of exercises
with optional set/rep targets. That's it.

**Pre-built templates** (shipped with app):
- Push / Pull / Legs (3 templates)
- Upper / Lower (2 templates)
- Full Body (1 template)
Each with sensible exercise selections for intermediate lifters.

**Creating a template:** Either save from a completed workout, or build
from scratch using the exercise picker. Both flows must be fast.

**Starting from template:** Tapping "Start" on a template creates a new
session with those exercises pre-loaded, sets pre-filled from the last
time that template was used. The user can modify anything during the session
(add/remove exercises, change weights). The template itself is not modified
unless the user explicitly saves changes back.

### Settings

**Purpose:** Customize units, rest timers, volume targets.

**Settings that matter most:**
1. **Unit preference:** kg / lbs. Affects ALL displays and inputs globally.
   Default to kg. Switching recalculates all displayed values (but stored
   data remains in original unit — conversion is a display concern).
2. **Volume targets per muscle group:** This is critical. The user sets
   their target weekly volume for each muscle group. Provide smart defaults
   based on research-backed recommendations, but let them customize freely.
   Show a brief explanation of what the numbers mean.
3. **Default rest timer:** 60 / 90 / 120 / 180 / 300 seconds. Can be
   overridden per exercise.
4. **Theme:** System / Light / Dark. Default to system.
5. **Export/Import data:** JSON backup. This builds trust. Users need to
   know their data is portable and safe.

---

## Volume Calculation — The Product Logic

This is the intellectual core of the app. Get this right.

### Formula

For each completed set:
```
set_volume = weight × reps
```

For each muscle group per week (Monday 00:00 to Sunday 23:59):
```
weekly_volume(muscle) =
  SUM of set_volume for all sets where exercise.primaryMuscle == muscle
  + 0.5 × SUM of set_volume for all sets where muscle IN exercise.secondaryMuscles
```

The 50% secondary attribution is a simplification but it's widely accepted in
exercise science literature and is what serious lifters intuitively expect.

### Only completed sets count

- Sets marked as "completed" (checkmark tapped) count toward volume.
- Warmup sets count (they still contribute volume — the muscle doesn't know
  it's a warmup).
- If a user marks a set type as "Warmup," still include it in volume BUT
  consider adding a setting to exclude warmup sets from volume calculation
  for purists. Default: include everything.

### Target defaults

Provide evidence-based default targets. These are approximate and users should
adjust, but good defaults prevent the "I have no idea what to set" problem:

```
Large muscle groups (Chest, Back, Quads): 10,000 – 15,000 kg/week
Medium groups (Shoulders, Hamstrings, Glutes): 6,000 – 10,000 kg/week
Small groups (Biceps, Triceps, Calves, Abs): 3,000 – 6,000 kg/week
Traps, Forearms, Lats: 4,000 – 8,000 kg/week
```

Note: these targets assume intermediate male lifters. The app should make
it easy to adjust. Consider adding "experience level" presets (intermediate /
advanced) that set reasonable defaults.

### Week boundary

Week runs Monday 00:00 to Sunday 23:59 in the user's local timezone.
This matches how most training programs are structured. Do NOT use
calendar weeks that start on Sunday — the gym world thinks Monday-first.

---

## Micro-Interactions & Polish Details

These small things separate "functional but boring" from "I love using this":

### 1. PR celebration
When a user logs a set that beats their previous best (heavier weight at
same or more reps), show a brief celebration:
- The set row gets a subtle golden glow/border for 2 seconds
- A small "PR" badge appears next to the set
- Optional: subtle haptic feedback (short vibration)
Do NOT show a modal, popup, or confetti animation. Keep it subtle and fast.
The user is mid-workout.

### 2. Completed set satisfaction
When the checkmark is tapped:
- The checkmark fills with the primary color (green/teal) immediately
- The set row background shifts slightly (very subtle surface color change)
- The weight and reps text become fully opaque (they were slightly muted
  before completion, indicating "pending")
- Haptic: light tap feedback

### 3. Rest timer urgency
- 0–10 seconds remaining: timer text shifts to amber/red, optional vibration
- Timer complete: brief vibration pattern, text shows "GO" for 3 seconds
  before resetting
- Timer is dismissible at any point with a single tap

### 4. Volume progress animations
On the volume dashboard, progress bars should animate when data changes:
- On screen load: bars animate from 0 to current value (500ms, ease-out)
- On week change: bars animate from old value to new value
This gives a sense of progress and makes the data feel alive.

### 5. Empty states that guide
Every screen with no data should show:
- A simple illustration or icon (not decorative — functional)
- One line of text explaining what will appear here
- A CTA to take the action that will populate this screen

Examples:
- History empty: "Your first workout will appear here" + "Start workout" button
- Volume empty: "Complete a workout to see your weekly volume" + "Start workout"
- Exercise detail empty: "You haven't done this exercise yet" + "Start a workout
  with this exercise" button

### 6. Workout in progress persistence
If the app is killed or the phone restarts during an active workout:
- The workout state is preserved in the database
- On next app open, show a banner: "You have an active workout — resume?"
- The timer continues from where it left off (recalculated from start time)
- NEVER lose workout data. This is a trust-destroying experience.

---

## What We DON'T Build

Equally important to what we build. These are explicit exclusions:

- **No social features.** No sharing, no followers, no leaderboards. This
  is a personal tool.
- **No guided workout programs.** We don't tell users what to do. We track
  what they choose to do and help them optimize it.
- **No calorie tracking or nutrition.** Separate domain. Stay focused.
- **No cardio tracking.** This is for resistance training. Cardio apps exist.
- **No AI coaching or recommendations.** The volume data IS the coaching.
  The user interprets it. We don't say "you should train legs more" — we
  show them their legs are at 35% of target and let them decide.
- **No account creation or login.** The app is local-first. No server, no
  account. Less friction, more trust.
- **No ads, no premium tier, no in-app purchases.** This is a focused tool.
  (Monetization decisions can come later, but the UX must not be designed
  around them.)

---

## Success Metrics (How We Know It's Working)

If we had analytics (even local-only tracking), these are the metrics
that prove the product is working:

1. **Workout completion rate:** % of started workouts that are finished
   (target: >90%). If people abandon mid-workout, logging is too painful.
2. **Sessions per week:** Average 3-5 for our target user. If below 2,
   the app isn't becoming habitual.
3. **Time to log a set:** From tapping the row to tapping the checkmark.
   Target: under 5 seconds for a set that matches the previous session,
   under 10 seconds for a modified set.
4. **Volume dashboard visits per week:** Target: 3+ visits. If users check
   their volume regularly, the core feature is resonating.
5. **Template usage rate:** % of workouts started from templates vs empty.
   Higher template use = less friction = more consistency.

---

## Implementation Notes for Claude CLI

When building this app, remember:

1. **Volume is not a report — it's the UI.** Volume data should be computed
   reactively (using Room + Flow) and surface in real time. Don't batch-
   calculate it on a schedule. When a set is logged, the volume numbers
   update immediately across all screens.

2. **Pre-filling is the killer UX feature.** The logic for "what did the user
   do last time with this exercise" must be fast and correct. Query the most
   recent completed session containing this exercise, get its sets, and pre-
   populate. This single feature saves more time than any fancy UI.

3. **The rest timer is a background concern.** It should work even when the
   screen is off (use a foreground service or WorkManager). A notification
   should show the countdown. The user often puts their phone down between
   sets.

4. **Sort volume dashboard by status, not alphabetically.** Under-target
   muscles first. This is a product decision that drives behavior change.

5. **Week calculation must handle timezone correctly.** Use the device's
   local timezone to determine Monday-Sunday boundaries. Never use UTC.

6. **The muscle heatmap is a Canvas drawing.** Don't try to use images or
   SVG files. Draw simplified body outlines using Compose Canvas paths.
   Each muscle region is a separate path that can be filled independently.
   Keep the silhouette minimal — it's a data visualization, not an anatomy
   textbook.
