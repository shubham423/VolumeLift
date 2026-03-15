package com.example.volumelift.presentation.theme

import androidx.compose.ui.graphics.Color

// Background hierarchy (dark theme)
val Background = Color(0xFF1A1A1E)
val Surface = Color(0xFF242428)
val SurfaceVariant = Color(0xFF2A2A2E)
val Border = Color(0xFF3A3A3E)

// Primary accent (teal)
val Primary = Color(0xFF2A6B5A)
val PrimaryLight = Color(0xFF6FD4AF)
val PrimaryDark = Color(0xFF1A4A3E)
val PrimaryContainer = Color(0xFF1A2E26)

// Text colors
val TextPrimary = Color(0xFFF0F0F0)
val TextSecondary = Color(0xFF8E8E93)
val TextTertiary = Color(0xFF6B6B70)

// Semantic: Volume status
val OnTarget = Color(0xFF6FD4AF)
val OnTargetBg = Color(0xFF1A2E26)
val UnderTarget = Color(0xFFE8A94F)
val UnderTargetBg = Color(0xFF2E2A1A)
val OverTarget = Color(0xFFE85D5D)
val OverTargetBg = Color(0xFF2E1A1A)

// Muscle group category colors (for tags)
val MuscleChestColor = Color(0xFFE8A94F)
val MuscleChestBg = Color(0xFF3A2A1A)
val MuscleBackColor = Color(0xFF6FD4AF)
val MuscleBackBg = Color(0xFF1A2A2A)
val MuscleLegsColor = Color(0xFFC490D4)
val MuscleLegsBg = Color(0xFF2A1A2A)
val MuscleSecondaryColor = Color(0xFF9090D4)
val MuscleSecondaryBg = Color(0xFF1A1A2E)

// Rest timer
val RestTimerBg = Color(0xFF2A2520)
val RestTimerButtonBg = Color(0xFF3A3530)

// Backward compat aliases used by screens
val VolumeOnTarget = OnTarget
val VolumeUnder = UnderTarget
val VolumeOver = OverTarget

val GradientStart = Primary
val GradientMid = PrimaryDark
val GradientEnd = PrimaryLight
