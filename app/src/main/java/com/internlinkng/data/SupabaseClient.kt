package com.internlinkng.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://avyzygqjtbdnfcgirirk.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF2eXp5Z3FqdGJkbmZjZ2lyaXJrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTQxMDUxNTMsImV4cCI6MjA2OTY4MTE1M30.cySRlJSFnDR1A-5crUnfa8XqL-KasQBTPN08UlFIY7g"
    
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Storage)
    }
    
    val auth get() = supabaseClient.auth
    val database get() = supabaseClient.postgrest
    val storage get() = supabaseClient.storage
} 