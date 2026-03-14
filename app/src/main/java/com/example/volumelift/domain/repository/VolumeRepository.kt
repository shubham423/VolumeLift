package com.example.volumelift.domain.repository

import com.example.volumelift.domain.model.MuscleVolume
import kotlinx.coroutines.flow.Flow

interface VolumeRepository {
    fun getWeeklyVolume(weekOffset: Int = 0): Flow<List<MuscleVolume>>
    fun getWeeklyVolumeHistory(weeks: Int = 8): Flow<List<List<MuscleVolume>>>
}
