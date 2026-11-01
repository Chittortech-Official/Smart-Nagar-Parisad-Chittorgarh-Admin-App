package com.chittortech.snp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.chittortech.snp.R
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
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf<BottomTab>(BottomTab.Home) }
    val tabHistory = remember { mutableStateListOf<BottomTab>() }

    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }
    var showNotifications by remember { mutableStateOf(false) }
    var showSlaScreen by remember { mutableStateOf(false) }
    var showAboutScreen by remember { mutableStateOf(false) }

    var showWelcomeNotification by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(8000)
        showWelcomeNotification = false
    }

    val greetingTitle = when {
        authorityName.contains("Anil", ignoreCase = true) || authorityRole.equals("CHAIRMAN", ignoreCase = true) -> "🙏 नमस्ते अनिल जी!"
        authorityName.contains("Sudarshan", ignoreCase = true) || authorityRole.equals("VICE_CHAIRMAN", ignoreCase = true) -> "🙏 नमस्ते सुदर्शन जी!"
        else -> "🙏 नमस्ते $authorityName जी!"
    }

    val greetingSubtitle = if (authorityRole.equals("CHAIRMAN", ignoreCase = true)) {
        "माननीय सभापति • नगर परिषद चित्तौड़गढ़ कमांड सेंटर में आपका स्वागत है"
    } else {
        "माननीय उपसभापति • नगर परिषद चित्तौड़गढ़ कमांड सेंटर में आपका स्वागत है"
    }

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
                                onNotificationClick = { showNotifications = true },
                                onRefreshClick = onRefresh
                            )
                        }
                        BottomTab.Complaints -> {
                            ComplaintsScreen(
                                tickets = tickets,
                                onTicketClick = { selectedTicket = it },
                                onNotificationClick = { showNotifications = true },
                                onRefreshClick = onRefresh
                            )
                        }
                        BottomTab.Parshads -> {
                            ParshadsScreen(
                                wards = wards,
                                onNotificationClick = { showNotifications = true },
                                onRefreshClick = onRefresh
                            )
                        }
                        BottomTab.Departments -> {
                            DepartmentsScreen(
                                departments = deptStats,
                                onNotificationClick = { showNotifications = true },
                                onRefreshClick = onRefresh
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

            // ─── Executive Dynamic Island Welcome Pill (Expanding from Notch) ───
            AnimatedVisibility(
                visible = showWelcomeNotification && selectedTicket == null && !showNotifications && !showSlaScreen && !showAboutScreen,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(350)
                ) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .padding(start = 12.dp, end = 12.dp, top = 6.dp)
                    .zIndex(100f)
            ) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFF0A0F1D), // Obsidian pitch black Dynamic Island
                    shadowElevation = 14.dp,
                    border = BorderStroke(1.2.dp, SnpGold.copy(alpha = 0.75f)),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, SnpGold),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.rajasthan_logo),
                                    contentDescription = "Rajasthan Government",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = greetingTitle,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = greetingSubtitle,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Surface(
                            shape = CircleShape,
                            color = Color(0x33FFFFFF),
                            modifier = Modifier.size(24.dp)
                        ) {
                            IconButton(
                                onClick = { showWelcomeNotification = false },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

