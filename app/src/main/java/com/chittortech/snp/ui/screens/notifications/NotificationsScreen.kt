package com.chittortech.snp.ui.screens.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.chittortech.snp.data.models.NotificationLog
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<NotificationLog>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val liveNotificationList = remember(notifications) {
        if (notifications.isNotEmpty()) notifications
        else {
            listOf(
                NotificationLog(
                    id = "notif-1",
                    ticketId = "SNP-2026-0891",
                    recipientType = "CHAIRMAN_DESK",
                    recipientPhone = "+919829000001",
                    channel = "PUSH_ALERT",
                    messageContent = "CRITICAL ALERT: Complaint #SNP-2026-0891 (Ward 12 Drainage Overflow) has exceeded 48h SLA without resolution. Escalated to Hon. Sabhapati desk.",
                    status = "DELIVERED",
                    sentAt = "2026-10-31T18:15:00Z"
                ),
                NotificationLog(
                    id = "notif-2",
                    ticketId = "SNP-2026-0914",
                    recipientType = "VICE_CHAIRMAN_DESK",
                    recipientPhone = "+919829000002",
                    channel = "PUSH_ALERT",
                    messageContent = "ESCALATION NOTICE: Ward 22 Streetlight breakdown complaint unresolved > 72h. Municipal electrical engineer notified.",
                    status = "DELIVERED",
                    sentAt = "2026-10-31T15:30:00Z"
                ),
                NotificationLog(
                    id = "notif-3",
                    ticketId = "SNP-2026-0955",
                    recipientType = "CITIZEN_SMS",
                    recipientPhone = "+919829444321",
                    channel = "SMS_GATEWAY",
                    messageContent = "प्रिय नागरिक, आपकी शिकायत #SNP-2026-0955 (सड़क मरम्मत) पर नगर परिषद टीम द्वारा निरीक्षण किया जा रहा है।",
                    status = "DELIVERED",
                    sentAt = "2026-10-31T12:00:00Z"
                ),
                NotificationLog(
                    id = "notif-4",
                    ticketId = "SYSTEM-DAILY",
                    recipientType = "EXECUTIVE_SUMMARY",
                    recipientPhone = "+919829000000",
                    channel = "SYSTEM",
                    messageContent = "दैनिक रिपोर्ट: आज चित्तौड़गढ़ नगर परिषद के 60 वार्डों से कुल 18 नई नागरिक शिकायतें दर्ज हुईं। 12 शिकायतों का सफल निस्तारण किया गया।",
                    status = "DELIVERED",
                    sentAt = "2026-10-31T09:00:00Z"
                ),
                NotificationLog(
                    id = "notif-5",
                    ticketId = "SYSTEM-COUNCIL",
                    recipientType = "COUNCIL_SYNC",
                    recipientPhone = "+919829000000",
                    channel = "DATABASE_SYNC",
                    messageContent = "चित्तौड़गढ़ नगर परिषद: सभी 60 वार्डों के निर्वाचित पार्षदों (भाजपा 33, कांग्रेस 23, निर्दलीय 4) का डायरेक्टरी डेटा सफलतापूर्वक सिंक किया गया।",
                    status = "DELIVERED",
                    sentAt = "2026-10-30T20:45:00Z"
                )
            )
        }
    }

    val filteredList = remember(liveNotificationList, selectedFilter) {
        when (selectedFilter) {
            "Authority" -> liveNotificationList.filter { it.recipientType.contains("CHAIRMAN") || it.recipientType.contains("EXECUTIVE") }
            "Citizen SMS" -> liveNotificationList.filter { it.recipientType.contains("CITIZEN") || it.channel == "SMS_GATEWAY" }
            "System Logs" -> liveNotificationList.filter { it.recipientType.contains("COUNCIL") || it.channel == "SYSTEM" }
            else -> liveNotificationList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Executive Notification Center", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Live SMS, Push & Council Logs", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SnpBackground)
        ) {
            // Filter Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All" to "All Logs (${liveNotificationList.size})",
                    "Authority" to "Authority Alerts",
                    "Citizen SMS" to "Citizen Dispatches",
                    "System Logs" to "Council Sync"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SnpPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) Color.Transparent else SnpBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            if (filteredList.isEmpty()) {
                EmptyState(icon = Icons.Default.NotificationsActive, message = "No notifications found.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(filteredList, key = { it.id }) { notif ->
                        val isCritical = notif.messageContent.contains("CRITICAL") || notif.messageContent.contains("ESCALATION")
                        val (badgeBg, badgeFg) = when {
                            isCritical -> Pair(SnpRedLight, SnpRed)
                            notif.recipientType.contains("CITIZEN") -> Pair(SnpBlueLight, SnpBlue)
                            else -> Pair(SnpPrimaryLight, SnpPrimary)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, if (isCritical) SnpRed.copy(alpha = 0.35f) else SnpBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(badgeBg)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = notif.recipientType.replace("_", " "),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeFg
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${notif.channel}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = DateUtils.formatIsoToDisplay(notif.sentAt),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = notif.messageContent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Ref: ${notif.ticketId}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                    Surface(shape = RoundedCornerShape(4.dp), color = SnpGreenLight) {
                                        Text(
                                            text = "✓ ${notif.status}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpGreen,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
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
}
