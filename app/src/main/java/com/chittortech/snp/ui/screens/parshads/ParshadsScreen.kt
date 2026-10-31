package com.chittortech.snp.ui.screens.parshads

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.OfficialParshadDirectory
import com.chittortech.snp.data.models.Ward
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*

@Composable
fun ParshadsScreen(
    wards: List<Ward>,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPartyFilter by remember { mutableStateOf("All") }

    // Merge Supabase wards with the official 2026 election dataset
    val fullWardsList = remember(wards) {
        (1..60).map { wNum ->
            val dbWard = wards.find { it.wardNumber == wNum }
            val official = OfficialParshadDirectory.getParshad(wNum)
            Ward(
                wardNumber = wNum,
                wardName = dbWard?.wardName ?: "Ward $wNum - Nagar Parishad Chittorgarh",
                councillorName = official?.councillorName ?: dbWard?.councillorName ?: "Designated Parshad",
                councillorPhone = dbWard?.councillorPhone,
                zone = dbWard?.zone ?: "Central Zone",
                keyLandmarks = dbWard?.keyLandmarks
            )
        }
    }

    val filteredWards = remember(fullWardsList, searchQuery, selectedPartyFilter) {
        fullWardsList.filter { w ->
            val official = OfficialParshadDirectory.getParshad(w.wardNumber)
            val matchesParty = when (selectedPartyFilter) {
                "भाजपा" -> official?.party == "भाजपा"
                "कांग्रेस" -> official?.party == "कांग्रेस"
                "निर्दलीय" -> official?.party == "निर्दलीय"
                else -> true
            }

            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isBlank() ||
                    w.wardNumber.toString() == query ||
                    (official?.councillorName?.lowercase()?.contains(query) == true) ||
                    (w.councillorName?.lowercase()?.contains(query) == true) ||
                    (official?.party?.lowercase()?.contains(query) == true) ||
                    (official?.category?.lowercase()?.contains(query) == true) ||
                    (official?.runnerUp?.lowercase()?.contains(query) == true) ||
                    w.wardName.lowercase().contains(query)

            matchesParty && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            SnpTopBar(
                title = "Parshad Directory",
                subtitle = "60 Wards • BJP 33 · INC 23 · IND 4",
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SnpBackground)
        ) {
            // Party Filter Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All" to "All (60)",
                    "भाजपा" to "भाजपा (33)",
                    "कांग्रेस" to "कांग्रेस (23)",
                    "निर्दलीय" to "निर्दलीय (4)"
                ).forEach { (key, label) ->
                    val isSelected = selectedPartyFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPartyFilter = key },
                        label = {
                            Text(
                                label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (key) {
                                "भाजपा" -> Color(0xFFE65100)
                                "कांग्रेस" -> Color(0xFF0277BD)
                                "निर्दलीय" -> Color(0xFF475569)
                                else -> SnpPrimary
                            },
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search parshad name, ward number, party...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = SnpPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = SnpPrimary,
                    unfocusedBorderColor = SnpBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (filteredWards.isEmpty()) {
                EmptyState(message = "No councillor found matching your search.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                ) {
                    items(filteredWards, key = { it.wardNumber }) { ward ->
                        val official = OfficialParshadDirectory.getParshad(ward.wardNumber)

                        val (partyBg, partyFg) = when (official?.party) {
                            "भाजपा" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
                            "कांग्रेस" -> Pair(Color(0xFFE1F5FE), Color(0xFF0277BD))
                            "निर्दलीय" -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
                            else -> Pair(SnpPrimaryLight, SnpPrimary)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Top row: Ward number + Party Badge + Category Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Ward ${ward.wardNumber}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(partyBg)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = official?.party ?: "Ward Council",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = partyFg
                                            )
                                        }
                                    }

                                    official?.category?.let { cat ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF8FAFC))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = cat,
                                                fontSize = 10.sp,
                                                color = TextMuted,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Main row: Avatar + Name + Ward Area
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(partyBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = partyFg,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = official?.councillorName ?: ward.councillorName ?: "Designated Parshad",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = ward.wardName,
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            maxLines = 1
                                        )
                                    }

                                    ward.councillorPhone?.let { phone ->
                                        IconButton(onClick = {}) {
                                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = SnpGreen)
                                        }
                                    }
                                }

                                // Runner-up and Vote Margin
                                official?.let { off ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = SnpBorder.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "2nd: ${off.runnerUp}",
                                            fontSize = 10.sp,
                                            color = TextMuted,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Text(
                                            text = "Votes: ${off.votes} (${off.margin})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary
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

