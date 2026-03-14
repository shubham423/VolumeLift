package com.example.volumelift.data.repository

import com.example.volumelift.data.local.dao.VolumeDao
import com.example.volumelift.data.local.db.Converters
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.MuscleVolume
import com.example.volumelift.domain.repository.UserPreferencesRepository
import com.example.volumelift.domain.repository.VolumeRepository
import com.example.volumelift.util.Constants
import com.example.volumelift.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VolumeRepositoryImpl @Inject constructor(
    private val volumeDao: VolumeDao,
    private val preferencesRepository: UserPreferencesRepository
) : VolumeRepository {

    override fun getWeeklyVolume(weekOffset: Int): Flow<List<MuscleVolume>> {
        val (currentStart, currentEnd) = DateUtils.getWeekStartEnd(weekOffset)
        val (prevStart, prevEnd) = DateUtils.getWeekStartEnd(weekOffset - 1)

        val currentWeekFlow = volumeDao.getVolumeByExerciseInRange(currentStart, currentEnd)
        val prevWeekFlow = volumeDao.getVolumeByExerciseInRange(prevStart, prevEnd)

        return combine(currentWeekFlow, prevWeekFlow, preferencesRepository.userPreferences) { currentResults, prevResults, prefs ->
            val currentVolumeMap = calculateMuscleVolumes(currentResults)
            val prevVolumeMap = calculateMuscleVolumes(prevResults)

            MuscleGroup.entries.map { muscleGroup ->
                MuscleVolume(
                    muscleGroup = muscleGroup,
                    currentVolume = currentVolumeMap[muscleGroup] ?: 0.0,
                    targetVolume = prefs.volumeTargets[muscleGroup] ?: 10000.0,
                    previousWeekVolume = prevVolumeMap[muscleGroup] ?: 0.0
                )
            }
        }
    }

    override fun getWeeklyVolumeHistory(weeks: Int): Flow<List<List<MuscleVolume>>> = flow {
        // Simplified: emit current week data for chart
        // In production you'd collect multiple weeks
    }

    private fun calculateMuscleVolumes(
        results: List<com.example.volumelift.data.local.dao.ExerciseVolumeResult>
    ): Map<MuscleGroup, Double> {
        val volumeMap = mutableMapOf<MuscleGroup, Double>()
        val converters = Converters()

        for (result in results) {
            val primary = MuscleGroup.valueOf(result.primaryMuscleGroup)
            val secondaries = converters.toMuscleGroupList(result.secondaryMuscleGroups)

            // Primary gets 100% volume
            volumeMap[primary] = (volumeMap[primary] ?: 0.0) +
                    (result.totalVolume * Constants.PRIMARY_MUSCLE_VOLUME_FACTOR)

            // Secondary muscles get 50% volume
            for (secondary in secondaries) {
                volumeMap[secondary] = (volumeMap[secondary] ?: 0.0) +
                        (result.totalVolume * Constants.SECONDARY_MUSCLE_VOLUME_FACTOR)
            }
        }

        return volumeMap
    }
}
