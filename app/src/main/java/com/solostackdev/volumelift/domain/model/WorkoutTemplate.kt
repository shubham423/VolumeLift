package com.solostackdev.volumelift.domain.model

data class WorkoutTemplate(
    val id: Long = 0,
    val name: String,
    val exerciseIds: List<Long> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val notes: String = ""
)
