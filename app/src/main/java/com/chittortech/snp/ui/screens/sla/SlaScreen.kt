package com.chittortech.snp.ui.screens.sla

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.chittortech.snp.data.models.Ticket
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlaScreen(
    overdueTickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val activeBreaches = remember(overdueTickets) {
        overdueTickets
    }

    val filteredList = remember(activeBreaches, selectedFilter) {
        when (selectedFilter) {
            "Escalated" -> activeBreaches.filter { it.isEscalated }
            "In Progress" -> activeBreaches.filter { it.status == "IN_PROGRESS" }
            "Pending Review" -> activeBreaches.filter { it.status == "REGISTERED" }
            else -> activeBreaches
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SLA & Escalation Monitor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${activeBreaches.size} Live Breaches Requiring Oversight", fontSize = 11.sp, color = SnpRed)
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
            // Executive KPI Header
            Surface(
                color = SnpCardWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("${activeBreaches.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SnpRed)
                        Text("Total Breaches", fontSize = 10.sp, color = TextMuted)
                    }
                    Box(modifier = Modifier.height(28.dp).width(1.dp).background(SnpBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        val escalatedCount = activeBreaches.count { it.isEscalated }
                        Text("$escalatedCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                        Text("Chairman Desk", fontSize = 10.sp, color = TextMuted)
                    }
                    Box(modifier = Modifier.height(28.dp).width(1.dp).background(SnpBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("48h+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SnpAmber)
                        Text("Avg Overdue", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All" to "All Breaches (${activeBreaches.size})",
                    "Escalated" to "Chairman Escalations",
                    "In Progress" to "Work in Progress",
                    "Pending Review" to "Unassigned"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        shape = SnpSquareShape,
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SnpRed,
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    EmptyState(icon = Icons.Default.HourglassEmpty, message = "कोई एसएलए उल्लंघन (SLA Breach) लंबित नहीं है।")
                    Spacer(modifier = Modifier.weight(1f))
                    SnpOfficialFooter()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList, key = { it.id.ifEmpty { it.ticketNumber } }) { ticket ->
                        Surface(
                            shape = SnpSquareShape,
                            color = SnpCardWhite,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, SnpRed.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTicketClick(ticket) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Escalation Tag Header
                                if (ticket.isEscalated) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(SnpSquareShape)
                                            .background(Color(0xFFFEF2F2))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PriorityHigh, null, tint = SnpRed, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "ESCALATED TO CHAIRMAN DESK • ACTION PENDING",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpRed
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ticket.ticketNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SnpRed)
                                    Surface(shape = SnpSquareShape, color = SnpRedLight) {
                                        Text(
                                            "OVERDUE SLA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpRed,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = ticket.problemDescription,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = SnpBorder.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Ward ${ticket.wardNumber} • Citizen: ${ticket.citizenName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                        Text(
                                            "Registered: ${DateUtils.formatToShortDate(ticket.registrationTime)}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Surface(
                                        shape = SnpSquareShape,
                                        color = SnpPrimaryLight,
                                        modifier = Modifier.clickable { onTicketClick(ticket) }
                                    ) {
                                        Text(
                                            "View Details →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item(key = "snp_official_footer") {
                        SnpOfficialFooter()
                    }
                }
            }
        }
    }
}
