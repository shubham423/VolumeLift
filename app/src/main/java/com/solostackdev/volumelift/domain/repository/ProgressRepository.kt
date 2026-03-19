package com.solostackdev.volumelift.domain.repository

import com.solostackdev.volumelift.data.local.entity.SetLogEntity
import com.solostackdev.volumelift.domain.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    suspend fun getBestSetForExercise(exerciseId: Long): WorkoutSet?
    fun getAllCompletedSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>>
}
