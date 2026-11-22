package com.mosque.community.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mosque.community.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY timestampEpochMillis ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE mosqueId = :mosqueId ORDER BY timestampEpochMillis ASC")
    fun getEventsByMosque(mosqueId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Update
    suspend fun update(event: EventEntity)

    @Query("UPDATE events SET rsvpState = :rsvpState, isBookmarked = :isBookmarked, attendeeCount = :attendeeCount WHERE id = :id")
    suspend fun updateRsvp(id: String, rsvpState: String, isBookmarked: Boolean, attendeeCount: Int)

    @Query("UPDATE events SET reminderEnabled = :reminderEnabled WHERE id = :id")
    suspend fun updateReminder(id: String, reminderEnabled: Boolean)

    @Query("UPDATE events SET isMosqueFollowed = :isFollowed WHERE mosqueId = :mosqueId")
    suspend fun updateMosqueFollowStatus(mosqueId: String, isFollowed: Boolean)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getCount(): Int
}
