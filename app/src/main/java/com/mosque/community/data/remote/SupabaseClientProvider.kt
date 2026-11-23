package com.mosque.community.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClientProvider {
    // These should ideally be stored in BuildConfig or secure configuration
    private const val DEFAULT_SUPABASE_URL = "https://mkzpoghwagjecghmygsa.supabase.co"
    private const val DEFAULT_SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1renBvZ2h3YWdqZWNnaG15Z3NhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTUzOTc2OTcsImV4cCI6MjExMDk3MzY5N30.CqRXPCE4J-4c0dvLZo4_ErsQUCVtXZ_o8ItEujY-k8o"

    @Volatile
    private var client: SupabaseClient? = null

    fun getClient(
        url: String = DEFAULT_SUPABASE_URL,
        anonKey: String = DEFAULT_SUPABASE_ANON_KEY
    ): SupabaseClient {
        return client ?: synchronized(this) {
            client ?: createSupabaseClient(
                supabaseUrl = url,
                supabaseKey = anonKey
            ) {
                httpEngine = OkHttp.create()
                install(Postgrest)
            }.also { client = it }
        }
    }
}
