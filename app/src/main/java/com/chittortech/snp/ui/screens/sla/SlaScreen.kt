package com.chittortech.snp.ui.screens.sla

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.models.Ticket
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@Composable
fun SlaScreen(
    overdueTickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SnpTopBar(
                title = "SLA & Escalation Control",
                subtitle = "${overdueTickets.size} Breached / Overdue Tickets",
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SnpBackground)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SnpRedLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = SnpRed, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Action Required by Authority", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SnpRed)
                        Text("These complaints have surpassed standard municipal SLA deadlines.", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (overdueTickets.isEmpty()) {
                EmptyState(icon = Icons.Default.HourglassEmpty, message = "No SLA breaches! All complaints on track.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(overdueTickets, key = { it.id.ifEmpty { it.ticketNumber } }) { ticket ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpRed.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ticket.ticketNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SnpRed)
                                    Surface(shape = RoundedCornerShape(6.dp), color = SnpRedLight) {
                                        Text("OVERDUE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnpRed, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(ticket.problemDescription, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Ward ${ticket.wardNumber} • Citizen: ${ticket.citizenName}", fontSize = 11.sp, color = TextSecondary)
                                    Text("Deadline: ${DateUtils.formatToShortDate(ticket.slaDeadline)}", fontSize = 11.sp, color = SnpRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

