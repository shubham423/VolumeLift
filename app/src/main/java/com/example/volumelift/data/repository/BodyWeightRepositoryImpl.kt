package com.example.volumelift.data.repository

import com.example.volumelift.data.local.dao.BodyWeightDao
import com.example.volumelift.data.mapper.toDomain
import com.example.volumelift.data.mapper.toEntity
import com.example.volumelift.domain.model.BodyWeightEntry
import com.example.volumelift.domain.repository.BodyWeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BodyWeightRepositoryImpl @Inject constructor(
    private val bodyWeightDao: BodyWeightDao
) : BodyWeightRepository {

    override fun getAllEntries(): Flow<List<BodyWeightEntry>> =
        bodyWeightDao.getAllEntries().map { list -> list.map { it.toDomain() } }

    override fun getEntriesInRange(startDate: Long, endDate: Long): Flow<List<BodyWeightEntry>> =
        bodyWeightDao.getEntriesInRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override suspend fun getLatestEntry(): BodyWeightEntry? =
        bodyWeightDao.getLatestEntry()?.toDomain()

    override suspend fun insertEntry(entry: BodyWeightEntry): Long =
        bodyWeightDao.insertEntry(entry.toEntity())

    override suspend fun deleteEntry(entry: BodyWeightEntry) =
        bodyWeightDao.deleteEntry(entry.toEntity())
}
