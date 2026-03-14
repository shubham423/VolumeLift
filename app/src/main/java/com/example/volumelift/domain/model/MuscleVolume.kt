package com.example.volumelift.domain.model

import com.example.volumelift.data.local.entity.MuscleGroup

data class MuscleVolume(
    val muscleGroup: MuscleGroup,
    val currentVolume: Double,
    val targetVolume: Double,
    val previousWeekVolume: Double = 0.0
) {
    val progressPercent: Float
        get() = if (targetVolume > 0) (currentVolume / targetVolume * 100).toFloat() else 0f

    val weekOverWeekChange: Float
        get() = if (previousWeekVolume > 0) {
            ((currentVolume - previousWeekVolume) / previousWeekVolume * 100).toFloat()
        } else 0f
}
