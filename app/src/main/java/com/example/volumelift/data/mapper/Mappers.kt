package com.example.volumelift.data.mapper

import com.example.volumelift.data.local.entity.BodyWeightEntity
import com.example.volumelift.data.local.entity.ExerciseEntity
import com.example.volumelift.data.local.entity.ExerciseLogEntity
import com.example.volumelift.data.local.entity.SetLogEntity
import com.example.volumelift.data.local.entity.WorkoutSessionEntity
import com.example.volumelift.data.local.entity.WorkoutTemplateEntity
import com.example.volumelift.domain.model.BodyWeightEntry
import com.example.volumelift.domain.model.Exercise
import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutSet
import com.example.volumelift.domain.model.WorkoutTemplate

fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    primaryMuscleGroup = primaryMuscleGroup,
    secondaryMuscleGroups = secondaryMuscleGroups,
    isCustom = isCustom,
    notes = notes
)

fun Exercise.toEntity() = ExerciseEntity(
    id = id,
    name = name,
    primaryMuscleGroup = primaryMuscleGroup,
    secondaryMuscleGroups = secondaryMuscleGroups,
    isCustom = isCustom,
    notes = notes
)

fun WorkoutSessionEntity.toDomain(exerciseLogs: List<ExerciseLogWithSets> = emptyList()) = WorkoutSession(
    id = id,
    templateId = templateId,
    startTime = startTime,
    endTime = endTime,
    notes = notes,
    isCompleted = isCompleted,
    exerciseLogs = exerciseLogs
)

fun WorkoutSession.toEntity() = WorkoutSessionEntity(
    id = id,
    templateId = templateId,
    startTime = startTime,
    endTime = endTime,
    notes = notes,
    isCompleted = isCompleted
)

fun SetLogEntity.toDomain() = WorkoutSet(
    id = id,
    exerciseLogId = exerciseLogId,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    setType = setType,
    isCompleted = isCompleted,
    restTimerSeconds = restTimerSeconds
)

fun WorkoutSet.toEntity() = SetLogEntity(
    id = id,
    exerciseLogId = exerciseLogId,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    setType = setType,
    isCompleted = isCompleted,
    restTimerSeconds = restTimerSeconds
)

fun WorkoutTemplateEntity.toDomain(exercises: List<Exercise> = emptyList()) = WorkoutTemplate(
    id = id,
    name = name,
    exerciseIds = exerciseIds,
    exercises = exercises,
    notes = notes
)

fun WorkoutTemplate.toEntity() = WorkoutTemplateEntity(
    id = id,
    name = name,
    exerciseIds = exerciseIds,
    notes = notes
)

fun BodyWeightEntity.toDomain() = BodyWeightEntry(
    id = id,
    weight = weight,
    date = date
)

fun BodyWeightEntry.toEntity() = BodyWeightEntity(
    id = id,
    weight = weight,
    date = date
)
