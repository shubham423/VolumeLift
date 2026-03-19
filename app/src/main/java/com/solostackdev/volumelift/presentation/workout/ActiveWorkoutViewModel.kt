package com.solostackdev.volumelift.presentation.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solostackdev.volumelift.domain.model.ExerciseLogWithSets
import com.solostackdev.volumelift.domain.model.WorkoutSession
import com.solostackdev.volumelift.domain.model.WorkoutSet
import com.solostackdev.volumelift.domain.repository.UserPreferencesRepository
import com.solostackdev.volumelift.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VolumeWhisper(
    val text: String,
    val id: Long // unique id for recomposition
)

sealed class ActiveWorkoutUiState {
    data object Loading : ActiveWorkoutUiState()
    data class Success(
        val session: WorkoutSession,
        val exerciseLogs: List<ExerciseLogWithSets> = emptyList(),
        val elapsedTime: String = "00:00",
        val restTimerSeconds: Int = 0,
        val isRestTimerRunning: Boolean = false,
        val defaultRestTimer: Int = 90,
        val previousSets: Map<Long, List<WorkoutSet>> = emptyMap(),
        val volumeWhisper: VolumeWhisper? = null
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

    private val _restTimerCompleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val restTimerCompleted = _restTimerCompleted

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null
    private var defaultRestTimer: Int = 90

    init {
        loadSession()
        startTimer()
    }

    private fun loadSession() {
        viewModelScope.launch {
            try {
                val prefs = preferencesRepository.getUserPreferences()
                defaultRestTimer = prefs.defaultRestTimerSeconds
                refreshData()
            } catch (e: Exception) {
                _uiState.value = ActiveWorkoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun refreshData() {
        val fullSession = workoutRepository.getFullSession(sessionId) ?: return
        val current = _uiState.value

        // Load previous sets for each exercise
        val prevSets = mutableMapOf<Long, List<WorkoutSet>>()
        for (log in fullSession.exerciseLogs) {
            if (!prevSets.containsKey(log.exerciseId)) {
                val prev = workoutRepository.getPreviousSetsForExercise(log.exerciseId, sessionId)
                if (prev.isNotEmpty()) {
                    prevSets[log.exerciseId] = prev
                }
            }
        }

        val elapsed = if (current is ActiveWorkoutUiState.Success) current.elapsedTime else "00:00"
        val restTimer = if (current is ActiveWorkoutUiState.Success) current.restTimerSeconds else 0
        val isRunning = if (current is ActiveWorkoutUiState.Success) current.isRestTimerRunning else false
        val whisper = if (current is ActiveWorkoutUiState.Success) current.volumeWhisper else null
        _uiState.value = ActiveWorkoutUiState.Success(
            session = fullSession,
            exerciseLogs = fullSession.exerciseLogs,
            elapsedTime = elapsed,
            restTimerSeconds = restTimer,
            isRestTimerRunning = isRunning,
            defaultRestTimer = defaultRestTimer,
            previousSets = prevSets,
            volumeWhisper = whisper
        )
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
            _restTimerCompleted.tryEmit(Unit)
        }
    }

    fun adjustRestTimer(delta: Int) {
        val current = _uiState.value
        if (current is ActiveWorkoutUiState.Success && current.isRestTimerRunning) {
            val newSeconds = (current.restTimerSeconds + delta).coerceAtLeast(0)
            if (newSeconds == 0) {
                cancelRestTimer()
            } else {
                // Restart the timer with the adjusted value
                restTimerJob?.cancel()
                startRestTimer(newSeconds)
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
            val exerciseLogId = workoutRepository.addExerciseToSession(sessionId, exerciseId)
            // Auto-create sets from previous session
            val prevSets = workoutRepository.getPreviousSetsForExercise(exerciseId, sessionId)
            if (prevSets.isNotEmpty()) {
                for (prevSet in prevSets) {
                    workoutRepository.addSet(
                        exerciseLogId,
                        WorkoutSet(
                            setNumber = prevSet.setNumber,
                            weight = prevSet.weight,
                            reps = prevSet.reps,
                            restTimerSeconds = defaultRestTimer
                        )
                    )
                }
            } else {
                // No previous data — add one empty set
                workoutRepository.addSet(
                    exerciseLogId,
                    WorkoutSet(setNumber = 1, restTimerSeconds = defaultRestTimer)
                )
            }
            refreshData()
        }
    }

    fun removeExercise(exerciseLogId: Long) {
        viewModelScope.launch {
            workoutRepository.removeExerciseFromSession(exerciseLogId)
            refreshData()
        }
    }

    fun addSet(exerciseLogId: Long) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current is ActiveWorkoutUiState.Success) {
                val log = current.exerciseLogs.find { it.id == exerciseLogId }
                val nextSetNumber = (log?.sets?.size ?: 0) + 1
                val lastSet = log?.sets?.lastOrNull()
                // Try previous session data for this set number, fall back to last set in current session
                val prevSets = log?.let { current.previousSets[it.exerciseId] }
                val prevSetForNumber = prevSets?.find { it.setNumber == nextSetNumber }
                val prefillWeight = prevSetForNumber?.weight ?: lastSet?.weight ?: 0.0
                val prefillReps = prevSetForNumber?.reps ?: lastSet?.reps ?: 0
                workoutRepository.addSet(
                    exerciseLogId,
                    WorkoutSet(
                        setNumber = nextSetNumber,
                        weight = prefillWeight,
                        reps = prefillReps,
                        restTimerSeconds = defaultRestTimer
                    )
                )
                refreshData()
            }
        }
    }

    fun updateSet(set: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(set)
            refreshData()
        }
    }

    fun completeSet(set: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(set.copy(isCompleted = true))
            refreshData()
            val current = _uiState.value
            if (current is ActiveWorkoutUiState.Success) {
                // Volume whisper
                val volume = set.weight * set.reps
                if (volume > 0) {
                    val log = current.exerciseLogs.find { it.sets.any { s -> s.id == set.id } }
                    val muscleName = log?.primaryMuscleGroup ?: ""
                    val volText = if (volume >= 1000) String.format("%,.0f", volume)
                    else String.format("%.0f", volume)
                    val whisperText = "+$volText kg" + if (muscleName.isNotEmpty()) " $muscleName" else ""
                    _uiState.value = current.copy(
                        volumeWhisper = VolumeWhisper(whisperText, System.currentTimeMillis())
                    )
                    // Clear whisper after 2 seconds
                    launch {
                        delay(2000)
                        val c = _uiState.value
                        if (c is ActiveWorkoutUiState.Success) {
                            _uiState.value = c.copy(volumeWhisper = null)
                        }
                    }
                }
                startRestTimer(set.restTimerSeconds)
            }
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteSet(setId)
            refreshData()
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
