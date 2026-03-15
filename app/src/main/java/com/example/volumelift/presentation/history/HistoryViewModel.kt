package com.example.volumelift.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.repository.ExerciseRepository
import com.example.volumelift.domain.repository.WorkoutRepository
import com.example.volumelift.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HistoryWorkoutItem(
    val session: WorkoutSession,
    val workoutName: String,
    val durationText: String,
    val totalVolume: Double,
    val exerciseCount: Int,
    val muscleGroups: List<MuscleGroup>
)

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data class Success(
        val groupedWorkouts: List<Pair<String, List<HistoryWorkoutItem>>> = emptyList(),
        val weekWorkoutCount: Int = 0,
        val weekTotalDuration: String = "",
        val weekTotalVolume: Double = 0.0,
        val weekDays: List<LocalDate> = emptyList(),
        val workoutDayIndices: Set<Int> = emptySet()
    ) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            workoutRepository.getCompletedSessions().collect { sessions ->
                val exerciseMap = exerciseRepository.getAllExercises().first()
                    .associateBy { it.id }

                val fullSessions = sessions.map { session ->
                    workoutRepository.getFullSession(session.id) ?: session
                }

                val workoutItems = fullSessions.map { session ->
                    val exerciseCount = session.exerciseLogs.size
                    val muscleGroups = session.exerciseLogs
                        .mapNotNull { log -> exerciseMap[log.exerciseId]?.primaryMuscleGroup }
                        .distinct()
                    val totalVolume = session.exerciseLogs
                        .flatMap { it.sets }
                        .filter { it.isCompleted }
                        .sumOf { it.weight * it.reps }
                    val durationText = DateUtils.formatDurationShort(session.startTime, session.endTime)
                    val workoutName = deriveWorkoutName(session)

                    HistoryWorkoutItem(
                        session = session,
                        workoutName = workoutName,
                        durationText = durationText,
                        totalVolume = totalVolume,
                        exerciseCount = exerciseCount,
                        muscleGroups = muscleGroups
                    )
                }

                // Group by date header
                val grouped = workoutItems.groupBy { item ->
                    DateUtils.formatDateHeader(item.session.startTime)
                }.toList()

                // Weekly stats
                val (weekStart, weekEnd) = DateUtils.getWeekStartEnd(0)
                val weekItems = workoutItems.filter {
                    it.session.startTime in weekStart..weekEnd
                }
                val weekWorkoutCount = weekItems.size
                val weekTotalVol = weekItems.sumOf { it.totalVolume }
                val weekTotalMs = weekItems.sumOf {
                    (it.session.endTime ?: System.currentTimeMillis()) - it.session.startTime
                }
                val weekTotalMinutes = (weekTotalMs / 1000 / 60).toInt()
                val weekTotalDuration = if (weekTotalMinutes >= 60) {
                    "${weekTotalMinutes / 60}h ${weekTotalMinutes % 60}m"
                } else {
                    "$weekTotalMinutes min"
                }

                // Day dots
                val weekDays = DateUtils.getWeekDays(0)
                val workoutDayIndices = weekItems.map { item ->
                    val date = Instant.ofEpochMilli(item.session.startTime)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    weekDays.indexOf(date)
                }.filter { it >= 0 }.toSet()

                _uiState.value = HistoryUiState.Success(
                    groupedWorkouts = grouped,
                    weekWorkoutCount = weekWorkoutCount,
                    weekTotalDuration = weekTotalDuration,
                    weekTotalVolume = weekTotalVol,
                    weekDays = weekDays,
                    workoutDayIndices = workoutDayIndices
                )
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteSession(sessionId)
        }
    }

    fun duplicateWorkout(sessionId: Long, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val original = workoutRepository.getFullSession(sessionId) ?: return@launch
            val newSessionId = workoutRepository.startSession(original.templateId)
            for (log in original.exerciseLogs) {
                workoutRepository.addExerciseToSession(newSessionId, log.exerciseId)
            }
            onCreated(newSessionId)
        }
    }

    private fun deriveWorkoutName(session: WorkoutSession): String {
        if (session.notes.isNotBlank()) return session.notes
        if (session.exerciseLogs.isEmpty()) return "Workout"
        val firstName = session.exerciseLogs.firstOrNull()?.exerciseName ?: ""
        val count = session.exerciseLogs.size
        return if (count <= 1) firstName.ifBlank { "Workout" }
        else "$firstName + ${count - 1} more"
    }
}
