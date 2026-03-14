package com.example.volumelift.presentation.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.Exercise
import com.example.volumelift.domain.repository.ExerciseRepository
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
        val searchQuery: String = "",
        val selectedMuscleGroup: MuscleGroup? = null
    ) : ExerciseLibraryUiState()
    data class Error(val message: String) : ExerciseLibraryUiState()
}

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseLibraryUiState>(ExerciseLibraryUiState.Loading)
    val uiState: StateFlow<ExerciseLibraryUiState> = _uiState.asStateFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                _uiState.value = ExerciseLibraryUiState.Success(exercises = exercises)
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            val current = _uiState.value as? ExerciseLibraryUiState.Success ?: return@launch
            if (query.isBlank() && current.selectedMuscleGroup == null) {
                exerciseRepository.getAllExercises().collect { exercises ->
                    _uiState.value = current.copy(exercises = exercises, searchQuery = query)
                }
            } else if (query.isBlank() && current.selectedMuscleGroup != null) {
                exerciseRepository.getExercisesByMuscleGroup(current.selectedMuscleGroup).collect { exercises ->
                    _uiState.value = current.copy(exercises = exercises, searchQuery = query)
                }
            } else {
                exerciseRepository.searchExercises(query).collect { exercises ->
                    val filtered = if (current.selectedMuscleGroup != null) {
                        exercises.filter { it.primaryMuscleGroup == current.selectedMuscleGroup }
                    } else exercises
                    _uiState.value = current.copy(exercises = filtered, searchQuery = query)
                }
            }
        }
    }

    fun filterByMuscleGroup(muscleGroup: MuscleGroup?) {
        viewModelScope.launch {
            val current = _uiState.value as? ExerciseLibraryUiState.Success ?: return@launch
            if (muscleGroup == null) {
                exerciseRepository.getAllExercises().collect { exercises ->
                    _uiState.value = current.copy(exercises = exercises, selectedMuscleGroup = null)
                }
            } else {
                exerciseRepository.getExercisesByMuscleGroup(muscleGroup).collect { exercises ->
                    _uiState.value = current.copy(exercises = exercises, selectedMuscleGroup = muscleGroup)
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
