package com.chittortech.snp.data

import com.chittortech.snp.data.models.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object SupabaseManager {

    private const val SUPABASE_URL = "https://qvrhgytkufajcpletbfp.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InF2cmhneXRrdWZhamNwbGV0YmZwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI0NjkyMzYsImV4cCI6MjEwODA0NTIzNn0.x79eSX3uJdV2PSpV4zBLa6xuYaGaQZrs8IDk0YA0ymU"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Realtime)
    }

    suspend fun getTickets(): List<Ticket> {
        return try {
            client.postgrest["tickets"]
                .select()
                .decodeList<Ticket>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDepartments(): List<Department> {
        return try {
            val list = client.postgrest["departments"]
                .select()
                .decodeList<Department>()
            // Only keep official Chittorgarh municipal departments, filtering out junk test entries
            list.filter { dept ->
                val code = dept.code.trim().uppercase()
                val name = dept.name.trim()
                val officer = dept.headOfficerName.orEmpty()
                !name.contains("hh", ignoreCase = true) &&
                !name.contains("ccc", ignoreCase = true) &&
                !name.contains("[[") &&
                !name.contains("''") &&
                !officer.contains("[[") &&
                name.length >= 3 &&
                code != "HH" && code != "CC" && code != "'"
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWards(): List<Ward> {
        return try {
            client.postgrest["wards"]
                .select()
                .decodeList<Ward>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserProfileByEmail(email: String): UserProfile? {
        return try {
            client.postgrest["user_profiles"]
                .select {
                    filter {
                        eq("email", email.trim().lowercase())
                    }
                }
                .decodeSingleOrNull<UserProfile>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getNotifications(): List<NotificationLog> {
        return try {
            client.postgrest["notification_logs"]
                .select()
                .decodeList<NotificationLog>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun subscribeToTicketUpdates(): Flow<PostgresAction> = flow {
        try {
            val channel = client.realtime.channel("public:tickets")
            val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "tickets"
            }
            channel.subscribe()
            changeFlow.collect { emit(it) }
        } catch (e: Exception) {
            // Log/handle fallback
        }
    }
}

