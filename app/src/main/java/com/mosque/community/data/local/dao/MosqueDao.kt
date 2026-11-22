package com.mosque.community.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mosque.community.data.local.entity.MosqueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MosqueDao {
    @Query("SELECT * FROM mosques ORDER BY distanceMiles ASC")
    fun getAllMosques(): Flow<List<MosqueEntity>>

    @Query("SELECT * FROM mosques WHERE id = :id LIMIT 1")
    suspend fun getMosqueById(id: String): MosqueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mosques: List<MosqueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mosque: MosqueEntity)

    @Update
    suspend fun update(mosque: MosqueEntity)

    @Query("UPDATE mosques SET isFollowed = :isFollowed, followerCount = :followerCount WHERE id = :id")
    suspend fun updateFollowStatus(id: String, isFollowed: Boolean, followerCount: Int)

    @Query("SELECT COUNT(*) FROM mosques")
    suspend fun getCount(): Int
}
