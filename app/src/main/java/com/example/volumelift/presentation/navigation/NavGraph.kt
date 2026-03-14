package com.example.volumelift.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.volumelift.presentation.workout.ActiveWorkoutViewModel
import com.example.volumelift.presentation.exercises.ExerciseLibraryScreen
import com.example.volumelift.presentation.history.HistoryScreen
import com.example.volumelift.presentation.history.WorkoutDetailScreen
import com.example.volumelift.presentation.home.HomeScreen
import com.example.volumelift.presentation.progress.BodyWeightScreen
import com.example.volumelift.presentation.settings.SettingsScreen
import com.example.volumelift.presentation.templates.TemplatesScreen
import com.example.volumelift.presentation.volume.VolumeScreen
import com.example.volumelift.presentation.workout.ActiveWorkoutScreen
import com.example.volumelift.presentation.workout.ExercisePickerScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToWorkout = { sessionId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(sessionId))
                },
                onNavigateToTemplates = {
                    navController.navigate(Screen.Templates.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(
            route = Screen.ActiveWorkout.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val viewModel: ActiveWorkoutViewModel = hiltViewModel()

            // Observe result from exercise picker
            val savedStateHandle = backStackEntry.savedStateHandle
            LaunchedEffect(Unit) {
                savedStateHandle.getStateFlow("addedExerciseId", -1L).collect { exerciseId ->
                    if (exerciseId > 0) {
                        viewModel.addExercise(exerciseId)
                        savedStateHandle["addedExerciseId"] = -1L
                    }
                }
            }

            ActiveWorkoutScreen(
                onNavigateToExercisePicker = { sessionId ->
                    navController.navigate(Screen.ExercisePicker.createRoute(sessionId))
                },
                onFinish = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.ExercisePicker.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: return@composable
            ExercisePickerScreen(
                onExerciseSelected = { exerciseId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("addedExerciseId", exerciseId)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToDetail = { sessionId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(sessionId))
                },
                onNavigateToWorkout = { sessionId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(sessionId))
                }
            )
        }

        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) {
            WorkoutDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Volume.route) {
            VolumeScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToExerciseLibrary = {
                    navController.navigate(Screen.ExerciseLibrary.route)
                },
                onNavigateToBodyWeight = {
                    navController.navigate(Screen.BodyWeight.route)
                },
                onNavigateToTemplates = {
                    navController.navigate(Screen.Templates.route)
                }
            )
        }

        composable(Screen.ExerciseLibrary.route) {
            ExerciseLibraryScreen(
                onNavigateToDetail = { /* TODO: exercise detail */ },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Templates.route) {
            TemplatesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BodyWeight.route) {
            BodyWeightScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
