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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.components.StatusBadge
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authorityRole: String,
    authorityName: String,
    stats: DashboardStats,
    recentTickets: List<Ticket>,
    isRefreshing: Boolean = false,
    onViewAllComplaints: () -> Unit,
    onTicketClick: (Ticket) -> Unit,
    onNotificationClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    onHeaderClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val filteredTickets = remember(recentTickets) {
        recentTickets.filter { DateUtils.isToday(it.registrationTime) }
            .sortedByDescending { it.registrationTime ?: "" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = authorityName,
            subtitle = when (authorityRole.uppercase()) {
                "CHAIRMAN" -> "सभापति • नगर परिषद चित्तौड़गढ़"
                "VICE_CHAIRMAN" -> "उपसभापति • नगर परिषद चित्तौड़गढ़"
                "SUPER_ADMIN" -> "सिस्टम एडमिनिस्ट्रेटर • चित्तौड़टेक"
                "PORTAL_ADMIN" -> "पोर्टल एडमिनिस्ट्रेटर • नगर परिषद चित्तौड़गढ़"
                else -> "अधिशाषी अधिकारी • नगर परिषद चित्तौड़गढ़"
            },
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick,
            onTitleClick = onHeaderClick
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onRefreshClick?.invoke() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Today's Live Command Indicator ────────────────────────────────
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SnpPrimaryLight.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, SnpPrimary.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SnpGreen,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Text(
                            text = "आज का दैनिक कमान केंद्र (Today's Live Queue)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SnpPrimary
                        )
                    }
                    Text(
                        text = "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SnpGreen
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
                        title = "Total Registered",
                        value = "${stats.totalRegistered}",
                        subtitle = "New Complaints",
                        icon = Icons.Default.FiberNew,
                        cardBg = Color(0xFFFFF3E0), // Soft Kesariya / Bhagwa
                        borderColor = Color(0xFFFFCC80),
                        iconBg = Color(0xFFFFE0B2),
                        iconTint = Color(0xFFE65100),
                        valueColor = Color(0xFFD84315),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Running",
                        value = "${stats.totalRunning}",
                        subtitle = "In Progress / Active",
                        icon = Icons.Default.Engineering,
                        cardBg = Color(0xFFF5F3FF), // Soft Regal Purple
                        borderColor = Color(0xFFDDD6FE),
                        iconBg = Color(0xFFEDE9FE),
                        iconTint = Color(0xFF7C3AED),
                        valueColor = Color(0xFF6D28D9),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Total Completed",
                        value = "${stats.totalCompleted}",
                        subtitle = "Resolved & Closed",
                        icon = Icons.Default.TaskAlt,
                        cardBg = Color(0xFFF0FDF4), // Soft Mint Green
                        borderColor = Color(0xFFBBF7D0),
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF16A34A),
                        valueColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Rejected",
                        value = "${stats.totalRejected}",
                        subtitle = "Rejected / Cancelled",
                        icon = Icons.Default.Cancel,
                        cardBg = Color(0xFFFEF2F2), // Soft Light Red
                        borderColor = Color(0xFFFECACA),
                        iconBg = Color(0xFFFEE2E2),
                        iconTint = Color(0xFFDC2626),
                        valueColor = Color(0xFFB91C1C),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Recent Complaints Stream ───────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(
                    title = "आज की नागरिक शिकायतें (${filteredTickets.size})",
                    actionLabel = "सभी देखें",
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
                                "आज कोई नई शिकायत दर्ज नहीं हुई है",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "पूर्व की शिकायतों एवं अभिलेखों के लिए नगर परिषद वेब पोर्टल / कंट्रोल रूम रिकॉर्ड शाखा से संपर्क करें।",
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
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Text(
                                            text = ticket.ticketNumber,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpPrimary,
                                            maxLines = 1,
                                            softWrap = false
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

                                // Line 3: Problem Description (two to three lines with ellipsis for long text)
                                Text(
                                    text = ticket.problemDescription,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Line 4: Citizen Name, Mobile and Time
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
                                            text = if (ticket.citizenPhone.isNotBlank()) "${ticket.citizenName} • ${ticket.citizenPhone}" else ticket.citizenName,
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            // Official Portal Archive Notice
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        tint = SnpPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "विभागीय रिकॉर्ड अभिलेखागार (Portal Archive Notice)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "मोबाइल ऐप में केवल 'आज' (Today) की लाइव नागरिक शिकायतें एवं कमान डेटा प्रदर्शित होता है। पूर्व की शिकायतों, मासिक प्रगति विवरण एवं संपूर्ण ऑडिट रिकॉर्ड देखने व प्रिंट करने हेतु नगर परिषद वेब पोर्टल अथवा कंट्रोल रूम रिकॉर्ड शाखा (Portal Officer) से डेटा प्राप्त करें।",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Official Nagar Parishad Branded Footer
            SnpOfficialFooter()
        }
    }
}
}

