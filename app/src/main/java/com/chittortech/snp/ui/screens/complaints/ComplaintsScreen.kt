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

// Helper to normalize Hindi digits (०-९) into standard ASCII digits (0-9)
private fun normalizeHindiDigits(input: String): String {
    return input.map { ch ->
        when (ch) {
            '०' -> '0'
            '१' -> '1'
            '२' -> '2'
            '३' -> '3'
            '४' -> '4'
            '५' -> '5'
            '६' -> '6'
            '७' -> '7'
            '८' -> '8'
            '९' -> '9'
            else -> ch
        }
    }.joinToString("")
}

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

    val statusTabs = listOf(
        "Total Registered" to "ALL",
        "Total Running" to "IN_PROGRESS",
        "Total Completed" to "CLOSED",
        "Total Rejected" to "REJECTED"
    )

    val todayTicketsTotal = remember(tickets) {
        tickets.count { DateUtils.isToday(it.registrationTime) }
    }

    val filteredTickets = remember(tickets, searchQuery, selectedStatus) {
        val rawQuery = searchQuery.trim()
        val normQuery = normalizeHindiDigits(rawQuery)
        val queryAlphaNum = normQuery.filter { it.isLetterOrDigit() }.lowercase()
        val queryDigits = normQuery.filter { it.isDigit() }
        val queryTokens = rawQuery.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }

        tickets.filter { t ->
            val matchesSearch = if (rawQuery.isBlank()) {
                true
            } else {
                val catMeta = OfficialCategoriesDirectory.getCategoryMeta(t.categoryId, t.problemDescription)
                val ticketAlphaNum = t.ticketNumber.filter { it.isLetterOrDigit() }.lowercase()
                val ticketDigits = t.ticketNumber.filter { it.isDigit() }
                val phoneDigits = t.citizenPhone.filter { it.isDigit() }

                // 1. Ticket Number match (e.g. "CGR-0015", "0015", "15", "०0१५")
                val matchesTicketNumber = t.ticketNumber.contains(rawQuery, ignoreCase = true) ||
                        t.ticketNumber.contains(normQuery, ignoreCase = true) ||
                        (queryAlphaNum.isNotEmpty() && ticketAlphaNum.contains(queryAlphaNum)) ||
                        (queryDigits.length >= 2 && ticketDigits.contains(queryDigits)) ||
                        (queryDigits.isNotEmpty() && ticketDigits.toIntOrNull() != null && ticketDigits.toIntOrNull() == queryDigits.toIntOrNull())

                // 2. Citizen Name / Username match (English & Hindi)
                val matchesCitizenName = t.citizenName.contains(rawQuery, ignoreCase = true) ||
                        t.citizenName.contains(normQuery, ignoreCase = true) ||
                        (queryTokens.isNotEmpty() && queryTokens.all { token -> t.citizenName.contains(token, ignoreCase = true) })

                // 3. Citizen Mobile Number match (English digits, Hindi digits, with/without +91)
                val matchesPhone = t.citizenPhone.contains(normQuery, ignoreCase = true) ||
                        (queryDigits.length >= 3 && phoneDigits.contains(queryDigits)) ||
                        (queryDigits.isNotEmpty() && phoneDigits.endsWith(queryDigits))

                // 4. Ward Number match ("15", "Ward 15", "वार्ड 15", "W15")
                val matchesWard = (queryDigits.isNotEmpty() && t.wardNumber.toString() == queryDigits) ||
                        normQuery.equals("ward ${t.wardNumber}", ignoreCase = true) ||
                        normQuery.equals("w${t.wardNumber}", ignoreCase = true) ||
                        rawQuery.contains("वार्ड ${t.wardNumber}", ignoreCase = true)

                // 5. Category Name & Problem Description (English and Hindi)
                val matchesCategoryOrProblem = t.problemDescription.contains(rawQuery, ignoreCase = true) ||
                        t.problemDescription.contains(normQuery, ignoreCase = true) ||
                        catMeta.name.contains(rawQuery, ignoreCase = true) ||
                        catMeta.hindiName.contains(rawQuery, ignoreCase = true) ||
                        catMeta.departmentCode.contains(rawQuery, ignoreCase = true)

                matchesTicketNumber || matchesCitizenName || matchesPhone || matchesWard || matchesCategoryOrProblem
            }

            // Status tab filtering:
            // When searching, match across all statuses so the user finds the complaint regardless of active tab
            val matchesStatus = if (rawQuery.isNotBlank()) {
                true
            } else {
                when (selectedStatus) {
                    "ALL" -> true
                    "IN_PROGRESS" -> t.status.equals("IN_PROGRESS", ignoreCase = true) || t.status.equals("REGISTERED", ignoreCase = true)
                    "CLOSED" -> t.status.equals("CLOSED", ignoreCase = true) || t.status.contains("RESOLVED", ignoreCase = true)
                    "REJECTED" -> t.status.contains("REJECT", ignoreCase = true)
                    else -> true
                }
            }

            matchesSearch && matchesStatus
        }.sortedByDescending { it.registrationTime ?: "" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = "शिकायत निवारण कमान",
            subtitle = "कुल ${tickets.size} शिकायतें • आज दर्ज: $todayTicketsTotal",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick
        )

        // Unified Search Bar (Bilingual English & Hindi: Ticket ID, Citizen Name, Mobile No, Ward)
        SnpSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "टिकट ID, नाम, मोबाइल नंबर या वार्ड खोजें..."
        )

        // 4 Primary Status Filter Tabs: Total Registered, Total Running, Total Completed, Total Rejected
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statusTabs.forEach { (label, statusKey) ->
                val isSelected = selectedStatus == statusKey
                val count = remember(tickets, statusKey) {
                    when (statusKey) {
                        "ALL" -> tickets.size
                        "IN_PROGRESS" -> tickets.count { it.status == "IN_PROGRESS" || it.status == "REGISTERED" }
                        "CLOSED" -> tickets.count { it.status == "CLOSED" || it.status.contains("RESOLVED", ignoreCase = true) }
                        "REJECTED" -> tickets.count { it.status.contains("REJECT", ignoreCase = true) }
                        else -> tickets.size
                    }
                }
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStatus = statusKey },
                    shape = SnpSquareShape,
                    label = { Text("$label ($count)", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
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
                    EmptyState(
                        message = if (searchQuery.isNotBlank()) 
                            "'$searchQuery' के लिए कोई शिकायत नहीं मिली। कृपया टिकट नंबर, नाम या मोबाइल नंबर जांचें।" 
                        else 
                            "इस श्रेणी में कोई शिकायत उपलब्ध नहीं है।"
                    )
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
                                    text = "मोबाइल ऐप में नगर परिषद चित्तौड़गढ़ की समस्त पंजीकृत शिकायतें, प्रगति विवरण एवं त्वरित कार्रवाई सूची प्रदर्शित होती है। मासिक ऑडिट रिकॉर्ड व विस्तृत प्रिंट हेतु नगर परिषद वेब पोर्टल अथवा कंट्रोल रूम रिकॉर्ड शाखा से डेटा प्राप्त करें।",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    SnpOfficialFooter()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 6.dp)
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
                                    text = "मोबाइल ऐप में नगर परिषद चित्तौड़गढ़ की समस्त पंजीकृत शिकायतें, प्रगति विवरण एवं त्वरित कार्रवाई सूची प्रदर्शित होती है। मासिक ऑडिट रिकॉर्ड व विस्तृत प्रिंट हेतु नगर परिषद वेब पोर्टल अथवा कंट्रोल रूम रिकॉर्ड शाखा से डेटा प्राप्त करें।",
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

