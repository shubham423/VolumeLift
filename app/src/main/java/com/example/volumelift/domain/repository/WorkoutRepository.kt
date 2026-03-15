package com.example.volumelift.domain.repository

import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getCompletedSessions(): Flow<List<WorkoutSession>>
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<WorkoutSession>>
    suspend fun getSessionById(id: Long): WorkoutSession?
    suspend fun getActiveSession(): WorkoutSession?
    suspend fun startSession(templateId: Long? = null): Long
    suspend fun completeSession(sessionId: Long)
    suspend fun deleteSession(sessionId: Long)
    suspend fun updateSessionNotes(sessionId: Long, notes: String)
    suspend fun addExerciseToSession(sessionId: Long, exerciseId: Long): Long
    suspend fun removeExerciseFromSession(exerciseLogId: Long)
    suspend fun reorderExercises(sessionId: Long, exerciseLogs: List<ExerciseLogWithSets>)
    fun getExerciseLogsForSession(sessionId: Long): Flow<List<ExerciseLogWithSets>>
    suspend fun addSet(exerciseLogId: Long, set: WorkoutSet): Long
    suspend fun updateSet(set: WorkoutSet)
    suspend fun deleteSet(setId: Long)
    suspend fun getFullSession(sessionId: Long): WorkoutSession?
    suspend fun getPreviousSetsForExercise(exerciseId: Long, currentSessionId: Long): List<WorkoutSet>
    fun getSessionCountInRange(startTime: Long, endTime: Long): Flow<Int>
}
