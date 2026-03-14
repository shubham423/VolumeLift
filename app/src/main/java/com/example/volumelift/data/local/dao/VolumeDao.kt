package com.example.volumelift.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class ExerciseVolumeResult(
    val exerciseId: Long,
    val primaryMuscleGroup: String,
    val secondaryMuscleGroups: String,
    val totalVolume: Double,
    val setCount: Int
)

@Dao
interface VolumeDao {
    @Query("""
        SELECT
            e.id as exerciseId,
            e.primaryMuscleGroup,
            e.secondaryMuscleGroups,
            SUM(sl.weight * sl.reps) as totalVolume,
            COUNT(sl.id) as setCount
        FROM set_logs sl
        INNER JOIN exercise_logs el ON sl.exerciseLogId = el.id
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        INNER JOIN exercises e ON el.exerciseId = e.id
        WHERE ws.startTime >= :startTime
            AND ws.startTime <= :endTime
            AND ws.isCompleted = 1
            AND sl.isCompleted = 1
            AND sl.setType != 'Warmup'
        GROUP BY e.id
    """)
    fun getVolumeByExerciseInRange(startTime: Long, endTime: Long): Flow<List<ExerciseVolumeResult>>
}
