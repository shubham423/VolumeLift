package com.solostackdev.volumelift.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "set_logs",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseLogId")]
)
data class SetLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseLogId: Long,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val setType: SetType = SetType.Working,
    val isCompleted: Boolean = false,
    val restTimerSeconds: Int = 90
)
