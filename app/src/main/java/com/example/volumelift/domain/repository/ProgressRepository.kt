package com.example.volumelift.domain.repository

import com.example.volumelift.data.local.entity.SetLogEntity
import com.example.volumelift.domain.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    suspend fun getBestSetForExercise(exerciseId: Long): WorkoutSet?
    fun getAllCompletedSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>>
}
