package com.chittortech.snp.data

import com.chittortech.snp.BuildConfig
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

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
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
            client.postgrest["departments"]
                .select()
                .decodeList<Department>()
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

