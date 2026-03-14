package com.example.volumelift.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volumelift.domain.model.BodyWeightEntry
import com.example.volumelift.domain.model.WorkoutSet
import com.example.volumelift.domain.repository.BodyWeightRepository
import com.example.volumelift.domain.repository.ProgressRepository
import com.example.volumelift.domain.repository.WorkoutRepository
import com.example.volumelift.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProgressUiState {
    data object Loading : ProgressUiState()
    data class Success(
        val workoutsThisWeek: Int = 0,
        val bodyWeightEntries: List<BodyWeightEntry> = emptyList(),
        val latestBodyWeight: BodyWeightEntry? = null
    ) : ProgressUiState()
    data class Error(val message: String) : ProgressUiState()
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val bodyWeightRepository: BodyWeightRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    private fun loadProgress() {
        val (weekStart, weekEnd) = DateUtils.getWeekStartEnd()

        viewModelScope.launch {
            workoutRepository.getSessionCountInRange(weekStart, weekEnd).collect { count ->
                val current = _uiState.value
                if (current is ProgressUiState.Success) {
                    _uiState.value = current.copy(workoutsThisWeek = count)
                } else {
                    _uiState.value = ProgressUiState.Success(workoutsThisWeek = count)
                }
            }
        }

        viewModelScope.launch {
            bodyWeightRepository.getAllEntries().collect { entries ->
                val current = _uiState.value
                if (current is ProgressUiState.Success) {
                    _uiState.value = current.copy(
                        bodyWeightEntries = entries,
                        latestBodyWeight = entries.firstOrNull()
                    )
                } else {
                    _uiState.value = ProgressUiState.Success(
                        bodyWeightEntries = entries,
                        latestBodyWeight = entries.firstOrNull()
                    )
                }
            }
        }
    }

    fun addBodyWeight(weight: Double) {
        viewModelScope.launch {
            bodyWeightRepository.insertEntry(
                BodyWeightEntry(weight = weight, date = System.currentTimeMillis())
            )
        }
    }

    fun deleteBodyWeight(entry: BodyWeightEntry) {
        viewModelScope.launch {
            bodyWeightRepository.deleteEntry(entry)
        }
    }
}
