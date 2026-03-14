package com.example.volumelift.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.ThemeMode
import com.example.volumelift.domain.model.UserPreferences
import com.example.volumelift.domain.model.defaultVolumeTargets
import com.example.volumelift.domain.repository.UserPreferencesRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val gson = Gson()

    private object PrefsKeys {
        val USE_KG = booleanPreferencesKey("use_kg")
        val DEFAULT_REST_TIMER = intPreferencesKey("default_rest_timer")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val VOLUME_TARGETS = stringPreferencesKey("volume_targets")
    }

    override val userPreferences: Flow<UserPreferences> =
        context.dataStore.data.map { prefs ->
            val volumeTargetsJson = prefs[PrefsKeys.VOLUME_TARGETS]
            val volumeTargets = if (volumeTargetsJson != null) {
                val type = object : TypeToken<Map<String, Double>>() {}.type
                val map: Map<String, Double> = gson.fromJson(volumeTargetsJson, type)
                map.mapKeys { MuscleGroup.valueOf(it.key) }
            } else {
                defaultVolumeTargets()
            }

            UserPreferences(
                useKg = prefs[PrefsKeys.USE_KG] ?: true,
                defaultRestTimerSeconds = prefs[PrefsKeys.DEFAULT_REST_TIMER] ?: 90,
                themeMode = prefs[PrefsKeys.THEME_MODE]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.System,
                volumeTargets = volumeTargets
            )
        }

    override suspend fun updateUseKg(useKg: Boolean) {
        context.dataStore.edit { it[PrefsKeys.USE_KG] = useKg }
    }

    override suspend fun updateDefaultRestTimer(seconds: Int) {
        context.dataStore.edit { it[PrefsKeys.DEFAULT_REST_TIMER] = seconds }
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { it[PrefsKeys.THEME_MODE] = themeMode.name }
    }

    override suspend fun updateVolumeTarget(muscleGroup: MuscleGroup, target: Double) {
        context.dataStore.edit { prefs ->
            val current = prefs[PrefsKeys.VOLUME_TARGETS]?.let {
                val type = object : TypeToken<Map<String, Double>>() {}.type
                gson.fromJson<Map<String, Double>>(it, type).toMutableMap()
            } ?: defaultVolumeTargets().mapKeys { it.key.name }.toMutableMap()
            current[muscleGroup.name] = target
            prefs[PrefsKeys.VOLUME_TARGETS] = gson.toJson(current)
        }
    }

    override suspend fun getUserPreferences(): UserPreferences =
        userPreferences.first()
}
