package com.solostackdev.volumelift.data.repository

import com.solostackdev.volumelift.data.local.dao.ExerciseDao
import com.solostackdev.volumelift.data.local.dao.ExerciseLogDao
import com.solostackdev.volumelift.data.local.dao.SetLogDao
import com.solostackdev.volumelift.data.local.dao.WorkoutSessionDao
import com.solostackdev.volumelift.data.local.entity.ExerciseLogEntity
import com.solostackdev.volumelift.data.local.entity.WorkoutSessionEntity
import com.solostackdev.volumelift.data.mapper.toDomain
import com.solostackdev.volumelift.data.mapper.toEntity
import com.solostackdev.volumelift.domain.model.ExerciseLogWithSets
import com.solostackdev.volumelift.domain.model.WorkoutSession
import com.solostackdev.volumelift.domain.model.WorkoutSet
import com.solostackdev.volumelift.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val sessionDao: WorkoutSessionDao,
    private val exerciseLogDao: ExerciseLogDao,
    private val setLogDao: SetLogDao,
    private val exerciseDao: ExerciseDao
) : WorkoutRepository {

    override fun getCompletedSessions(): Flow<List<WorkoutSession>> =
        sessionDao.getCompletedSessions().map { sessions ->
            sessions.map { it.toDomain() }
        }

    override fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<WorkoutSession>> =
        sessionDao.getSessionsInRange(startTime, endTime).map { sessions ->
            sessions.map { it.toDomain() }
        }

    override suspend fun getSessionById(id: Long): WorkoutSession? =
        sessionDao.getSessionById(id)?.toDomain()

    override suspend fun getActiveSession(): WorkoutSession? {
        val session = sessionDao.getActiveSession() ?: return null
        return getFullSession(session.id)
    }

    override suspend fun startSession(templateId: Long?): Long {
        val session = WorkoutSessionEntity(
            templateId = templateId,
            startTime = System.currentTimeMillis(),
            isCompleted = false
        )
        return sessionDao.insertSession(session)
    }

    override suspend fun completeSession(sessionId: Long) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.updateSession(
            session.copy(
                endTime = System.currentTimeMillis(),
                isCompleted = true
            )
        )
    }

    override suspend fun deleteSession(sessionId: Long) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.deleteSession(session)
    }

    override suspend fun updateSessionNotes(sessionId: Long, notes: String) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.updateSession(session.copy(notes = notes))
    }

    override suspend fun addExerciseToSession(sessionId: Long, exerciseId: Long): Long {
        val existingLogs = exerciseLogDao.getLogsForSessionOnce(sessionId)
        val order = existingLogs.size
        return exerciseLogDao.insertLog(
            ExerciseLogEntity(
                sessionId = sessionId,
                exerciseId = exerciseId,
                order = order
            )
        )
    }

    override suspend fun removeExerciseFromSession(exerciseLogId: Long) {
        val log = exerciseLogDao.getLogById(exerciseLogId) ?: return
        exerciseLogDao.deleteLog(log)
    }

    override suspend fun reorderExercises(sessionId: Long, exerciseLogs: List<ExerciseLogWithSets>) {
        exerciseLogs.forEachIndexed { index, log ->
            val entity = exerciseLogDao.getLogById(log.id) ?: return@forEachIndexed
            exerciseLogDao.updateLog(entity.copy(order = index))
        }
    }

    override fun getExerciseLogsForSession(sessionId: Long): Flow<List<ExerciseLogWithSets>> =
        exerciseLogDao.getLogsForSession(sessionId).map { logs ->
            logs.map { log ->
                val exercise = exerciseDao.getExerciseById(log.exerciseId)
                val sets = setLogDao.getSetsForExerciseLogOnce(log.id)
                ExerciseLogWithSets(
                    id = log.id,
                    sessionId = log.sessionId,
                    exerciseId = log.exerciseId,
                    exerciseName = exercise?.name ?: "Unknown",
                    primaryMuscleGroup = exercise?.primaryMuscleGroup?.name ?: "",
                    order = log.order,
                    sets = sets.map { it.toDomain() }
                )
            }
        }

    override suspend fun addSet(exerciseLogId: Long, set: WorkoutSet): Long =
        setLogDao.insertSet(set.copy(exerciseLogId = exerciseLogId).toEntity())

    override suspend fun updateSet(set: WorkoutSet) =
        setLogDao.updateSet(set.toEntity())

    override suspend fun deleteSet(setId: Long) {
        val set = setLogDao.getSetById(setId) ?: return
        setLogDao.deleteSet(set)
    }

    override suspend fun getFullSession(sessionId: Long): WorkoutSession? {
        val session = sessionDao.getSessionById(sessionId) ?: return null
        val logs = exerciseLogDao.getLogsForSessionOnce(sessionId)
        val exerciseLogsWithSets = logs.map { log ->
            val exercise = exerciseDao.getExerciseById(log.exerciseId)
            val sets = setLogDao.getSetsForExerciseLogOnce(log.id)
            ExerciseLogWithSets(
                id = log.id,
                sessionId = log.sessionId,
                exerciseId = log.exerciseId,
                exerciseName = exercise?.name ?: "Unknown",
                primaryMuscleGroup = exercise?.primaryMuscleGroup?.name ?: "",
                order = log.order,
                sets = sets.map { it.toDomain() }
            )
        }
        return session.toDomain(exerciseLogsWithSets)
    }

    override suspend fun getPreviousSetsForExercise(exerciseId: Long, currentSessionId: Long): List<WorkoutSet> =
        setLogDao.getPreviousSetsForExercise(exerciseId, currentSessionId).map { it.toDomain() }

    override fun getSessionCountInRange(startTime: Long, endTime: Long): Flow<Int> =
        sessionDao.getSessionCountInRange(startTime, endTime)
}
