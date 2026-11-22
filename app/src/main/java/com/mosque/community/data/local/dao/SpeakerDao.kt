package com.mosque.community.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mosque.community.data.local.entity.SpeakerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeakerDao {
    @Query("SELECT * FROM speakers ORDER BY name ASC")
    fun getAllSpeakers(): Flow<List<SpeakerEntity>>

    @Query("SELECT * FROM speakers WHERE id = :id LIMIT 1")
    suspend fun getSpeakerById(id: String): SpeakerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(speakers: List<SpeakerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(speaker: SpeakerEntity)

    @Update
    suspend fun update(speaker: SpeakerEntity)

    @Query("UPDATE speakers SET isFollowed = :isFollowed, followerCount = :followerCount WHERE id = :id")
    suspend fun updateFollowStatus(id: String, isFollowed: Boolean, followerCount: Int)

    @Query("SELECT COUNT(*) FROM speakers")
    suspend fun getCount(): Int
}
