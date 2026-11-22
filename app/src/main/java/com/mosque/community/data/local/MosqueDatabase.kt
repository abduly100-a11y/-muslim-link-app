package com.mosque.community.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mosque.community.data.local.dao.EventDao
import com.mosque.community.data.local.dao.MosqueDao
import com.mosque.community.data.local.dao.SpeakerDao
import com.mosque.community.data.local.entity.EventEntity
import com.mosque.community.data.local.entity.MosqueEntity
import com.mosque.community.data.local.entity.SpeakerEntity

@Database(
    entities = [
        MosqueEntity::class,
        EventEntity::class,
        SpeakerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MosqueDatabase : RoomDatabase() {
    abstract fun mosqueDao(): MosqueDao
    abstract fun eventDao(): EventDao
    abstract fun speakerDao(): SpeakerDao

    companion object {
        @Volatile
        private var INSTANCE: MosqueDatabase? = null

        fun getDatabase(context: Context): MosqueDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MosqueDatabase::class.java,
                    "muslim_link_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
