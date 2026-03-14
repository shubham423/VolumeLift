package com.example.volumelift.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.volumelift.data.local.dao.BodyWeightDao
import com.example.volumelift.data.local.dao.ExerciseDao
import com.example.volumelift.data.local.dao.ExerciseLogDao
import com.example.volumelift.data.local.dao.SetLogDao
import com.example.volumelift.data.local.dao.VolumeDao
import com.example.volumelift.data.local.dao.WorkoutSessionDao
import com.example.volumelift.data.local.dao.WorkoutTemplateDao
import com.example.volumelift.data.local.entity.BodyWeightEntity
import com.example.volumelift.data.local.entity.ExerciseEntity
import com.example.volumelift.data.local.entity.ExerciseLogEntity
import com.example.volumelift.data.local.entity.SetLogEntity
import com.example.volumelift.data.local.entity.WorkoutSessionEntity
import com.example.volumelift.data.local.entity.WorkoutTemplateEntity

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutTemplateEntity::class,
        WorkoutSessionEntity::class,
        ExerciseLogEntity::class,
        SetLogEntity::class,
        BodyWeightEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun setLogDao(): SetLogDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun bodyWeightDao(): BodyWeightDao
    abstract fun volumeDao(): VolumeDao
}
