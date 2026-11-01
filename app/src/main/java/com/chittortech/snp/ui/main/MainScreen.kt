package com.chittortech.snp.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.chittortech.snp.R
import com.chittortech.snp.data.models.*
import com.chittortech.snp.ui.screens.about.AboutAppScreen
import com.chittortech.snp.ui.screens.complaints.ComplaintDetailScreen
import com.chittortech.snp.ui.screens.complaints.ComplaintsScreen
import com.chittortech.snp.ui.screens.contact.ContactDeveloperScreen
import com.chittortech.snp.ui.screens.controlroom.ControlRoomScreen
import com.chittortech.snp.ui.screens.departments.DepartmentsScreen
import com.chittortech.snp.ui.screens.home.HomeScreen
import com.chittortech.snp.ui.screens.more.MoreScreen
import com.chittortech.snp.ui.screens.notifications.NotificationsScreen
import com.chittortech.snp.ui.screens.parshads.ParshadsScreen
import com.chittortech.snp.ui.screens.sla.SlaScreen
import com.chittortech.snp.ui.theme.*

sealed class BottomTab(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedColor: Color,
    val unselectedColor: Color
) {
    object Home : BottomTab("home", "Home", Icons.Default.Dashboard, Color(0xFF1E40AF), Color(0xFF3B82F6))
    object Complaints : BottomTab("complaints", "Complaints", Icons.AutoMirrored.Filled.Assignment, Color(0xFFDC2626), Color(0xFFEF4444))
    object Parshads : BottomTab("parshads", "Parshads", Icons.Default.People, Color(0xFF047857), Color(0xFF10B981))
    object Departments : BottomTab("departments", "Depts", Icons.Default.Apartment, Color(0xFF7C3AED), Color(0xFF8B5CF6))
    object Settings : BottomTab("settings", "Settings", Icons.Default.Settings, Color(0xFFD97706), Color(0xFFF59E0B))
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
    hasShownWelcomeNotification: Boolean = false,
    onWelcomeDismissed: () -> Unit = {},
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
    var showContactScreen by remember { mutableStateOf(false) }
    var showControlRoomScreen by remember { mutableStateOf(false) }

    var showWelcomeNotification by remember { mutableStateOf(false) }
    var isIslandExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(hasShownWelcomeNotification) {
        if (!hasShownWelcomeNotification) {
            // Settle HomeScreen for 400ms
            kotlinx.coroutines.delay(400)
            showWelcomeNotification = true
            // Expand fluidly like Apple Dynamic Island
            kotlinx.coroutines.delay(350)
            isIslandExpanded = true
            // Stay expanded for 8.5 seconds
            kotlinx.coroutines.delay(8500)
            // Morph smoothly back into compact notch pill
            isIslandExpanded = false
            // After 3.5 seconds in compact pill, slide smoothly into notch
            kotlinx.coroutines.delay(3500)
            showWelcomeNotification = false
            onWelcomeDismissed()
        }
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
            showControlRoomScreen ||
            showContactScreen ||
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
            showControlRoomScreen -> {
                showControlRoomScreen = false
            }
            showContactScreen -> {
                showContactScreen = false
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (selectedTicket == null && !showNotifications && !showSlaScreen && !showAboutScreen && !showContactScreen && !showControlRoomScreen) {
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
                        BottomTab.Settings
                    )
                    tabs.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateToTab(tab) },
                            alwaysShowLabel = true,
                            icon = {
                                Icon(
                                    tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    tab.title,
                                    fontSize = 10.sp,
                                    letterSpacing = (-0.2).sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = tab.selectedColor,
                                selectedTextColor = tab.selectedColor,
                                indicatorColor = tab.selectedColor.copy(alpha = 0.16f),
                                unselectedIconColor = tab.unselectedColor.copy(alpha = 0.70f),
                                unselectedTextColor = tab.unselectedColor.copy(alpha = 0.85f)
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
                .padding(bottom = paddingValues.calculateBottomPadding())
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
                showContactScreen -> {
                    ContactDeveloperScreen(
                        onBack = { showContactScreen = false }
                    )
                }
                showControlRoomScreen -> {
                    ControlRoomScreen(
                        onBack = { showControlRoomScreen = false }
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
                                onRefreshClick = onRefresh,
                                onHeaderClick = {
                                    showWelcomeNotification = true
                                    isIslandExpanded = true
                                }
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
                        BottomTab.Settings -> {
                            MoreScreen(
                                authorityName = authorityName,
                                authorityRole = authorityRole,
                                authorityEmail = authorityEmail,
                                onNavigateToSla = { showSlaScreen = true },
                                onNavigateToNotifications = { showNotifications = true },
                                onNavigateToAbout = { showAboutScreen = true },
                                onNavigateToContact = { showContactScreen = true },
                                onNavigateToControlRoom = { showControlRoomScreen = true },
                                onLogout = onLogout,
                                onNotificationClick = { showNotifications = true }
                            )
                        }
                    }
                }
            }

            // ─── Executive Dynamic Island Welcome Pill (Apple Responsive Morph) ───
            AnimatedVisibility(
                visible = showWelcomeNotification && selectedTicket == null && !showNotifications && !showSlaScreen && !showAboutScreen && !showContactScreen && !showControlRoomScreen,
                enter = expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = 300f
                    )
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(animationSpec = tween(250)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .padding(start = 12.dp, end = 12.dp, top = 6.dp)
                    .zIndex(100f)
            ) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF000000), // True Apple Dynamic Island Pitch Black
                    shadowElevation = 16.dp,
                    border = BorderStroke(1.2.dp, SnpGold.copy(alpha = 0.9f)),
                    modifier = Modifier
                        .wrapContentSize()
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                        .clickable { isIslandExpanded = !isIslandExpanded }
                ) {
                    if (!isIslandExpanded) {
                        // ─── Collapsed State (Compact Notch Pill) ──────────────
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(0.8.dp, SnpGold),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.rajasthan_logo),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "गढ़-सेवा • $greetingTitle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                tint = SnpGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        // ─── Expanded State (Full Dynamic Island Card) ─────────
                        Row(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 12.dp, top = 9.dp, bottom = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(1.2.dp, SnpGold),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.rajasthan_logo),
                                        contentDescription = "Rajasthan Government",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Text(
                                    text = greetingTitle,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = greetingSubtitle,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 14.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Surface(
                                shape = CircleShape,
                                color = Color(0x33FFFFFF),
                                modifier = Modifier.size(26.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        showWelcomeNotification = false
                                        onWelcomeDismissed()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color(0xFFCBD5E1),
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
}

