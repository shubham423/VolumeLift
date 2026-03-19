package com.solostackdev.volumelift.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solostackdev.volumelift.data.local.dao.BodyWeightDao
import com.solostackdev.volumelift.data.local.dao.ExerciseDao
import com.solostackdev.volumelift.data.local.dao.ExerciseLogDao
import com.solostackdev.volumelift.data.local.dao.SetLogDao
import com.solostackdev.volumelift.data.local.dao.VolumeDao
import com.solostackdev.volumelift.data.local.dao.WorkoutSessionDao
import com.solostackdev.volumelift.data.local.dao.WorkoutTemplateDao
import com.solostackdev.volumelift.data.local.db.AppDatabase
import com.solostackdev.volumelift.data.local.db.PrepopulateData
import com.solostackdev.volumelift.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        exerciseDaoProvider: Provider<ExerciseDao>,
        templateDaoProvider: Provider<WorkoutTemplateDao>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val exerciseDao = exerciseDaoProvider.get()
                        val templateDao = templateDaoProvider.get()
                        exerciseDao.insertAll(PrepopulateData.exercises)
                        templateDao.insertAll(PrepopulateData.defaultTemplates)
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideExerciseDao(database: AppDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideWorkoutSessionDao(database: AppDatabase): WorkoutSessionDao = database.workoutSessionDao()

    @Provides
    fun provideExerciseLogDao(database: AppDatabase): ExerciseLogDao = database.exerciseLogDao()

    @Provides
    fun provideSetLogDao(database: AppDatabase): SetLogDao = database.setLogDao()

    @Provides
    fun provideWorkoutTemplateDao(database: AppDatabase): WorkoutTemplateDao = database.workoutTemplateDao()

    @Provides
    fun provideBodyWeightDao(database: AppDatabase): BodyWeightDao = database.bodyWeightDao()

    @Provides
    fun provideVolumeDao(database: AppDatabase): VolumeDao = database.volumeDao()
}
