package com.example.volumelift.presentation.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.Exercise
import com.example.volumelift.domain.repository.ExerciseRepository
import com.example.volumelift.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExerciseLibraryUiState {
    data object Loading : ExerciseLibraryUiState()
    data class Success(
        val exercises: List<Exercise> = emptyList(),
        val prMap: Map<Long, Double> = emptyMap(),
        val searchQuery: String = "",
        val selectedMuscleGroup: MuscleGroup? = null
    ) : ExerciseLibraryUiState()
    data class Error(val message: String) : ExerciseLibraryUiState()
}

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseLibraryUiState>(ExerciseLibraryUiState.Loading)
    val uiState: StateFlow<ExerciseLibraryUiState> = _uiState.asStateFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                val prMap = loadPRs(exercises)
                _uiState.value = ExerciseLibraryUiState.Success(exercises = exercises, prMap = prMap)
            }
        }
    }

    private suspend fun loadPRs(exercises: List<Exercise>): Map<Long, Double> {
        val map = mutableMapOf<Long, Double>()
        for (exercise in exercises) {
            val bestSet = progressRepository.getBestSetForExercise(exercise.id)
            if (bestSet != null && bestSet.weight > 0) {
                map[exercise.id] = bestSet.weight
            }
        }
        return map
    }

    fun search(query: String) {
        viewModelScope.launch {
            val current = _uiState.value as? ExerciseLibraryUiState.Success ?: return@launch
            if (query.isBlank() && current.selectedMuscleGroup == null) {
                exerciseRepository.getAllExercises().collect { exercises ->
                    val prMap = loadPRs(exercises)
                    _uiState.value = current.copy(exercises = exercises, prMap = prMap, searchQuery = query)
                }
            } else if (query.isBlank() && current.selectedMuscleGroup != null) {
                exerciseRepository.getExercisesByMuscleGroup(current.selectedMuscleGroup).collect { exercises ->
                    val prMap = loadPRs(exercises)
                    _uiState.value = current.copy(exercises = exercises, prMap = prMap, searchQuery = query)
                }
            } else {
                exerciseRepository.searchExercises(query).collect { exercises ->
                    val filtered = if (current.selectedMuscleGroup != null) {
                        exercises.filter { it.primaryMuscleGroup == current.selectedMuscleGroup }
                    } else exercises
                    val prMap = loadPRs(filtered)
                    _uiState.value = current.copy(exercises = filtered, prMap = prMap, searchQuery = query)
                }
            }
        }
    }

    fun filterByMuscleGroup(muscleGroup: MuscleGroup?) {
        viewModelScope.launch {
            val current = _uiState.value as? ExerciseLibraryUiState.Success ?: return@launch
            if (muscleGroup == null) {
                exerciseRepository.getAllExercises().collect { exercises ->
                    val prMap = loadPRs(exercises)
                    _uiState.value = current.copy(exercises = exercises, prMap = prMap, selectedMuscleGroup = null)
                }
            } else {
                exerciseRepository.getExercisesByMuscleGroup(muscleGroup).collect { exercises ->
                    val prMap = loadPRs(exercises)
                    _uiState.value = current.copy(exercises = exercises, prMap = prMap, selectedMuscleGroup = muscleGroup)
                }
            }
        }
    }

    fun addCustomExercise(
        name: String,
        primaryMuscleGroup: MuscleGroup,
        secondaryMuscleGroups: List<MuscleGroup>,
        notes: String = ""
    ) {
        viewModelScope.launch {
            exerciseRepository.insertExercise(
                Exercise(
                    name = name,
                    primaryMuscleGroup = primaryMuscleGroup,
                    secondaryMuscleGroups = secondaryMuscleGroups,
                    isCustom = true,
                    notes = notes
                )
            )
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.deleteExercise(exercise)
        }
    }
}
