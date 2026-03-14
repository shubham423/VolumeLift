package com.example.volumelift.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.volumelift.data.local.entity.BodyWeightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyWeightDao {
    @Query("SELECT * FROM body_weight ORDER BY date DESC")
    fun getAllEntries(): Flow<List<BodyWeightEntity>>

    @Query("SELECT * FROM body_weight ORDER BY date DESC LIMIT 1")
    suspend fun getLatestEntry(): BodyWeightEntity?

    @Query("SELECT * FROM body_weight WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getEntriesInRange(startDate: Long, endDate: Long): Flow<List<BodyWeightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BodyWeightEntity): Long

    @Delete
    suspend fun deleteEntry(entry: BodyWeightEntity)
}
