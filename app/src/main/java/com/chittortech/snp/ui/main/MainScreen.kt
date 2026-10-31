package com.chittortech.snp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.models.*
import com.chittortech.snp.ui.screens.about.AboutAppScreen
import com.chittortech.snp.ui.screens.complaints.ComplaintDetailScreen
import com.chittortech.snp.ui.screens.complaints.ComplaintsScreen
import com.chittortech.snp.ui.screens.departments.DepartmentsScreen
import com.chittortech.snp.ui.screens.home.HomeScreen
import com.chittortech.snp.ui.screens.more.MoreScreen
import com.chittortech.snp.ui.screens.notifications.NotificationsScreen
import com.chittortech.snp.ui.screens.parshads.ParshadsScreen
import com.chittortech.snp.ui.screens.sla.SlaScreen
import com.chittortech.snp.ui.theme.*

sealed class BottomTab(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomTab("home", "Home", Icons.Default.Dashboard)
    object Complaints : BottomTab("complaints", "Complaints", Icons.AutoMirrored.Filled.Assignment)
    object Parshads : BottomTab("parshads", "Parshads", Icons.Default.People)
    object Departments : BottomTab("departments", "Depts", Icons.Default.Apartment)
    object More : BottomTab("more", "More", Icons.Default.MoreHoriz)
}

@Composable
fun MainScreen(
    authorityEmail: String,
    authorityRole: String,
    authorityName: String,
    tickets: List<Ticket>,
    wards: List<Ward>,
    departments: List<Department>,
    notifications: List<NotificationLog>,
    stats: DashboardStats,
    deptStats: List<DepartmentStats>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf<BottomTab>(BottomTab.Home) }
    val tabHistory = remember { mutableStateListOf<BottomTab>() }

    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }
    var showNotifications by remember { mutableStateOf(false) }
    var showSlaScreen by remember { mutableStateOf(false) }
    var showAboutScreen by remember { mutableStateOf(false) }

    fun navigateToTab(tab: BottomTab) {
        if (currentTab != tab) {
            tabHistory.add(currentTab)
            currentTab = tab
        }
    }

    val canHandleBack = selectedTicket != null ||
            showAboutScreen ||
            showSlaScreen ||
            showNotifications ||
            tabHistory.isNotEmpty() ||
            currentTab != BottomTab.Home

    BackHandler(enabled = canHandleBack) {
        when {
            selectedTicket != null -> {
                selectedTicket = null
            }
            showAboutScreen -> {
                showAboutScreen = false
            }
            showSlaScreen -> {
                showSlaScreen = false
            }
            showNotifications -> {
                showNotifications = false
            }
            tabHistory.isNotEmpty() -> {
                currentTab = tabHistory.removeAt(tabHistory.lastIndex)
            }
            currentTab != BottomTab.Home -> {
                currentTab = BottomTab.Home
            }
        }
    }

    val overdueTickets = remember(tickets) {
        tickets.filter { it.isOverdue || it.status.equals("OVERDUE", ignoreCase = true) }
    }

    Scaffold(
        bottomBar = {
            if (selectedTicket == null && !showNotifications && !showSlaScreen && !showAboutScreen) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val tabs = listOf(
                        BottomTab.Home,
                        BottomTab.Complaints,
                        BottomTab.Parshads,
                        BottomTab.Departments,
                        BottomTab.More
                    )
                    tabs.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateToTab(tab) },
                            icon = { Icon(tab.icon, contentDescription = tab.title, modifier = Modifier.size(22.dp)) },
                            label = {
                                Text(
                                    tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SnpPrimary,
                                selectedTextColor = SnpPrimary,
                                indicatorColor = SnpPrimaryLight,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                selectedTicket != null -> {
                    ComplaintDetailScreen(
                        ticket = selectedTicket!!,
                        onBack = { selectedTicket = null }
                    )
                }
                showNotifications -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = { showNotifications = false }
                    )
                }
                showSlaScreen -> {
                    SlaScreen(
                        overdueTickets = overdueTickets,
                        onTicketClick = { selectedTicket = it },
                        onBack = { showSlaScreen = false }
                    )
                }
                showAboutScreen -> {
                    AboutAppScreen(
                        onBack = { showAboutScreen = false }
                    )
                }
                else -> {
                    when (currentTab) {
                        BottomTab.Home -> {
                            HomeScreen(
                                authorityRole = authorityRole,
                                authorityName = authorityName,
                                stats = stats,
                                recentTickets = tickets,
                                onViewAllComplaints = { navigateToTab(BottomTab.Complaints) },
                                onTicketClick = { selectedTicket = it },
                                onNotificationClick = { showNotifications = true }
                            )
                        }
                        BottomTab.Complaints -> {
                            ComplaintsScreen(
                                tickets = tickets,
                                onTicketClick = { selectedTicket = it },
                                onNotificationClick = { showNotifications = true }
                            )
                        }
                        BottomTab.Parshads -> {
                            ParshadsScreen(
                                wards = wards,
                                onNotificationClick = { showNotifications = true }
                            )
                        }
                        BottomTab.Departments -> {
                            DepartmentsScreen(
                                departments = deptStats,
                                onNotificationClick = { showNotifications = true }
                            )
                        }
                        BottomTab.More -> {
                            MoreScreen(
                                authorityName = authorityName,
                                authorityRole = authorityRole,
                                authorityEmail = authorityEmail,
                                onNavigateToSla = { showSlaScreen = true },
                                onNavigateToNotifications = { showNotifications = true },
                                onNavigateToAbout = { showAboutScreen = true },
                                onLogout = onLogout,
                                onNotificationClick = { showNotifications = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

