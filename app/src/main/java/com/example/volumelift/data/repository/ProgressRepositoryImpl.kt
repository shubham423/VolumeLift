package com.example.volumelift.data.repository

import com.example.volumelift.data.local.dao.SetLogDao
import com.example.volumelift.data.mapper.toDomain
import com.example.volumelift.domain.model.WorkoutSet
import com.example.volumelift.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val setLogDao: SetLogDao
) : ProgressRepository {

    override suspend fun getBestSetForExercise(exerciseId: Long): WorkoutSet? =
        setLogDao.getBestSetForExercise(exerciseId)?.toDomain()

    override fun getAllCompletedSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>> =
        setLogDao.getAllCompletedSetsForExercise(exerciseId).map { list -> list.map { it.toDomain() } }
}
