package com.example.volumelift.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data class Success(val sessions: List<WorkoutSession> = emptyList()) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            workoutRepository.getCompletedSessions().collect { sessions ->
                _uiState.value = HistoryUiState.Success(sessions = sessions)
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
}
