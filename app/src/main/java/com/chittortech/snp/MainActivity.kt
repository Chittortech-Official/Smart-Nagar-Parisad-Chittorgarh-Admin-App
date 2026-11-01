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
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartNagarParishadTheme {
                val scope = rememberCoroutineScope()
                var isSplashShowing by remember { mutableStateOf(true) }
                var currentUser by remember { mutableStateOf<Triple<String, String, String>?>(null) } // email, role, name

                var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
                var wards by remember { mutableStateOf<List<Ward>>(emptyList()) }
                var departments by remember { mutableStateOf<List<Department>>(emptyList()) }
                var notifications by remember { mutableStateOf<List<NotificationLog>>(emptyList()) }
                var isLoadingData by remember { mutableStateOf(false) }
                var hasShownWelcomeNotification by remember { mutableStateOf(false) }

                fun refreshData() {
                    scope.launch {
                        isLoadingData = true
                        tickets = SupabaseManager.getTickets()
                        wards = SupabaseManager.getWards()
                        departments = SupabaseManager.getDepartments()
                        notifications = SupabaseManager.getNotifications()
                        isLoadingData = false
                    }
                }

                // Check stored session
                LaunchedEffect(Unit) {
                    val sessionResult = SnpSessionManager.validateSession(this@MainActivity)
                    if (sessionResult is SnpSessionResult.Valid) {
                        currentUser = Triple(sessionResult.email, sessionResult.role, sessionResult.fullName)
                        refreshData()
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

                // Subscribe to Supabase Realtime ticket notifications
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        try {
                            SupabaseManager.subscribeToTicketUpdates().collect { action ->
                                refreshData()
                                if (action is PostgresAction.Insert) {
                                    val tNum = action.record["ticket_number"]?.toString()?.replace("\"", "") ?: "SNP-LIVE"
                                    val wNum = action.record["ward_number"]?.toString()?.replace("\"", "")?.toIntOrNull() ?: 1
                                    val desc = action.record["problem_description"]?.toString()?.replace("\"", "") ?: "नई नागरिक शिकायत दर्ज"
                                    val citizen = action.record["citizen_name"]?.toString()?.replace("\"", "") ?: "नागरिक"
                                    val isEmerg = desc.contains("आग") || desc.contains("मृत") || desc.contains("दमकल") || desc.contains("दुर्घटना")
                                    SnpNotificationManager.showNewComplaintAlert(
                                        context = this@MainActivity,
                                        ticketNumber = tNum,
                                        wardNumber = wNum,
                                        categoryTitle = desc,
                                        citizenName = citizen,
                                        isEmergency = isEmerg
                                    )
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }

                // Calculate Stats
                val dashboardStats = remember(tickets) {
                    val todayReg = tickets.count { DateUtils.isToday(it.registrationTime) }
                    val total = tickets.size
                    val pending = tickets.count { it.status == "REGISTERED" }
                    val inProg = tickets.count { it.status == "IN_PROGRESS" }
                    val todayCl = tickets.count { it.status == "CLOSED" && DateUtils.isToday(it.closedAt) }
                    val monthCl = tickets.count { it.status == "CLOSED" && DateUtils.isThisMonth(it.closedAt) }
                    val yearCl = tickets.count { it.status == "CLOSED" && DateUtils.isThisYear(it.closedAt) }
                    val overdue = tickets.count { it.isOverdue || it.status == "OVERDUE" }
                    val escalated = tickets.count { it.isEscalated }
                    val rate = if (total > 0) ((monthCl * 100) / total) else 0

                    DashboardStats(
                        todayRegistered = todayReg,
                        totalComplaints = total,
                        pendingComplaints = pending,
                        inProgressComplaints = inProg,
                        todayClosed = todayCl,
                        monthClosed = monthCl,
                        yearClosed = yearCl,
                        overdueComplaints = overdue,
                        escalatedComplaints = escalated,
                        resolutionRatePercent = rate
                    )
                }

                val deptStats = remember(departments, tickets) {
                    departments.map { dept ->
                        val deptTickets = tickets.filter { it.departmentId == dept.id }
                        val dTotal = deptTickets.size
                        val dPending = deptTickets.count { it.status == "REGISTERED" }
                        val dInProg = deptTickets.count { it.status == "IN_PROGRESS" }
                        val dClosed = deptTickets.count { it.status == "CLOSED" }
                        val dOverdue = deptTickets.count { it.isOverdue || it.status == "OVERDUE" }
                        val dRate = if (dTotal > 0) ((dClosed * 100) / dTotal) else 0

                        DepartmentStats(
                            department = dept,
                            total = dTotal,
                            pending = dPending,
                            inProgress = dInProg,
                            closed = dClosed,
                            overdue = dOverdue,
                            resolutionRatePercent = dRate
                        )
                    }
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
                            },
                            onDirectDemoLogin = { email, role, name ->
                                SnpSessionManager.saveSession(this@MainActivity, email, role, name)
                                currentUser = Triple(email, role, name)
                                refreshData()
                            }
                        )
                    }
                    isLoadingData -> {
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
                            deptStats = deptStats,
                            hasShownWelcomeNotification = hasShownWelcomeNotification,
                            onWelcomeDismissed = { hasShownWelcomeNotification = true },
                            onRefresh = { refreshData() },
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
}

