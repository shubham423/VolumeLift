package com.solostackdev.volumelift.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solostackdev.volumelift.data.local.entity.MuscleGroup
import com.solostackdev.volumelift.domain.model.WorkoutSession
import com.solostackdev.volumelift.domain.model.WorkoutTemplate
import com.solostackdev.volumelift.domain.repository.ExerciseRepository
import com.solostackdev.volumelift.domain.repository.TemplateRepository
import com.solostackdev.volumelift.domain.repository.WorkoutRepository
import com.solostackdev.volumelift.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val activeSession: WorkoutSession? = null,
        val recentWorkouts: List<WorkoutSession> = emptyList(),
        val templates: List<WorkoutTemplate> = emptyList(),
        val weekWorkoutCount: Int = 0,
        val weekTotalVolume: Double = 0.0,
        val sessionMuscleGroups: Map<Long, List<MuscleGroup>> = emptyMap()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val templateRepository: TemplateRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                // Initialize Success state first, then start Flow collectors
                val activeSession = workoutRepository.getActiveSession()
                _uiState.value = HomeUiState.Success(activeSession = activeSession)

                // Now launch Flow collectors — state is guaranteed to be Success
                launch {
                    workoutRepository.getCompletedSessions().collect { sessions ->
                        val exerciseMap = exerciseRepository.getAllExercises().first()
                            .associateBy { it.id }
                        val recentFull = sessions.take(3).map { session ->
                            workoutRepository.getFullSession(session.id) ?: session
                        }
                        val muscleGroupsMap = recentFull.associate { session ->
                            session.id to session.exerciseLogs
                                .mapNotNull { log -> exerciseMap[log.exerciseId]?.primaryMuscleGroup }
                                .distinct()
                        }
                        _uiState.update { current ->
                            if (current is HomeUiState.Success) current.copy(
                                recentWorkouts = recentFull,
                                sessionMuscleGroups = muscleGroupsMap
                            ) else current
                        }
                    }
                }

                launch {
                    templateRepository.getAllTemplates().collect { templates ->
                        _uiState.update { current ->
                            if (current is HomeUiState.Success) current.copy(templates = templates)
                            else current
                        }
                    }
                }

                launch {
                    val (weekStart, weekEnd) = DateUtils.getWeekStartEnd(0)
                    workoutRepository.getSessionCountInRange(weekStart, weekEnd).collect { count ->
                        _uiState.update { current ->
                            if (current is HomeUiState.Success) current.copy(weekWorkoutCount = count)
                            else current
                        }
                    }
                }

                launch {
                    val (weekStart, weekEnd) = DateUtils.getWeekStartEnd(0)
                    workoutRepository.getSessionsInRange(weekStart, weekEnd).collect { sessions ->
                        var totalVol = 0.0
                        for (session in sessions) {
                            val full = workoutRepository.getFullSession(session.id)
                            if (full != null) {
                                for (log in full.exerciseLogs) {
                                    for (set in log.sets) {
                                        if (set.isCompleted) {
                                            totalVol += set.weight * set.reps
                                        }
                                    }
                                }
                            }
                        }
                        _uiState.update { current ->
                            if (current is HomeUiState.Success) current.copy(weekTotalVolume = totalVol)
                            else current
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val activeSession = workoutRepository.getActiveSession()
            _uiState.update { current ->
                if (current is HomeUiState.Success) current.copy(activeSession = activeSession)
                else current
            }
        }
    }

    fun startEmptyWorkout(onSessionCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val sessionId = workoutRepository.startSession()
            onSessionCreated(sessionId)
        }
    }

    fun startFromTemplate(templateId: Long, onSessionCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val template = templateRepository.getTemplateById(templateId) ?: return@launch
            val sessionId = workoutRepository.startSession(templateId)
            for (exerciseId in template.exerciseIds) {
                val logId = workoutRepository.addExerciseToSession(sessionId, exerciseId)
                // Pre-fill sets from previous session
                val prevSets = workoutRepository.getPreviousSetsForExercise(exerciseId, sessionId)
                if (prevSets.isNotEmpty()) {
                    for (prevSet in prevSets) {
                        workoutRepository.addSet(logId, com.solostackdev.volumelift.domain.model.WorkoutSet(
                            setNumber = prevSet.setNumber,
                            weight = prevSet.weight,
                            reps = prevSet.reps
                        ))
                    }
                } else {
                    // Default: 3 empty sets
                    for (i in 1..3) {
                        workoutRepository.addSet(logId, com.solostackdev.volumelift.domain.model.WorkoutSet(setNumber = i))
                    }
                }
            }
            onSessionCreated(sessionId)
        }
    }
}
