package com.example.volumelift.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ActiveWorkout : Screen("active_workout/{sessionId}") {
        fun createRoute(sessionId: Long) = "active_workout/$sessionId"
    }
    data object ExercisePicker : Screen("exercise_picker/{sessionId}") {
        fun createRoute(sessionId: Long) = "exercise_picker/$sessionId"
    }
    data object ExerciseLibrary : Screen("exercise_library")
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: Long) = "exercise_detail/$exerciseId"
    }
    data object History : Screen("history")
    data object WorkoutDetail : Screen("workout_detail/{sessionId}") {
        fun createRoute(sessionId: Long) = "workout_detail/$sessionId"
    }
    data object Volume : Screen("volume")
    data object Templates : Screen("templates")
    data object CreateTemplate : Screen("create_template")
    data object EditTemplate : Screen("edit_template/{templateId}") {
        fun createRoute(templateId: Long) = "edit_template/$templateId"
    }
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")
    data object BodyWeight : Screen("body_weight")
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
    BottomNavItem("History", Icons.Default.History, Screen.History.route),
    BottomNavItem("Volume", Icons.AutoMirrored.Filled.ShowChart, Screen.Volume.route),
    BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
)
