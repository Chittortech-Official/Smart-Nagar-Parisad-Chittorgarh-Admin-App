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
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All Time") }
    val filterOptions = listOf("Today", "Yesterday", "Last 7 Days", "This Month", "All Time")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = authorityName,
            subtitle = if (authorityRole == "CHAIRMAN") "Chairman • Nagar Parishad Chittorgarh" else "Vice Chairman • Nagar Parishad Chittorgarh",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick
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
                        iconBg = SnpBlueLight,
                        iconTint = SnpBlue,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Pending Action",
                        value = "${stats.pendingComplaints}",
                        subtitle = "Under Inspection",
                        icon = Icons.Default.PendingActions,
                        iconBg = SnpAmberLight,
                        iconTint = SnpAmber,
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
                        iconBg = SnpPrimaryLight,
                        iconTint = SnpPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Closed Today",
                        value = "${stats.todayClosed}",
                        subtitle = "Resolved & Verified",
                        icon = Icons.Default.TaskAlt,
                        iconBg = SnpGreenLight,
                        iconTint = SnpGreen,
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
                    title = "Live Citizen Complaints",
                    actionLabel = "View All",
                    onAction = onViewAllComplaints
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (recentTickets.isEmpty()) {
                    Surface(
                        shape = SnpSquareShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, SnpBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No complaints registered yet.", fontSize = 13.sp, color = TextMuted)
                        }
                    }
                } else {
                    recentTickets.take(6).forEach { ticket ->
                        val catMeta = OfficialCategoriesDirectory.getCategoryMeta(ticket.categoryId, ticket.problemDescription)

                        Surface(
                            shape = SnpCardShape,
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onTicketClick(ticket) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = ticket.ticketNumber,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Ward ${ticket.wardNumber}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${catMeta.emoji} ${catMeta.name}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ticket.problemDescription,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = DateUtils.formatIsoToDisplay(ticket.registrationTime),
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                StatusBadge(status = ticket.status)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

