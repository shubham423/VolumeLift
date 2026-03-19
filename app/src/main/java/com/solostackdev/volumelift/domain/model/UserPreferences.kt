package com.solostackdev.volumelift.domain.model

import com.solostackdev.volumelift.data.local.entity.MuscleGroup

data class UserPreferences(
    val useKg: Boolean = true,
    val defaultRestTimerSeconds: Int = 90,
    val themeMode: ThemeMode = ThemeMode.System,
    val volumeTargets: Map<MuscleGroup, Double> = defaultVolumeTargets(),
    val setTargets: Map<MuscleGroup, Int> = defaultSetTargets()
)

enum class ThemeMode {
    Light, Dark, System
}

fun defaultVolumeTargets(): Map<MuscleGroup, Double> = mapOf(
    MuscleGroup.Chest to 10000.0,
    MuscleGroup.Back to 12000.0,
    MuscleGroup.Shoulders to 8000.0,
    MuscleGroup.Biceps to 5000.0,
    MuscleGroup.Triceps to 5000.0,
    MuscleGroup.Quads to 15000.0,
    MuscleGroup.Hamstrings to 10000.0,
    MuscleGroup.Glutes to 12000.0,
    MuscleGroup.Calves to 5000.0,
    MuscleGroup.Abs to 5000.0,
    MuscleGroup.Forearms to 3000.0,
    MuscleGroup.Traps to 5000.0,
    MuscleGroup.Lats to 10000.0
)

fun defaultSetTargets(): Map<MuscleGroup, Int> = mapOf(
    MuscleGroup.Chest to 16,
    MuscleGroup.Back to 16,
    MuscleGroup.Shoulders to 16,
    MuscleGroup.Biceps to 12,
    MuscleGroup.Triceps to 12,
    MuscleGroup.Quads to 16,
    MuscleGroup.Hamstrings to 12,
    MuscleGroup.Glutes to 12,
    MuscleGroup.Calves to 10,
    MuscleGroup.Abs to 10,
    MuscleGroup.Forearms to 8,
    MuscleGroup.Traps to 10,
    MuscleGroup.Lats to 14
)
