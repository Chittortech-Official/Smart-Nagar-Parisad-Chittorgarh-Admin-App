package com.chittortech.snp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.chittortech.snp.data.SupabaseManager
import com.chittortech.snp.data.models.Ticket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object SnpTicketMonitor {
    private const val TAG = "SnpTicketMonitor"
    private const val PREFS_NAME = "snp_ticket_monitor_state_v2"

    // Encrypted prefs so ticket UUIDs/status are not readable on rooted devices
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
    private const val KEY_INITIALIZED = "is_monitor_initialized"
    private const val KEY_LAST_STATUS_PREFIX = "ticket_status_"
    private const val KEY_OVERDUE_PREFIX = "ticket_overdue_"

    // 15-Minute battery-saver fallback (WebSocket handles live push instantly)
    private const val FALLBACK_POLLING_INTERVAL_MS = 15 * 60 * 1000L

    private val monitorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    @Volatile
    private var isMonitoringStarted = false

    fun startMonitoring(context: Context) {
        if (isMonitoringStarted) return
        isMonitoringStarted = true
        val appContext = context.applicationContext

        monitorScope.launch {
            Log.d(TAG, "Starting SnpTicketMonitor background engine...")
            // 1. Initial snapshot & sync
            syncTickets(appContext)

            // 2. Realtime WebSocket subscription listener
            launch {
                startRealtimeSubscription(appContext)
            }

            // 3. Continuous background polling loop (every 5 seconds)
            launch {
                startPollingLoop(appContext)
            }
        }
    }

    suspend fun syncTickets(context: Context) = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            try {
                val tickets = SupabaseManager.getTickets()
                if (tickets.isNotEmpty()) {
                    processTicketBatch(context, tickets)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during ticket sync: ${e.message}")
            }
        }
    }

    private fun processTicketBatch(context: Context, tickets: List<Ticket>) {
        val prefs = getPrefs(context)
        val isInitialized = prefs.getBoolean(KEY_INITIALIZED, false)
        val editor = prefs.edit()

        if (!isInitialized) {
            // First run baseline: Record current state of historical tickets
            for (ticket in tickets) {
                val statusKey = KEY_LAST_STATUS_PREFIX + ticket.id
                editor.putString(statusKey, ticket.status)
                if (ticket.isOverdue || ticket.status.equals("OVERDUE", ignoreCase = true)) {
                    editor.putBoolean(KEY_OVERDUE_PREFIX + ticket.id, true)
                }

                // If a ticket was registered recently (within last 60 minutes), alert immediately!
                if (DateUtils.isRecent(ticket.registrationTime, minutes = 60)) {
                    Log.d(TAG, "🔔 Found recent live ticket during baseline initialization: ${ticket.ticketNumber}")
                    val desc = ticket.problemDescription
                    val isEmerg = desc.contains("आग") || desc.contains("मृत") || desc.contains("दमकल") ||
                            desc.contains("दुर्घटना") || desc.contains("धंस") || desc.contains("गंभीर") ||
                            ticket.forwardingOption.contains("EMERGENCY", ignoreCase = true)

                    SnpNotificationManager.showNewComplaintAlert(
                        context = context,
                        ticketId = ticket.id,
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = desc,
                        citizenName = ticket.citizenName,
                        citizenPhone = ticket.citizenPhone,
                        isEmergency = isEmerg
                    )
                }
            }
            editor.putBoolean(KEY_INITIALIZED, true)
            editor.apply()
            Log.d(TAG, "Monitor baseline initialized with ${tickets.size} tickets. Live tracking active.")
            return
        }

        // Live diff processing: detect new complaints, status updates, resolutions, and overdues
        for (ticket in tickets) {
            val statusKey = KEY_LAST_STATUS_PREFIX + ticket.id
            val previousStatus = prefs.getString(statusKey, null)

            if (previousStatus == null) {
                // 1. BRAND NEW COMPLAINT!
                Log.d(TAG, "🔔 Live new complaint detected: ${ticket.ticketNumber} (${ticket.id}) | status: ${ticket.status}")
                val desc = ticket.problemDescription
                val isEmerg = desc.contains("आग") || desc.contains("मृत") || desc.contains("दमकल") ||
                        desc.contains("दुर्घटना") || desc.contains("धंस") || desc.contains("गंभीर") ||
                        ticket.forwardingOption.contains("EMERGENCY", ignoreCase = true)

                SnpNotificationManager.showNewComplaintAlert(
                    context = context,
                    ticketId = ticket.id,
                    ticketNumber = ticket.ticketNumber,
                    wardNumber = ticket.wardNumber,
                    categoryTitle = desc,
                    citizenName = ticket.citizenName,
                    citizenPhone = ticket.citizenPhone,
                    isEmergency = isEmerg
                )
                editor.putString(statusKey, ticket.status)
            } else if (!previousStatus.equals(ticket.status, ignoreCase = true)) {
                // 2. STATUS CHANGED!
                Log.d(TAG, "🔔 Live status transition detected for ${ticket.ticketNumber}: $previousStatus -> ${ticket.status}")
                val newStatus = ticket.status.trim().uppercase()
                if (newStatus == "CLOSED" || newStatus.contains("RESOLVED")) {
                    SnpNotificationManager.showResolvedAlert(
                        context = context,
                        ticketId = ticket.id,
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = ticket.problemDescription,
                        citizenName = ticket.citizenName,
                        citizenPhone = ticket.citizenPhone
                    )
                } else {
                    SnpNotificationManager.showStatusChangeAlert(
                        context = context,
                        ticketId = ticket.id,
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = ticket.problemDescription,
                        newStatus = ticket.status,
                        citizenName = ticket.citizenName,
                        citizenPhone = ticket.citizenPhone
                    )
                }
                editor.putString(statusKey, ticket.status)
            }

            // 3. SLA OVERDUE ESCALATION!
            val overdueKey = KEY_OVERDUE_PREFIX + ticket.id
            val wasOverdueNotified = prefs.getBoolean(overdueKey, false)
            if ((ticket.isOverdue || ticket.status.equals("OVERDUE", ignoreCase = true)) && !wasOverdueNotified) {
                Log.d(TAG, "🔔 Live SLA Overdue breach detected: ${ticket.ticketNumber}")
                SnpNotificationManager.showOverdueAlert(
                    context = context,
                    ticketId = ticket.id,
                    ticketNumber = ticket.ticketNumber,
                    wardNumber = ticket.wardNumber,
                    categoryTitle = ticket.problemDescription,
                    citizenName = ticket.citizenName,
                    citizenPhone = ticket.citizenPhone
                )
                editor.putBoolean(overdueKey, true)
            }
        }

        editor.apply()
    }

    private suspend fun startPollingLoop(context: Context) {
        while (true) {
            try {
                delay(FALLBACK_POLLING_INTERVAL_MS)
                syncTickets(context)
            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                Log.e(TAG, "Error in polling loop: ${e.message}")
                delay(60_000)
            }
        }
    }

    private suspend fun startRealtimeSubscription(context: Context) {
        while (true) {
            try {
                SupabaseManager.subscribeToTicketUpdates()
                    .catch { e ->
                        Log.e(TAG, "Realtime subscription exception: ${e.message}")
                    }
                    .collect { action ->
                        Log.d(TAG, "Supabase Realtime action received: $action")
                        // Trigger immediate state diff & sync
                        syncTickets(context)
                    }
            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                Log.e(TAG, "Realtime subscription reconnecting: ${e.message}")
                delay(5_000)
            }
        }
    }
}
