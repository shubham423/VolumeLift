package com.example.volumelift.presentation.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.data.local.entity.SetType
import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutSet
import com.example.volumelift.domain.repository.UserPreferencesRepository
import com.example.volumelift.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActiveWorkoutUiState {
    data object Loading : ActiveWorkoutUiState()
    data class Success(
        val session: WorkoutSession,
        val exerciseLogs: List<ExerciseLogWithSets> = emptyList(),
        val elapsedTime: String = "00:00",
        val restTimerSeconds: Int = 0,
        val isRestTimerRunning: Boolean = false,
        val defaultRestTimer: Int = 90
    ) : ActiveWorkoutUiState()
    data class Error(val message: String) : ActiveWorkoutUiState()
}

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: -1L

    private val _uiState = MutableStateFlow<ActiveWorkoutUiState>(ActiveWorkoutUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null

    init {
        loadSession()
        startTimer()
    }

    private fun loadSession() {
        viewModelScope.launch {
            try {
                val prefs = preferencesRepository.getUserPreferences()
                workoutRepository.getExerciseLogsForSession(sessionId).collect { logs ->
                    val session = workoutRepository.getSessionById(sessionId)
                    if (session != null) {
                        val current = _uiState.value
                        val elapsed = if (current is ActiveWorkoutUiState.Success) current.elapsedTime else "00:00"
                        val restTimer = if (current is ActiveWorkoutUiState.Success) current.restTimerSeconds else 0
                        val isRunning = if (current is ActiveWorkoutUiState.Success) current.isRestTimerRunning else false
                        _uiState.value = ActiveWorkoutUiState.Success(
                            session = session,
                            exerciseLogs = logs,
                            elapsedTime = elapsed,
                            restTimerSeconds = restTimer,
                            isRestTimerRunning = isRunning,
                            defaultRestTimer = prefs.defaultRestTimerSeconds
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ActiveWorkoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _uiState.value
                if (current is ActiveWorkoutUiState.Success) {
                    val elapsed = System.currentTimeMillis() - current.session.startTime
                    val minutes = (elapsed / 1000 / 60).toInt()
                    val seconds = (elapsed / 1000 % 60).toInt()
                    val hours = minutes / 60
                    val displayMinutes = minutes % 60
                    val timeStr = if (hours > 0) {
                        String.format("%d:%02d:%02d", hours, displayMinutes, seconds)
                    } else {
                        String.format("%02d:%02d", displayMinutes, seconds)
                    }
                    _uiState.value = current.copy(elapsedTime = timeStr)
                }
            }
        }
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                val current = _uiState.value
                if (current is ActiveWorkoutUiState.Success) {
                    _uiState.value = current.copy(restTimerSeconds = remaining, isRestTimerRunning = true)
                }
                delay(1000)
                remaining--
            }
            val current = _uiState.value
            if (current is ActiveWorkoutUiState.Success) {
                _uiState.value = current.copy(restTimerSeconds = 0, isRestTimerRunning = false)
            }
        }
    }

    fun cancelRestTimer() {
        restTimerJob?.cancel()
        val current = _uiState.value
        if (current is ActiveWorkoutUiState.Success) {
            _uiState.value = current.copy(restTimerSeconds = 0, isRestTimerRunning = false)
        }
    }

    fun addExercise(exerciseId: Long) {
        viewModelScope.launch {
            workoutRepository.addExerciseToSession(sessionId, exerciseId)
        }
    }

    fun removeExercise(exerciseLogId: Long) {
        viewModelScope.launch {
            workoutRepository.removeExerciseFromSession(exerciseLogId)
        }
    }

    fun addSet(exerciseLogId: Long) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current is ActiveWorkoutUiState.Success) {
                val log = current.exerciseLogs.find { it.id == exerciseLogId }
                val nextSetNumber = (log?.sets?.size ?: 0) + 1
                val lastSet = log?.sets?.lastOrNull()
                workoutRepository.addSet(
                    exerciseLogId,
                    WorkoutSet(
                        setNumber = nextSetNumber,
                        weight = lastSet?.weight ?: 0.0,
                        reps = lastSet?.reps ?: 0,
                        restTimerSeconds = current.defaultRestTimer
                    )
                )
            }
        }
    }

    fun updateSet(set: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(set)
        }
    }

    fun completeSet(set: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(set.copy(isCompleted = true))
            val current = _uiState.value
            if (current is ActiveWorkoutUiState.Success) {
                startRestTimer(set.restTimerSeconds)
            }
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteSet(setId)
        }
    }

    fun completeWorkout(onComplete: () -> Unit) {
        viewModelScope.launch {
            workoutRepository.completeSession(sessionId)
            timerJob?.cancel()
            restTimerJob?.cancel()
            onComplete()
        }
    }

    fun cancelWorkout(onCancel: () -> Unit) {
        viewModelScope.launch {
            workoutRepository.deleteSession(sessionId)
            timerJob?.cancel()
            restTimerJob?.cancel()
            onCancel()
        }
    }

    fun updateNotes(notes: String) {
        viewModelScope.launch {
            workoutRepository.updateSessionNotes(sessionId, notes)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        restTimerJob?.cancel()
    }
}
