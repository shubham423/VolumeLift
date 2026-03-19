package com.solostackdev.volumelift.presentation.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solostackdev.volumelift.domain.model.WorkoutTemplate
import com.solostackdev.volumelift.domain.repository.TemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TemplatesUiState {
    data object Loading : TemplatesUiState()
    data class Success(val templates: List<WorkoutTemplate> = emptyList()) : TemplatesUiState()
    data class Error(val message: String) : TemplatesUiState()
}

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TemplatesUiState>(TemplatesUiState.Loading)
    val uiState: StateFlow<TemplatesUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            templateRepository.getAllTemplates().collect { templates ->
                _uiState.value = TemplatesUiState.Success(templates = templates)
            }
        }
    }

    fun deleteTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            templateRepository.deleteTemplate(template)
        }
    }

    fun createTemplate(name: String, exerciseIds: List<Long>, notes: String) {
        viewModelScope.launch {
            templateRepository.insertTemplate(
                WorkoutTemplate(name = name, exerciseIds = exerciseIds, notes = notes)
            )
        }
    }
}
