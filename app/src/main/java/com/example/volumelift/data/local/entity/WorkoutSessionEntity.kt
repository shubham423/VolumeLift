package com.example.volumelift.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    indices = [Index("startTime")]
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateId: Long? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val notes: String = "",
    val isCompleted: Boolean = false
)
