package com.solostackdev.volumelift.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solostackdev.volumelift.data.local.entity.MuscleGroup
import com.solostackdev.volumelift.domain.model.ThemeMode
import com.solostackdev.volumelift.domain.model.UserPreferences
import com.solostackdev.volumelift.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data class Success(val preferences: UserPreferences) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            preferencesRepository.userPreferences.collect { prefs ->
                _uiState.value = SettingsUiState.Success(prefs)
            }
        }
    }

    fun toggleUnit() {
        viewModelScope.launch {
            val current = (_uiState.value as? SettingsUiState.Success)?.preferences ?: return@launch
            preferencesRepository.updateUseKg(!current.useKg)
        }
    }

    fun updateRestTimer(seconds: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultRestTimer(seconds)
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(mode)
        }
    }

    fun updateVolumeTarget(muscleGroup: MuscleGroup, target: Double) {
        viewModelScope.launch {
            preferencesRepository.updateVolumeTarget(muscleGroup, target)
        }
    }
}
