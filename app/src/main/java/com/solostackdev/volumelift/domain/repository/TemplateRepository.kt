package com.solostackdev.volumelift.domain.repository

import com.solostackdev.volumelift.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    fun getAllTemplates(): Flow<List<WorkoutTemplate>>
    suspend fun getTemplateById(id: Long): WorkoutTemplate?
    suspend fun insertTemplate(template: WorkoutTemplate): Long
    suspend fun updateTemplate(template: WorkoutTemplate)
    suspend fun deleteTemplate(template: WorkoutTemplate)
}
