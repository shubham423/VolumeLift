package com.solostackdev.volumelift.domain.model

import com.solostackdev.volumelift.data.local.entity.MuscleGroup

data class Exercise(
    val id: Long = 0,
    val name: String,
    val primaryMuscleGroup: MuscleGroup,
    val secondaryMuscleGroups: List<MuscleGroup> = emptyList(),
    val isCustom: Boolean = false,
    val notes: String = ""
)
