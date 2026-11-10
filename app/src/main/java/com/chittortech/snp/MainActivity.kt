package com.chittortech.snp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.chittortech.snp.data.SnpSessionManager
import com.chittortech.snp.data.SnpSessionResult
import com.chittortech.snp.data.SupabaseManager
import com.chittortech.snp.data.models.*
import com.chittortech.snp.ui.components.SnpLoadingScreen
import com.chittortech.snp.ui.main.MainScreen
import com.chittortech.snp.ui.screens.auth.SnpLoginScreen
import com.chittortech.snp.ui.screens.splash.SnpSplashScreen
import com.chittortech.snp.ui.theme.SmartNagarParishadTheme
import com.chittortech.snp.ui.theme.SnpPrimary
import com.chittortech.snp.util.DateUtils
import com.chittortech.snp.util.SnpNotificationManager
import com.chittortech.snp.util.SnpTicketMonitor
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val targetTicketState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extractTicketIntent(intent)
        enableEdgeToEdge()

        setContent {
            SmartNagarParishadTheme {
                val scope = rememberCoroutineScope()
                val targetTicket = targetTicketState.value
                var isSplashShowing by remember { mutableStateOf(true) }
                var currentUser by remember { mutableStateOf<Triple<String, String, String>?>(null) } // email, role, name

                var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
                var wards by remember { mutableStateOf<List<Ward>>(emptyList()) }
                var departments by remember { mutableStateOf<List<Department>>(emptyList()) }
                var notifications by remember { mutableStateOf<List<NotificationLog>>(emptyList()) }
                var isInitialLoading by remember { mutableStateOf(false) }
                var isRefreshing by remember { mutableStateOf(false) }
                var hasShownWelcomeNotification by remember { mutableStateOf(false) }
                var isInitialFetchDone by remember { mutableStateOf(false) }

                fun refreshData(isManual: Boolean = false) {
                    if (isRefreshing) return
                    scope.launch {
                        if (isManual) {
                            isRefreshing = true
                        } else if (tickets.isEmpty()) {
                            isInitialLoading = true
                        }
                        try {
                            SupabaseManager.restoreSessionIfAvailable(this@MainActivity)
                            val newTickets = SupabaseManager.getTickets(this@MainActivity)
                            val newWards = SupabaseManager.getWards()
                            val newDepartments = SupabaseManager.getDepartments()
                            val newNotifications = SupabaseManager.getNotifications()

                            tickets = newTickets
                            wards = newWards
                            departments = newDepartments
                            notifications = newNotifications
                        } catch (_: Exception) {
                        } finally {
                            isInitialLoading = false
                            isRefreshing = false
                        }
                        SnpTicketMonitor.syncTickets(this@MainActivity)
                    }
                }

                // Check stored session & load data once in background
                LaunchedEffect(Unit) {
                    val sessionResult = SnpSessionManager.validateSession(this@MainActivity)
                    if (sessionResult is SnpSessionResult.Valid) {
                        val sessionRestored = SupabaseManager.restoreSessionIfAvailable(this@MainActivity)
                        if (sessionRestored && SupabaseManager.isSessionAuthenticated()) {
                            currentUser = Triple(sessionResult.email, sessionResult.role, sessionResult.fullName)
                            refreshData()
                            isInitialFetchDone = true
                        } else {
                            // Supabase session was revoked or expired: clear local cache to prompt clean re-login
                            SnpSessionManager.clearSession(this@MainActivity)
                            currentUser = null
                        }
                    }
                }

                // Auto-refresh only when returning to foreground after initial load (NOT on cold start)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            if (currentUser != null && isInitialFetchDone && !isSplashShowing) {
                                refreshData(isManual = false)
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // If intent targeted a specific ticket, ensure data is refreshed to locate it immediately
                LaunchedEffect(targetTicket) {
                    if (!targetTicket.isNullOrBlank()) {
                        refreshData(isManual = false)
                    }
                }

                // Notification Permission for Android 13+ (TIRAMISU)
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { /* Result handled */ }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Subscribe to Supabase Realtime ticket notifications to refresh UI
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        try {
                            SupabaseManager.subscribeToTicketUpdates().collect { _ ->
                                refreshData()
                            }
                        } catch (_: Exception) {}
                    }
                }

                // Calculate Stats strictly focusing on Today's Live Monitoring
                val dashboardStats = remember(tickets) {
                    val todayTickets = tickets.filter { DateUtils.isToday(it.registrationTime) }
                    val todayReg = todayTickets.size
                    val todayRunning = todayTickets.count { it.status == "IN_PROGRESS" || it.status == "REGISTERED" }
                    val todayCl = tickets.count {
                        (it.status == "CLOSED" || it.status.contains("RESOLVED", ignoreCase = true)) &&
                                (DateUtils.isToday(it.closedAt) || DateUtils.isToday(it.resolutionTime) || DateUtils.isToday(it.registrationTime))
                    }
                    val todayRej = tickets.count {
                        it.status.contains("REJECT", ignoreCase = true) &&
                                (DateUtils.isToday(it.closedAt) || DateUtils.isToday(it.resolutionTime) || DateUtils.isToday(it.registrationTime))
                    }

                    val pending = todayTickets.count { it.status == "REGISTERED" }
                    val inProg = todayTickets.count { it.status == "IN_PROGRESS" }
                    val monthCl = tickets.count { it.status == "CLOSED" && DateUtils.isThisMonth(it.closedAt) }
                    val yearCl = tickets.count { it.status == "CLOSED" && DateUtils.isThisYear(it.closedAt) }
                    val overdue = tickets.count { it.isOverdue || it.status == "OVERDUE" }
                    val escalated = tickets.count { it.isEscalated }
                    val total = tickets.size
                    val rate = if (todayReg > 0) (((todayCl) * 100) / todayReg) else 100

                    val totalReg = tickets.size
                    val totalInProg = tickets.count { it.status == "IN_PROGRESS" || it.status == "REGISTERED" }
                    val totalClosedCount = tickets.count { it.status == "CLOSED" || it.status.contains("RESOLVED", ignoreCase = true) }
                    val totalRejCount = tickets.count { it.status.contains("REJECT", ignoreCase = true) }

                    DashboardStats(
                        todayRegistered = todayReg,
                        todayRunning = todayRunning,
                        todayClosed = todayCl,
                        todayRejected = todayRej,
                        totalComplaints = total,
                        pendingComplaints = pending,
                        inProgressComplaints = inProg,
                        monthClosed = monthCl,
                        yearClosed = yearCl,
                        overdueComplaints = overdue,
                        escalatedComplaints = escalated,
                        resolutionRatePercent = rate,
                        totalRegistered = totalReg,
                        totalRunning = totalInProg,
                        totalCompleted = totalClosedCount,
                        totalRejected = totalRejCount
                    )
                }

                when {
                    isSplashShowing -> {
                        SnpSplashScreen(onSplashFinished = { isSplashShowing = false })
                    }
                    currentUser == null -> {
                        SnpLoginScreen(
                            onLoginSuccess = { email, role, name ->
                                SnpSessionManager.saveSession(this@MainActivity, email, role, name)
                                currentUser = Triple(email, role, name)
                                refreshData()
                            }
                        )
                    }
                    isInitialLoading && tickets.isEmpty() -> {
                        SnpLoadingScreen(message = "सर्वर से डेटा लोड हो रहा है...")
                    }
                    else -> {
                        val (email, role, name) = currentUser!!
                        MainScreen(
                            authorityEmail = email,
                            authorityRole = role,
                            authorityName = name,
                            tickets = tickets,
                            wards = wards,
                            departments = departments,
                            notifications = notifications,
                            stats = dashboardStats,
                            isRefreshing = isRefreshing,
                            targetTicketNumber = targetTicket,
                            onTargetTicketHandled = { targetTicketState.value = null },
                            hasShownWelcomeNotification = hasShownWelcomeNotification,
                            onWelcomeDismissed = { hasShownWelcomeNotification = true },
                            onRefresh = { refreshData(isManual = true) },
                            onLogout = {
                                SnpSessionManager.clearSession(this@MainActivity)
                                currentUser = null
                                hasShownWelcomeNotification = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractTicketIntent(intent)
    }

    private fun extractTicketIntent(incomingIntent: android.content.Intent?) {
        val tNumber = incomingIntent?.getStringExtra("EXTRA_TICKET_NUMBER")
        val tId = incomingIntent?.getStringExtra("EXTRA_TICKET_ID")
        // Length-cap: reject any malformed/oversized extras (prevents state corruption)
        val target = tNumber?.takeIf { it.isNotBlank() && it.length < 100 }
            ?: tId?.takeIf { it.isNotBlank() && it.length < 100 }
        if (!target.isNullOrBlank()) {
            targetTicketState.value = target
        }
    }
}

