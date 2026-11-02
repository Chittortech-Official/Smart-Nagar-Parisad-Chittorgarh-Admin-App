package com.chittortech.snp.util

import android.content.Context
import android.util.Log
import com.chittortech.snp.data.SupabaseManager
import com.chittortech.snp.data.models.Ticket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object SnpTicketMonitor {
    private const val TAG = "SnpTicketMonitor"
    private const val PREFS_NAME = "snp_ticket_monitor_state"
    private const val KEY_INITIALIZED = "is_monitor_initialized"
    private const val KEY_LAST_STATUS_PREFIX = "ticket_status_"
    private const val KEY_OVERDUE_PREFIX = "ticket_overdue_"

    private val monitorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    @Volatile
    private var isMonitoringStarted = false

    fun startMonitoring(context: Context) {
        if (isMonitoringStarted) return
        isMonitoringStarted = true
        val appContext = context.applicationContext

        monitorScope.launch {
            Log.d(TAG, "Starting SnpTicketMonitor background service...")
            // 1. Initial snapshot & sync
            syncTickets(appContext)

            // 2. Realtime WebSocket subscription listener
            launch {
                startRealtimeSubscription(appContext)
            }

            // 3. Continuous background polling loop (every 10 seconds)
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
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isInitialized = prefs.getBoolean(KEY_INITIALIZED, false)
        val editor = prefs.edit()

        if (!isInitialized) {
            // First run baseline: Record current state of all existing tickets
            // This prevents old historical tickets from spamming the user on initial boot
            for (ticket in tickets) {
                editor.putString(KEY_LAST_STATUS_PREFIX + ticket.ticketNumber, ticket.status)
                if (ticket.isOverdue || ticket.status.equals("OVERDUE", ignoreCase = true)) {
                    editor.putBoolean(KEY_OVERDUE_PREFIX + ticket.ticketNumber, true)
                }
            }
            editor.putBoolean(KEY_INITIALIZED, true)
            editor.apply()
            Log.d(TAG, "Monitor baseline initialized with ${tickets.size} tickets. Ready for live events.")
            return
        }

        // Live diff processing: detect new complaints, status updates, resolutions, and overdues
        for (ticket in tickets) {
            val statusKey = KEY_LAST_STATUS_PREFIX + ticket.ticketNumber
            val previousStatus = prefs.getString(statusKey, null)

            if (previousStatus == null) {
                // 1. BRAND NEW COMPLAINT!
                Log.d(TAG, "🔔 Live new complaint detected: ${ticket.ticketNumber} | status: ${ticket.status}")
                val desc = ticket.problemDescription
                val isEmerg = desc.contains("आग") || desc.contains("मृत") || desc.contains("दमकल") ||
                        desc.contains("दुर्घटना") || desc.contains("धंस") || desc.contains("गंभीर") ||
                        ticket.forwardingOption.contains("EMERGENCY", ignoreCase = true)

                SnpNotificationManager.showNewComplaintAlert(
                    context = context,
                    ticketNumber = ticket.ticketNumber,
                    wardNumber = ticket.wardNumber,
                    categoryTitle = desc,
                    citizenName = ticket.citizenName,
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
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = ticket.problemDescription
                    )
                } else {
                    SnpNotificationManager.showStatusChangeAlert(
                        context = context,
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = ticket.problemDescription,
                        newStatus = ticket.status
                    )
                }
                editor.putString(statusKey, ticket.status)
            }

            // 3. SLA OVERDUE ESCALATION!
            val overdueKey = KEY_OVERDUE_PREFIX + ticket.ticketNumber
            val wasOverdueNotified = prefs.getBoolean(overdueKey, false)
            if ((ticket.isOverdue || ticket.status.equals("OVERDUE", ignoreCase = true)) && !wasOverdueNotified) {
                Log.d(TAG, "🔔 Live SLA Overdue breach detected: ${ticket.ticketNumber}")
                SnpNotificationManager.showOverdueAlert(
                    context = context,
                    ticketNumber = ticket.ticketNumber,
                    wardNumber = ticket.wardNumber,
                    categoryTitle = ticket.problemDescription
                )
                editor.putBoolean(overdueKey, true)
            }
        }

        editor.apply()
    }

    private suspend fun startPollingLoop(context: Context) {
        while (true) {
            try {
                delay(10_000) // Poll every 10 seconds
                syncTickets(context)
            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                Log.e(TAG, "Error in polling loop: ${e.message}")
                delay(15_000)
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
