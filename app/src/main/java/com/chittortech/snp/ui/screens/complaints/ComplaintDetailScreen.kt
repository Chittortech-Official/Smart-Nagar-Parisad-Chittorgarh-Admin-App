package com.chittortech.snp.ui.screens.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.models.Ticket
import com.chittortech.snp.ui.components.StatusBadge
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    ticket: Ticket,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ticket.ticketNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status", fontSize = 12.sp, color = TextMuted)
                        StatusBadge(status = ticket.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Issue Description", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(ticket.problemDescription, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
            }

            // Location & Ward
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ward & Location Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    DetailRow(label = "Ward Number", value = "Ward ${ticket.wardNumber}")
                    DetailRow(label = "Address", value = ticket.address)
                    if (!ticket.landmark.isNullOrBlank()) {
                        DetailRow(label = "Landmark", value = ticket.landmark)
                    }
                }
            }

            // Citizen Info
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Citizen Information", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    DetailRow(label = "Citizen Name", value = ticket.citizenName)
                    DetailRow(label = "Phone Contact", value = ticket.citizenPhone)
                }
            }

            // Timelines & SLA
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SLA & Timeline", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    DetailRow(label = "Registration Time", value = DateUtils.formatIsoToDisplay(ticket.registrationTime))
                    DetailRow(label = "Resolution Target (SLA)", value = "${ticket.slaHours} Hours")
                    DetailRow(label = "Deadline", value = DateUtils.formatIsoToDisplay(ticket.slaDeadline))
                    if (!ticket.closedAt.isNullOrBlank()) {
                        DetailRow(label = "Closure Date", value = DateUtils.formatIsoToDisplay(ticket.closedAt))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

