package com.example.volumelift.domain.repository

import com.example.volumelift.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateUseKg(useKg: Boolean)
    suspend fun updateDefaultRestTimer(seconds: Int)
    suspend fun updateThemeMode(themeMode: com.example.volumelift.domain.model.ThemeMode)
    suspend fun updateVolumeTarget(muscleGroup: com.example.volumelift.data.local.entity.MuscleGroup, target: Double)
    suspend fun getUserPreferences(): UserPreferences
}
