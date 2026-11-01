package com.chittortech.snp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        SnpLoadingScreen(message = "Synchronizing data from the backend...")
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
                            onRefresh = { refreshData() },
                            onLogout = {
                                SnpSessionManager.clearSession(this@MainActivity)
                                currentUser = null
                            }
                        )
                    }
                }
            }
        }
    }
}

