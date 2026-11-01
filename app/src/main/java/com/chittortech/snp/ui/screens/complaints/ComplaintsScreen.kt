package com.chittortech.snp.ui.screens.complaints

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.OfficialCategoriesDirectory
import com.chittortech.snp.data.models.Ticket
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.components.StatusBadge
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@Composable
fun ComplaintsScreen(
    tickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    onNotificationClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ALL") }

    val statusTabs = listOf("ALL", "REGISTERED", "IN_PROGRESS", "CLOSED", "OVERDUE")

    val filteredTickets = remember(tickets, searchQuery, selectedStatus) {
        tickets.filter { t ->
            val matchesSearch = searchQuery.isBlank() ||
                    t.ticketNumber.contains(searchQuery, ignoreCase = true) ||
                    t.citizenName.contains(searchQuery, ignoreCase = true) ||
                    t.wardNumber.toString() == searchQuery.trim() ||
                    t.problemDescription.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatus) {
                "ALL" -> true
                "OVERDUE" -> t.isOverdue
                else -> t.status.equals(selectedStatus, ignoreCase = true)
            }
            matchesSearch && matchesStatus
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = "Complaints Central",
            subtitle = "Total ${tickets.size} Registered Tickets",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick
        )
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Ticket ID, Ward, Citizen Name...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = SnpPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = SnpSquareShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = SnpPrimary,
                    unfocusedBorderColor = SnpBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Status Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusTabs.forEach { status ->
                    val isSelected = selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatus = status },
                        shape = SnpSquareShape,
                        label = { Text(status.replace("_", " "), fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredTickets.isEmpty()) {
                EmptyState(message = "No complaints found matching criteria.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = filteredTickets,
                        key = { it.id.ifEmpty { it.ticketNumber } },
                        contentType = { "complaint_ticket" }
                    ) { ticket ->
                        val catMeta = OfficialCategoriesDirectory.getCategoryMeta(ticket.categoryId, ticket.problemDescription)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTicketClick(ticket) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
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
        }
}

