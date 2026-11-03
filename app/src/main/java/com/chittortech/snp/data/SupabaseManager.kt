package com.chittortech.snp.data

import com.chittortech.snp.data.models.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
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
        install(Auth)
    }

    suspend fun getTickets(): List<Ticket> {
        return try {
            client.postgrest["tickets"]
                .select()
                .decodeList<Ticket>()
                .sortedByDescending { it.registrationTime ?: "" }
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

    suspend fun getCategories(): List<ComplaintCategory> {
        return try {
            client.postgrest["complaint_categories"]
                .select()
                .decodeList<ComplaintCategory>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAuthorityConfirmation(ticketId: String): AuthorityConfirmation? {
        return try {
            client.postgrest["authority_confirmations"]
                .select {
                    filter {
                        eq("ticket_id", ticketId)
                    }
                }
                .decodeSingleOrNull<AuthorityConfirmation>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCitizenConfirmation(ticketId: String): CitizenConfirmation? {
        return try {
            client.postgrest["citizen_confirmations"]
                .select {
                    filter {
                        eq("ticket_id", ticketId)
                    }
                }
                .decodeSingleOrNull<CitizenConfirmation>()
        } catch (e: Exception) {
            null
        }
    }

    fun subscribeToTicketUpdates(): Flow<PostgresAction> = flow {
        try {
            client.realtime.connect()
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

    suspend fun signInAuthority(emailInput: String, passwordInput: String): Result<AuthorityUser> {
        val cleanEmail = emailInput.trim().lowercase()
        val cleanPassword = passwordInput.trim()

        // Strict Supabase Auth only: verifies against Supabase auth.users
        return try {
            client.auth.signInWith(Email) {
                this.email = cleanEmail
                this.password = cleanPassword
            }
            // Supabase verified credentials successfully!
            val user = resolveAuthorityUser(cleanEmail)
            Result.success(user)
        } catch (e: Exception) {
            val msg = e.message.orEmpty()
            val userFriendlyMsg = if (msg.contains("ConnectException", ignoreCase = true) ||
                msg.contains("UnknownHost", ignoreCase = true) ||
                msg.contains("timeout", ignoreCase = true)) {
                "Network connection error. Please check your internet connection."
            } else {
                "Invalid official email or password. Please try again."
            }
            Result.failure(Exception(userFriendlyMsg))
        }
    }

    private fun resolveAuthorityUser(email: String): AuthorityUser {
        return when (email) {
            "kushsharma.cor@gmail.com" -> AuthorityUser(email, "SUPER_ADMIN", "कुश शर्मा")
            "lavsharma.cor@gmail.com" -> AuthorityUser(email, "SUPER_ADMIN", "लव शर्मा")
            "sabhapati@gmail.com", "chairman@chittorgarh.gov.in" -> AuthorityUser(email, "CHAIRMAN", "अनिल इनाणी")
            "upsabhapati@gmail.com", "vicechairman@chittorgarh.gov.in" -> AuthorityUser(email, "VICE_CHAIRMAN", "सुदर्शन रामपुरा")
            "ravivirani@gmail.com" -> AuthorityUser(email, "PORTAL_ADMIN", "रवि विरानी")
            else -> {
                val derivedName = email.substringBefore("@")
                    .split(".", "_")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                AuthorityUser(email, "OFFICER", derivedName)
            }
        }
    }
}

data class AuthorityUser(
    val email: String,
    val role: String,
    val fullName: String
)


