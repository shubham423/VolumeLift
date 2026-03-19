package com.solostackdev.volumelift.data.local.db

import com.solostackdev.volumelift.data.local.entity.ExerciseEntity
import com.solostackdev.volumelift.data.local.entity.MuscleGroup
import com.solostackdev.volumelift.data.local.entity.WorkoutTemplateEntity

object PrepopulateData {
    val exercises = listOf(
        // Chest
        ExerciseEntity(name = "Barbell Bench Press", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Triceps, MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Incline Barbell Bench Press", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Triceps, MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Dumbbell Bench Press", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Triceps, MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Incline Dumbbell Press", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Triceps, MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Cable Flyes", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Dumbbell Flyes", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Push Ups", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Triceps, MuscleGroup.Shoulders)),
        ExerciseEntity(name = "Chest Dips", primaryMuscleGroup = MuscleGroup.Chest, secondaryMuscleGroups = listOf(MuscleGroup.Triceps, MuscleGroup.Shoulders)),

        // Back
        ExerciseEntity(name = "Barbell Row", primaryMuscleGroup = MuscleGroup.Back, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Lats)),
        ExerciseEntity(name = "Dumbbell Row", primaryMuscleGroup = MuscleGroup.Back, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Lats)),
        ExerciseEntity(name = "Seated Cable Row", primaryMuscleGroup = MuscleGroup.Back, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Lats)),
        ExerciseEntity(name = "T-Bar Row", primaryMuscleGroup = MuscleGroup.Back, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Lats)),

        // Lats
        ExerciseEntity(name = "Pull Ups", primaryMuscleGroup = MuscleGroup.Lats, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Back)),
        ExerciseEntity(name = "Lat Pulldown", primaryMuscleGroup = MuscleGroup.Lats, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Back)),
        ExerciseEntity(name = "Close Grip Lat Pulldown", primaryMuscleGroup = MuscleGroup.Lats, secondaryMuscleGroups = listOf(MuscleGroup.Biceps, MuscleGroup.Back)),
        ExerciseEntity(name = "Chin Ups", primaryMuscleGroup = MuscleGroup.Lats, secondaryMuscleGroups = listOf(MuscleGroup.Biceps)),

        // Shoulders
        ExerciseEntity(name = "Overhead Press", primaryMuscleGroup = MuscleGroup.Shoulders, secondaryMuscleGroups = listOf(MuscleGroup.Triceps)),
        ExerciseEntity(name = "Dumbbell Shoulder Press", primaryMuscleGroup = MuscleGroup.Shoulders, secondaryMuscleGroups = listOf(MuscleGroup.Triceps)),
        ExerciseEntity(name = "Lateral Raises", primaryMuscleGroup = MuscleGroup.Shoulders, secondaryMuscleGroups = listOf(MuscleGroup.Traps)),
        ExerciseEntity(name = "Front Raises", primaryMuscleGroup = MuscleGroup.Shoulders, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Rear Delt Flyes", primaryMuscleGroup = MuscleGroup.Shoulders, secondaryMuscleGroups = listOf(MuscleGroup.Back)),
        ExerciseEntity(name = "Face Pulls", primaryMuscleGroup = MuscleGroup.Shoulders, secondaryMuscleGroups = listOf(MuscleGroup.Traps, MuscleGroup.Back)),

        // Biceps
        ExerciseEntity(name = "Barbell Curl", primaryMuscleGroup = MuscleGroup.Biceps, secondaryMuscleGroups = listOf(MuscleGroup.Forearms)),
        ExerciseEntity(name = "Dumbbell Curl", primaryMuscleGroup = MuscleGroup.Biceps, secondaryMuscleGroups = listOf(MuscleGroup.Forearms)),
        ExerciseEntity(name = "Hammer Curl", primaryMuscleGroup = MuscleGroup.Biceps, secondaryMuscleGroups = listOf(MuscleGroup.Forearms)),
        ExerciseEntity(name = "Incline Dumbbell Curl", primaryMuscleGroup = MuscleGroup.Biceps, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Cable Curl", primaryMuscleGroup = MuscleGroup.Biceps, secondaryMuscleGroups = listOf(MuscleGroup.Forearms)),
        ExerciseEntity(name = "Preacher Curl", primaryMuscleGroup = MuscleGroup.Biceps, secondaryMuscleGroups = emptyList()),

        // Triceps
        ExerciseEntity(name = "Tricep Pushdown", primaryMuscleGroup = MuscleGroup.Triceps, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Overhead Tricep Extension", primaryMuscleGroup = MuscleGroup.Triceps, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Skull Crushers", primaryMuscleGroup = MuscleGroup.Triceps, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Close Grip Bench Press", primaryMuscleGroup = MuscleGroup.Triceps, secondaryMuscleGroups = listOf(MuscleGroup.Chest)),
        ExerciseEntity(name = "Tricep Dips", primaryMuscleGroup = MuscleGroup.Triceps, secondaryMuscleGroups = listOf(MuscleGroup.Chest)),

        // Quads
        ExerciseEntity(name = "Barbell Squat", primaryMuscleGroup = MuscleGroup.Quads, secondaryMuscleGroups = listOf(MuscleGroup.Glutes, MuscleGroup.Hamstrings)),
        ExerciseEntity(name = "Front Squat", primaryMuscleGroup = MuscleGroup.Quads, secondaryMuscleGroups = listOf(MuscleGroup.Glutes)),
        ExerciseEntity(name = "Leg Press", primaryMuscleGroup = MuscleGroup.Quads, secondaryMuscleGroups = listOf(MuscleGroup.Glutes)),
        ExerciseEntity(name = "Leg Extension", primaryMuscleGroup = MuscleGroup.Quads, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Bulgarian Split Squat", primaryMuscleGroup = MuscleGroup.Quads, secondaryMuscleGroups = listOf(MuscleGroup.Glutes)),
        ExerciseEntity(name = "Lunges", primaryMuscleGroup = MuscleGroup.Quads, secondaryMuscleGroups = listOf(MuscleGroup.Glutes, MuscleGroup.Hamstrings)),

        // Hamstrings
        ExerciseEntity(name = "Romanian Deadlift", primaryMuscleGroup = MuscleGroup.Hamstrings, secondaryMuscleGroups = listOf(MuscleGroup.Glutes, MuscleGroup.Back)),
        ExerciseEntity(name = "Leg Curl", primaryMuscleGroup = MuscleGroup.Hamstrings, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Stiff Leg Deadlift", primaryMuscleGroup = MuscleGroup.Hamstrings, secondaryMuscleGroups = listOf(MuscleGroup.Glutes, MuscleGroup.Back)),

        // Glutes
        ExerciseEntity(name = "Hip Thrust", primaryMuscleGroup = MuscleGroup.Glutes, secondaryMuscleGroups = listOf(MuscleGroup.Hamstrings)),
        ExerciseEntity(name = "Glute Bridge", primaryMuscleGroup = MuscleGroup.Glutes, secondaryMuscleGroups = listOf(MuscleGroup.Hamstrings)),
        ExerciseEntity(name = "Cable Kickback", primaryMuscleGroup = MuscleGroup.Glutes, secondaryMuscleGroups = emptyList()),

        // Calves
        ExerciseEntity(name = "Standing Calf Raise", primaryMuscleGroup = MuscleGroup.Calves, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Seated Calf Raise", primaryMuscleGroup = MuscleGroup.Calves, secondaryMuscleGroups = emptyList()),

        // Abs
        ExerciseEntity(name = "Crunches", primaryMuscleGroup = MuscleGroup.Abs, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Hanging Leg Raise", primaryMuscleGroup = MuscleGroup.Abs, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Cable Crunch", primaryMuscleGroup = MuscleGroup.Abs, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Ab Rollout", primaryMuscleGroup = MuscleGroup.Abs, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Plank", primaryMuscleGroup = MuscleGroup.Abs, secondaryMuscleGroups = emptyList()),

        // Traps
        ExerciseEntity(name = "Barbell Shrugs", primaryMuscleGroup = MuscleGroup.Traps, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Dumbbell Shrugs", primaryMuscleGroup = MuscleGroup.Traps, secondaryMuscleGroups = emptyList()),

        // Forearms
        ExerciseEntity(name = "Wrist Curl", primaryMuscleGroup = MuscleGroup.Forearms, secondaryMuscleGroups = emptyList()),
        ExerciseEntity(name = "Reverse Wrist Curl", primaryMuscleGroup = MuscleGroup.Forearms, secondaryMuscleGroups = emptyList()),

        // Compound
        ExerciseEntity(name = "Deadlift", primaryMuscleGroup = MuscleGroup.Back, secondaryMuscleGroups = listOf(MuscleGroup.Hamstrings, MuscleGroup.Glutes, MuscleGroup.Traps, MuscleGroup.Forearms)),
    )

    val defaultTemplates = listOf(
        WorkoutTemplateEntity(name = "Push Day", exerciseIds = listOf(1, 3, 5, 17, 19, 29), notes = "Chest, Shoulders, Triceps"),
        WorkoutTemplateEntity(name = "Pull Day", exerciseIds = listOf(9, 14, 13, 22, 23, 25), notes = "Back, Biceps"),
        WorkoutTemplateEntity(name = "Leg Day", exerciseIds = listOf(33, 36, 39, 40, 41, 44), notes = "Quads, Hamstrings, Glutes, Calves"),
        WorkoutTemplateEntity(name = "Upper Body", exerciseIds = listOf(1, 9, 17, 14, 23, 29), notes = "Full upper body workout"),
        WorkoutTemplateEntity(name = "Lower Body", exerciseIds = listOf(33, 39, 36, 41, 42, 44), notes = "Full lower body workout"),
        WorkoutTemplateEntity(name = "Full Body", exerciseIds = listOf(1, 33, 9, 17, 23, 29, 44), notes = "Hit every muscle group"),
    )
}
