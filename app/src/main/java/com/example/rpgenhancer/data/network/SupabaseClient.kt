package com.example.rpgenhancer.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://hlplgmecifnaxjmrhdph.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhscGxnbWVjaWZuYXhqbXJoZHBoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjY2ODkxODEsImV4cCI6MjA0MjI2NTE4MX0.rdmS416Xxe-EEXe0BozMBVni-NOSoZUArlg-HYvt-2k"
    ) {
        install(Postgrest)
        install(Storage)
    }
}