package com.chittortech.snp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

sealed class SnpSessionResult {
    data class Valid(val email: String, val role: String, val fullName: String) : SnpSessionResult()
    data class Expired(val message: String) : SnpSessionResult()
    object NoSession : SnpSessionResult()
}

object SnpSessionManager {

    private const val PREFS_NAME = "snp_authority_session_secure_prefs"
    private const val KEY_LOGIN_TIMESTAMP = "snp_login_timestamp"
    private const val KEY_LAST_ACTIVE_TIMESTAMP = "snp_last_active_timestamp"
    private const val KEY_EMAIL = "snp_authority_email"
    private const val KEY_ROLE = "snp_authority_role"
    private const val KEY_FULL_NAME = "snp_authority_name"
    private const val KEY_SUPABASE_SESSION_JSON = "snp_supabase_session_json"

    // 5 Days Inactivity Timeout
    const val INACTIVITY_TIMEOUT_MS: Long = 5L * 24 * 60 * 60 * 1000L

    // 7 Days Max Session Lifetime
    const val MAX_SESSION_LIFETIME_MS: Long = 7L * 24 * 60 * 60 * 1000L

    private fun getPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveSession(context: Context, email: String, role: String, fullName: String, sessionJson: String? = null) {
        val now = System.currentTimeMillis()
        val editor = getPrefs(context).edit()
            .putLong(KEY_LOGIN_TIMESTAMP, now)
            .putLong(KEY_LAST_ACTIVE_TIMESTAMP, now)
            .putString(KEY_EMAIL, email.trim().lowercase())
            .putString(KEY_ROLE, role.trim().uppercase())
            .putString(KEY_FULL_NAME, fullName.trim())
        if (!sessionJson.isNullOrBlank()) {
            editor.putString(KEY_SUPABASE_SESSION_JSON, sessionJson)
        }
        editor.apply()
    }

    fun saveSupabaseSessionJson(context: Context, sessionJson: String) {
        getPrefs(context).edit()
            .putString(KEY_SUPABASE_SESSION_JSON, sessionJson)
            .apply()
    }

    fun getSavedSupabaseSessionJson(context: Context): String? {
        return getPrefs(context).getString(KEY_SUPABASE_SESSION_JSON, null)
    }

    fun updateLastActive(context: Context) {
        val prefs = getPrefs(context)
        if (prefs.contains(KEY_EMAIL)) {
            prefs.edit()
                .putLong(KEY_LAST_ACTIVE_TIMESTAMP, System.currentTimeMillis())
                .apply()
        }
    }

    fun validateSession(context: Context): SnpSessionResult {
        val prefs = getPrefs(context)
        val email = prefs.getString(KEY_EMAIL, null)
        val role = prefs.getString(KEY_ROLE, null) ?: ""
        val fullName = prefs.getString(KEY_FULL_NAME, "Hon. Authority") ?: "Hon. Authority"
        val loginTs = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)
        val lastActiveTs = prefs.getLong(KEY_LAST_ACTIVE_TIMESTAMP, 0L)

        if (email.isNullOrBlank() || loginTs == 0L || lastActiveTs == 0L) {
            return SnpSessionResult.NoSession
        }

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastActiveTs > INACTIVITY_TIMEOUT_MS) {
            clearSession(context)
            return SnpSessionResult.Expired("Session expired due to 5 days of inactivity.")
        }

        if (currentTime - loginTs > MAX_SESSION_LIFETIME_MS) {
            clearSession(context)
            return SnpSessionResult.Expired("Session reached 7-day security limit.")
        }

        updateLastActive(context)
        return SnpSessionResult.Valid(email, role, fullName)
    }

    fun clearSession(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}

