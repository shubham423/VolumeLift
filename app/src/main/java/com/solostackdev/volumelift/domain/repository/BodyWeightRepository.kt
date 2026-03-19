package com.solostackdev.volumelift.domain.repository

import com.solostackdev.volumelift.domain.model.BodyWeightEntry
import kotlinx.coroutines.flow.Flow

interface BodyWeightRepository {
    fun getAllEntries(): Flow<List<BodyWeightEntry>>
    fun getEntriesInRange(startDate: Long, endDate: Long): Flow<List<BodyWeightEntry>>
    suspend fun getLatestEntry(): BodyWeightEntry?
    suspend fun insertEntry(entry: BodyWeightEntry): Long
    suspend fun deleteEntry(entry: BodyWeightEntry)
}
