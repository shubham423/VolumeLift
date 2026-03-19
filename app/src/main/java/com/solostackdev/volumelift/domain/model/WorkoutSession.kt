package com.solostackdev.volumelift.domain.model

data class WorkoutSession(
    val id: Long = 0,
    val templateId: Long? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val exerciseLogs: List<ExerciseLogWithSets> = emptyList()
)

data class ExerciseLogWithSets(
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String = "",
    val primaryMuscleGroup: String = "",
    val order: Int,
    val sets: List<WorkoutSet> = emptyList()
)

data class WorkoutSet(
    val id: Long = 0,
    val exerciseLogId: Long = 0,
    val setNumber: Int,
    val weight: Double = 0.0,
    val reps: Int = 0,
    val setType: com.solostackdev.volumelift.data.local.entity.SetType = com.solostackdev.volumelift.data.local.entity.SetType.Working,
    val isCompleted: Boolean = false,
    val restTimerSeconds: Int = 90
)
