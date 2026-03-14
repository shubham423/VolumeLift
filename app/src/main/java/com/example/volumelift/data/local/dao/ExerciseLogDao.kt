package com.example.volumelift.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.volumelift.data.local.entity.ExerciseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLogDao {
    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY `order` ASC")
    fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLogEntity>>

    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY `order` ASC")
    suspend fun getLogsForSessionOnce(sessionId: Long): List<ExerciseLogEntity>

    @Query("SELECT * FROM exercise_logs WHERE exerciseId = :exerciseId ORDER BY sessionId DESC")
    fun getLogsForExercise(exerciseId: Long): Flow<List<ExerciseLogEntity>>

    @Query("SELECT * FROM exercise_logs WHERE id = :id")
    suspend fun getLogById(id: Long): ExerciseLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ExerciseLogEntity): Long

    @Update
    suspend fun updateLog(log: ExerciseLogEntity)

    @Delete
    suspend fun deleteLog(log: ExerciseLogEntity)

    @Query("DELETE FROM exercise_logs WHERE sessionId = :sessionId")
    suspend fun deleteLogsForSession(sessionId: Long)
}
