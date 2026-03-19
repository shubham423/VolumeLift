package com.solostackdev.volumelift.data.repository

import com.solostackdev.volumelift.data.local.dao.ExerciseVolumeResult
import com.solostackdev.volumelift.data.local.dao.VolumeDao
import com.solostackdev.volumelift.data.local.db.Converters
import com.solostackdev.volumelift.data.local.entity.MuscleGroup
import com.solostackdev.volumelift.domain.model.MuscleVolume
import com.solostackdev.volumelift.domain.repository.UserPreferencesRepository
import com.solostackdev.volumelift.domain.repository.VolumeRepository
import com.solostackdev.volumelift.util.Constants
import com.solostackdev.volumelift.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private data class MuscleStats(
    val volume: Double = 0.0,
    val sets: Int = 0
)

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
            val currentStats = calculateMuscleStats(currentResults)
            val prevStats = calculateMuscleStats(prevResults)

            MuscleGroup.entries.map { muscleGroup ->
                val current = currentStats[muscleGroup] ?: MuscleStats()
                val prev = prevStats[muscleGroup] ?: MuscleStats()
                MuscleVolume(
                    muscleGroup = muscleGroup,
                    currentSets = current.sets,
                    targetSets = prefs.setTargets[muscleGroup] ?: 14,
                    previousWeekSets = prev.sets,
                    currentVolume = current.volume,
                    targetVolume = prefs.volumeTargets[muscleGroup] ?: 10000.0,
                    previousWeekVolume = prev.volume
                )
            }
        }
    }

    override fun getWeeklyVolumeHistory(weeks: Int): Flow<List<List<MuscleVolume>>> = flow {
        // Simplified: emit current week data for chart
    }

    private fun calculateMuscleStats(
        results: List<ExerciseVolumeResult>
    ): Map<MuscleGroup, MuscleStats> {
        val statsMap = mutableMapOf<MuscleGroup, MuscleStats>()
        val converters = Converters()

        for (result in results) {
            val primary = MuscleGroup.valueOf(result.primaryMuscleGroup)
            val secondaries = converters.toMuscleGroupList(result.secondaryMuscleGroups)

            // Primary muscle: 100% volume, full set count
            val currentPrimary = statsMap[primary] ?: MuscleStats()
            statsMap[primary] = MuscleStats(
                volume = currentPrimary.volume + (result.totalVolume * Constants.PRIMARY_MUSCLE_VOLUME_FACTOR),
                sets = currentPrimary.sets + result.setCount
            )

            // Secondary muscles: 50% volume, 50% set count (rounded up)
            for (secondary in secondaries) {
                val currentSecondary = statsMap[secondary] ?: MuscleStats()
                val secondarySets = ((result.setCount * 0.5) + 0.5).toInt()
                statsMap[secondary] = MuscleStats(
                    volume = currentSecondary.volume + (result.totalVolume * Constants.SECONDARY_MUSCLE_VOLUME_FACTOR),
                    sets = currentSecondary.sets + secondarySets
                )
            }
        }

        return statsMap
    }
}
