package com.example.volumelift.data.repository

import com.example.volumelift.data.local.dao.ExerciseDao
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.data.mapper.toDomain
import com.example.volumelift.data.mapper.toEntity
import com.example.volumelift.domain.model.Exercise
import com.example.volumelift.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises().map { list -> list.map { it.toDomain() } }

    override fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>> =
        exerciseDao.getExercisesByMuscleGroup(muscleGroup).map { list -> list.map { it.toDomain() } }

    override fun searchExercises(query: String): Flow<List<Exercise>> =
        exerciseDao.searchExercises(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getExerciseById(id: Long): Exercise? =
        exerciseDao.getExerciseById(id)?.toDomain()

    override suspend fun insertExercise(exercise: Exercise): Long =
        exerciseDao.insertExercise(exercise.toEntity())

    override suspend fun updateExercise(exercise: Exercise) =
        exerciseDao.updateExercise(exercise.toEntity())

    override suspend fun deleteExercise(exercise: Exercise) =
        exerciseDao.deleteExercise(exercise.toEntity())
}
