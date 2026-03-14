package com.example.volumelift.di

import com.example.volumelift.data.repository.BodyWeightRepositoryImpl
import com.example.volumelift.data.repository.ExerciseRepositoryImpl
import com.example.volumelift.data.repository.ProgressRepositoryImpl
import com.example.volumelift.data.repository.TemplateRepositoryImpl
import com.example.volumelift.data.repository.UserPreferencesRepositoryImpl
import com.example.volumelift.data.repository.VolumeRepositoryImpl
import com.example.volumelift.data.repository.WorkoutRepositoryImpl
import com.example.volumelift.domain.repository.BodyWeightRepository
import com.example.volumelift.domain.repository.ExerciseRepository
import com.example.volumelift.domain.repository.ProgressRepository
import com.example.volumelift.domain.repository.TemplateRepository
import com.example.volumelift.domain.repository.UserPreferencesRepository
import com.example.volumelift.domain.repository.VolumeRepository
import com.example.volumelift.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindTemplateRepository(impl: TemplateRepositoryImpl): TemplateRepository

    @Binds
    @Singleton
    abstract fun bindVolumeRepository(impl: VolumeRepositoryImpl): VolumeRepository

    @Binds
    @Singleton
    abstract fun bindBodyWeightRepository(impl: BodyWeightRepositoryImpl): BodyWeightRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository
}
