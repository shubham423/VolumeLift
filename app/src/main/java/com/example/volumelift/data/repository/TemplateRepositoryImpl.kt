package com.example.volumelift.data.repository

import com.example.volumelift.data.local.dao.ExerciseDao
import com.example.volumelift.data.local.dao.WorkoutTemplateDao
import com.example.volumelift.data.mapper.toDomain
import com.example.volumelift.data.mapper.toEntity
import com.example.volumelift.domain.model.WorkoutTemplate
import com.example.volumelift.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
    private val templateDao: WorkoutTemplateDao,
    private val exerciseDao: ExerciseDao
) : TemplateRepository {

    override fun getAllTemplates(): Flow<List<WorkoutTemplate>> =
        templateDao.getAllTemplates().map { templates ->
            templates.map { template ->
                val exercises = template.exerciseIds.mapNotNull { id ->
                    exerciseDao.getExerciseById(id)?.toDomain()
                }
                template.toDomain(exercises)
            }
        }

    override suspend fun getTemplateById(id: Long): WorkoutTemplate? {
        val template = templateDao.getTemplateById(id) ?: return null
        val exercises = template.exerciseIds.mapNotNull { exerciseId ->
            exerciseDao.getExerciseById(exerciseId)?.toDomain()
        }
        return template.toDomain(exercises)
    }

    override suspend fun insertTemplate(template: WorkoutTemplate): Long =
        templateDao.insertTemplate(template.toEntity())

    override suspend fun updateTemplate(template: WorkoutTemplate) =
        templateDao.updateTemplate(template.toEntity())

    override suspend fun deleteTemplate(template: WorkoutTemplate) =
        templateDao.deleteTemplate(template.toEntity())
}
