package com.example.volumelift.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.volumelift.data.local.entity.SetLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetLogDao {
    @Query("SELECT * FROM set_logs WHERE exerciseLogId = :exerciseLogId ORDER BY setNumber ASC")
    fun getSetsForExerciseLog(exerciseLogId: Long): Flow<List<SetLogEntity>>

    @Query("SELECT * FROM set_logs WHERE exerciseLogId = :exerciseLogId ORDER BY setNumber ASC")
    suspend fun getSetsForExerciseLogOnce(exerciseLogId: Long): List<SetLogEntity>

    @Query("SELECT * FROM set_logs WHERE id = :id")
    suspend fun getSetById(id: Long): SetLogEntity?

    @Query("""
        SELECT sl.* FROM set_logs sl
        INNER JOIN exercise_logs el ON sl.exerciseLogId = el.id
        WHERE el.exerciseId = :exerciseId AND sl.isCompleted = 1
        ORDER BY (sl.weight * sl.reps) DESC
        LIMIT 1
    """)
    suspend fun getBestSetForExercise(exerciseId: Long): SetLogEntity?

    @Query("""
        SELECT sl.* FROM set_logs sl
        INNER JOIN exercise_logs el ON sl.exerciseLogId = el.id
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE el.exerciseId = :exerciseId AND sl.isCompleted = 1 AND ws.isCompleted = 1
        ORDER BY ws.startTime DESC
    """)
    fun getAllCompletedSetsForExercise(exerciseId: Long): Flow<List<SetLogEntity>>

    @Query("""
        SELECT sl.* FROM set_logs sl
        INNER JOIN exercise_logs el ON sl.exerciseLogId = el.id
        WHERE el.id = (
            SELECT el2.id FROM exercise_logs el2
            INNER JOIN workout_sessions ws ON el2.sessionId = ws.id
            WHERE el2.exerciseId = :exerciseId
              AND ws.isCompleted = 1
              AND ws.id != :currentSessionId
            ORDER BY ws.startTime DESC
            LIMIT 1
        )
        AND sl.isCompleted = 1
        ORDER BY sl.setNumber ASC
    """)
    suspend fun getPreviousSetsForExercise(exerciseId: Long, currentSessionId: Long): List<SetLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sets: List<SetLogEntity>)

    @Update
    suspend fun updateSet(set: SetLogEntity)

    @Delete
    suspend fun deleteSet(set: SetLogEntity)

    @Query("DELETE FROM set_logs WHERE exerciseLogId = :exerciseLogId")
    suspend fun deleteSetsForExerciseLog(exerciseLogId: Long)
}
