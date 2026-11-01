package com.chittortech.snp.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.OfficialCategoriesDirectory
import com.chittortech.snp.data.models.DashboardStats
import com.chittortech.snp.data.models.Ticket
import com.chittortech.snp.ui.components.KpiCard
import com.chittortech.snp.ui.components.SectionHeader
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.components.StatusBadge
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@Composable
fun HomeScreen(
    authorityRole: String,
    authorityName: String,
    stats: DashboardStats,
    recentTickets: List<Ticket>,
    onViewAllComplaints: () -> Unit,
    onTicketClick: (Ticket) -> Unit,
    onNotificationClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    onHeaderClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Today") }
    val filterOptions = listOf("Today", "Yesterday", "Last 7 Days", "This Month", "All Time")

    val filteredTickets = remember(recentTickets, selectedFilter) {
        when (selectedFilter) {
            "Today" -> recentTickets.filter { DateUtils.isToday(it.registrationTime) }
            "Yesterday" -> recentTickets.filter { DateUtils.isYesterday(it.registrationTime) }
            "Last 7 Days" -> recentTickets.filter { DateUtils.isLastNDays(it.registrationTime, 7) }
            "This Month" -> recentTickets.filter { DateUtils.isThisMonth(it.registrationTime) }
            else -> recentTickets
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = authorityName,
            subtitle = if (authorityRole == "CHAIRMAN") "Chairman • Nagar Parishad Chittorgarh" else "Vice Chairman • Nagar Parishad Chittorgarh",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick,
            onTitleClick = onHeaderClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Date Filters Strip ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        shape = SnpSquareShape,
                        label = { Text(filter, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SnpPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) SnpPrimary else SnpBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Primary Executive Metrics (2x2 Grid) ───────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Today Registered",
                        value = "${stats.todayRegistered}",
                        subtitle = "New Complaints",
                        icon = Icons.Default.FiberNew,
                        cardBg = Color(0xFFEFF6FF), // Soft Rich Blue
                        borderColor = Color(0xFFBFDBFE),
                        iconBg = Color(0xFFDBEAFE),
                        iconTint = Color(0xFF1D4ED8),
                        valueColor = Color(0xFF1E40AF),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Pending Action",
                        value = "${stats.pendingComplaints}",
                        subtitle = "Under Inspection",
                        icon = Icons.Default.PendingActions,
                        cardBg = Color(0xFFFFFBEB), // Soft Warm Amber
                        borderColor = Color(0xFFFDE68A),
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        valueColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "In Progress",
                        value = "${stats.inProgressComplaints}",
                        subtitle = "Assigned to Ward",
                        icon = Icons.Default.Engineering,
                        cardBg = Color(0xFFF5F3FF), // Soft Regal Purple
                        borderColor = Color(0xFFDDD6FE),
                        iconBg = Color(0xFFEDE9FE),
                        iconTint = Color(0xFF7C3AED),
                        valueColor = Color(0xFF6D28D9),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Closed Today",
                        value = "${stats.todayClosed}",
                        subtitle = "Resolved & Verified",
                        icon = Icons.Default.TaskAlt,
                        cardBg = Color(0xFFF0FDF4), // Soft Mint Green
                        borderColor = Color(0xFFBBF7D0),
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF16A34A),
                        valueColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Critical Alerts (Overdue & Escalations) ─────────────────────────
            Surface(
                shape = SnpSquareShape,
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SLA & Escalation Overview", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${stats.overdueComplaints}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SnpRed)
                            Text("SLA Overdue", fontSize = 11.sp, color = TextSecondary)
                        }
                        Box(modifier = Modifier.height(36.dp).width(1.dp).background(SnpBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${stats.escalatedComplaints}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SnpAmber)
                            Text("Escalated", fontSize = 11.sp, color = TextSecondary)
                        }
                        Box(modifier = Modifier.height(36.dp).width(1.dp).background(SnpBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${stats.monthClosed}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SnpGreen)
                            Text("Month Closed", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Recent Complaints Stream ───────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = if (selectedFilter == "Today") "Today's Citizen Complaints (${filteredTickets.size})" else "Complaints • $selectedFilter (${filteredTickets.size})",
                    actionLabel = "View All",
                    onAction = onViewAllComplaints
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredTickets.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, SnpBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Inbox,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No complaints found for $selectedFilter",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Switch filter to 'Yesterday', 'Last 7 Days', or 'All Time' to view other records.",
                                fontSize = 11.5.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    filteredTickets.take(10).forEach { ticket ->
                        val catMeta = OfficialCategoriesDirectory.getCategoryMeta(ticket.categoryId, ticket.problemDescription)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable { onTicketClick(ticket) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Line 1: Ticket Number & Ward on Left, Status Badge on Right
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = ticket.ticketNumber,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpPrimary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SnpSurfaceVariant,
                                            border = BorderStroke(0.5.dp, SnpBorder)
                                        ) {
                                            Text(
                                                text = "Ward ${ticket.wardNumber}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextSecondary,
                                                maxLines = 1,
                                                softWrap = false,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    StatusBadge(status = if (ticket.isOverdue) "OVERDUE" else ticket.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Line 2: Department Badge + Category Name
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SnpPrimaryLight
                                    ) {
                                        Text(
                                            text = catMeta.departmentCode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SnpPrimary,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = catMeta.name,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Line 3: Problem Description
                                Text(
                                    text = ticket.problemDescription,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Line 4: Citizen Name and Time
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = ticket.citizenName,
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = DateUtils.formatIsoToDisplay(ticket.registrationTime),
                                            fontSize = 10.5.sp,
                                            color = TextMuted,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

