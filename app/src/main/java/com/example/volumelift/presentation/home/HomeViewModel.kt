package com.example.volumelift.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutTemplate
import com.example.volumelift.domain.repository.TemplateRepository
import com.example.volumelift.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val activeSession: WorkoutSession? = null,
        val recentWorkouts: List<WorkoutSession> = emptyList(),
        val templates: List<WorkoutTemplate> = emptyList()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                val activeSession = workoutRepository.getActiveSession()
                _uiState.value = HomeUiState.Success(activeSession = activeSession)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }

        viewModelScope.launch {
            workoutRepository.getCompletedSessions().collect { sessions ->
                val current = _uiState.value
                if (current is HomeUiState.Success) {
                    _uiState.value = current.copy(recentWorkouts = sessions.take(5))
                }
            }
        }

        viewModelScope.launch {
            templateRepository.getAllTemplates().collect { templates ->
                val current = _uiState.value
                if (current is HomeUiState.Success) {
                    _uiState.value = current.copy(templates = templates)
                }
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
                workoutRepository.addExerciseToSession(sessionId, exerciseId)
            }
            onSessionCreated(sessionId)
        }
    }
}
