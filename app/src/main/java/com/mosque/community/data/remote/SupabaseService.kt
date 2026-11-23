package com.mosque.community.data.remote

import com.mosque.community.data.remote.dto.EventDto
import com.mosque.community.data.remote.dto.MosqueDto
import com.mosque.community.data.remote.dto.SpeakerDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseService {
    private val client: SupabaseClient? by lazy {
        try {
            SupabaseClientProvider.getClient()
        } catch (e: Throwable) {
            null
        }
    }

    suspend fun getMosques(): List<MosqueDto> = withContext(Dispatchers.IO) {
        try {
            val c = client ?: return@withContext emptyList()
            c.from("mosques").select().decodeList<MosqueDto>()
        } catch (e: Throwable) {
            emptyList()
        }
    }

    suspend fun getSpeakers(): List<SpeakerDto> = withContext(Dispatchers.IO) {
        try {
            val c = client ?: return@withContext emptyList()
            c.from("speakers").select().decodeList<SpeakerDto>()
        } catch (e: Throwable) {
            emptyList()
        }
    }

    suspend fun getEvents(): List<EventDto> = withContext(Dispatchers.IO) {
        try {
            val c = client ?: return@withContext emptyList()
            c.from("events").select().decodeList<EventDto>()
        } catch (e: Throwable) {
            emptyList()
        }
    }

    suspend fun createEvent(event: EventDto): Boolean = withContext(Dispatchers.IO) {
        try {
            val c = client ?: return@withContext false
            c.from("events").insert(event)
            true
        } catch (e: Throwable) {
            false
        }
    }
}
