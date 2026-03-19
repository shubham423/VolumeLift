package com.solostackdev.volumelift.presentation.volume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solostackdev.volumelift.domain.model.MuscleVolume
import com.solostackdev.volumelift.domain.model.UserPreferences
import com.solostackdev.volumelift.domain.repository.UserPreferencesRepository
import com.solostackdev.volumelift.domain.repository.VolumeRepository
import com.solostackdev.volumelift.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VolumeUiState {
    data object Loading : VolumeUiState()
    data class Success(
        val muscleVolumes: List<MuscleVolume> = emptyList(),
        val weekOffset: Int = 0,
        val weekLabel: String = "",
        val preferences: UserPreferences = UserPreferences()
    ) : VolumeUiState()
    data class Error(val message: String) : VolumeUiState()
}

@HiltViewModel
class VolumeViewModel @Inject constructor(
    private val volumeRepository: VolumeRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VolumeUiState>(VolumeUiState.Loading)
    val uiState: StateFlow<VolumeUiState> = _uiState.asStateFlow()

    private var currentWeekOffset = 0

    init {
        loadVolume()
    }

    private fun loadVolume() {
        viewModelScope.launch {
            combine(
                volumeRepository.getWeeklyVolume(currentWeekOffset),
                preferencesRepository.userPreferences
            ) { volumes, prefs ->
                VolumeUiState.Success(
                    muscleVolumes = volumes,
                    weekOffset = currentWeekOffset,
                    weekLabel = DateUtils.formatWeekRange(currentWeekOffset),
                    preferences = prefs
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun previousWeek() {
        currentWeekOffset--
        loadVolume()
    }

    fun nextWeek() {
        if (currentWeekOffset < 0) {
            currentWeekOffset++
            loadVolume()
        }
    }
}
