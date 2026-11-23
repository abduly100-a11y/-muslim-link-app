package com.mosque.community.data.remote

import com.mosque.community.data.remote.dto.EventDto
import com.mosque.community.data.remote.dto.MosqueDto
import com.mosque.community.data.remote.dto.SpeakerDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseService(
    private val client: SupabaseClient = SupabaseClientProvider.getClient()
) {
    suspend fun getMosques(): List<MosqueDto> = withContext(Dispatchers.IO) {
        try {
            client.from("mosques").select().decodeList<MosqueDto>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSpeakers(): List<SpeakerDto> = withContext(Dispatchers.IO) {
        try {
            client.from("speakers").select().decodeList<SpeakerDto>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getEvents(): List<EventDto> = withContext(Dispatchers.IO) {
        try {
            client.from("events").select().decodeList<EventDto>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createEvent(event: EventDto): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("events").insert(event)
            true
        } catch (e: Exception) {
            false
        }
    }
}
