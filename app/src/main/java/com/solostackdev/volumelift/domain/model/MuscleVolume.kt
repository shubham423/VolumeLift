package com.solostackdev.volumelift.domain.model

import com.solostackdev.volumelift.data.local.entity.MuscleGroup

data class MuscleVolume(
    val muscleGroup: MuscleGroup,
    val currentSets: Int,
    val targetSets: Int,
    val previousWeekSets: Int = 0,
    val currentVolume: Double,
    val targetVolume: Double,
    val previousWeekVolume: Double = 0.0
) {
    val setsProgressPercent: Float
        get() = if (targetSets > 0) (currentSets.toFloat() / targetSets * 100f) else 0f

    val setsWeekOverWeekChange: Float
        get() = if (previousWeekSets > 0) {
            ((currentSets - previousWeekSets).toFloat() / previousWeekSets * 100f)
        } else 0f

    val volumeProgressPercent: Float
        get() = if (targetVolume > 0) (currentVolume / targetVolume * 100).toFloat() else 0f

    val volumeWeekOverWeekChange: Float
        get() = if (previousWeekVolume > 0) {
            ((currentVolume - previousWeekVolume) / previousWeekVolume * 100).toFloat()
        } else 0f
}
