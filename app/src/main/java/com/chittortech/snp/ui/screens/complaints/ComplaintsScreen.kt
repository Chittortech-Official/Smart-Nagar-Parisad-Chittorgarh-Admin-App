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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.components.SnpSearchBar
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.components.StatusBadge
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen(
    tickets: List<Ticket>,
    isRefreshing: Boolean = false,
    onTicketClick: (Ticket) -> Unit,
    onNotificationClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ALL") }

    val statusTabs = listOf("ALL", "REGISTERED", "IN_PROGRESS", "CLOSED", "OVERDUE")

    val todayTicketsTotal = remember(tickets) {
        tickets.count { DateUtils.isToday(it.registrationTime) }
    }

    val filteredTickets = remember(tickets, searchQuery, selectedStatus) {
        tickets.filter { t ->
            val matchesDate = DateUtils.isToday(t.registrationTime)

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
            matchesDate && matchesSearch && matchesStatus
        }.sortedByDescending { it.registrationTime ?: "" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = "शिकायत निवारण कमान",
            subtitle = "आज की कुल $todayTicketsTotal शिकायतें (Today's Live Queue)",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick
        )

        // Unified Search Bar
        SnpSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search by Ticket ID, Ward, Citizen Name..."
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

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onRefreshClick?.invoke() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredTickets.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    EmptyState(message = if (searchQuery.isNotBlank()) "खोज के अनुसार कोई शिकायत नहीं मिली।" else "आज कोई नई शिकायत दर्ज नहीं हुई है।")
                    Spacer(modifier = Modifier.height(20.dp))

                    // Official Portal Archive Notice
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = "मोबाइल ऐप में केवल 'आज' (Today) की नागरिक शिकायतें एवं त्वरित कार्रवाई सूची प्रदर्शित होती है। पूर्व की शिकायतों, मासिक प्रगति विवरण एवं संपूर्ण ऑडिट रिकॉर्ड देखने व प्रिंट करने हेतु नगर परिषद वेब पोर्टल अथवा कंट्रोल रूम रिकॉर्ड शाखा (Portal Officer) से डेटा प्राप्त करें।",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    SnpOfficialFooter()
                    Spacer(modifier = Modifier.height(20.dp))
                }
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

                // Official Portal Archive Notice
                item(key = "portal_archive_notice") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
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
                                    text = "मोबाइल ऐप में केवल 'आज' (Today) की नागरिक शिकायतें एवं त्वरित कार्रवाई सूची प्रदर्शित होती है। पूर्व की शिकायतों, मासिक प्रगति विवरण एवं संपूर्ण ऑडिट रिकॉर्ड देखने व प्रिंट करने हेतु नगर परिषद वेब पोर्टल अथवा कंट्रोल रूम रिकॉर्ड शाखा (Portal Officer) से डेटा प्राप्त करें।",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Official Nagar Parishad Branded Footer
                item(key = "snp_official_footer") {
                    SnpOfficialFooter()
                }
            }
        }
    }
}
}

