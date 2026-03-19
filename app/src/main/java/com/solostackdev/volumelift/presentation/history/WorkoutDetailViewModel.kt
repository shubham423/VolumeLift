package com.solostackdev.volumelift.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solostackdev.volumelift.domain.model.WorkoutSession
import com.solostackdev.volumelift.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState()
    data class Success(val session: WorkoutSession) : WorkoutDetailUiState()
    data class Error(val message: String) : WorkoutDetailUiState()
}

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: -1L

    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            try {
                val session = workoutRepository.getFullSession(sessionId)
                if (session != null) {
                    _uiState.value = WorkoutDetailUiState.Success(session)
                } else {
                    _uiState.value = WorkoutDetailUiState.Error("Session not found")
                }
            } catch (e: Exception) {
                _uiState.value = WorkoutDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
