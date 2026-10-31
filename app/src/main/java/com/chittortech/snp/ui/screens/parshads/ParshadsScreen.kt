package com.chittortech.snp.ui.screens.parshads

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.R
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
                wardName = "Ward $wNum",
                councillorName = official?.councillorName ?: dbWard?.councillorName ?: "Designated Parshad",
                councillorPhone = dbWard?.councillorPhone,
                zone = null,
                keyLandmarks = null
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SnpBackground),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Party Filter Strip with Official Symbols (Scrolls naturally, NOT sticky)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("All", "All (60)", null),
                        Triple("भाजपा", "भाजपा (33)", R.drawable.ic_party_bjp),
                        Triple("कांग्रेस", "कांग्रेस (23)", R.drawable.ic_party_inc),
                        Triple("निर्दलीय", "निर्दलीय (4)", R.drawable.ic_party_ind)
                    ).forEach { (key, label, logoRes) ->
                        val isSelected = selectedPartyFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPartyFilter = key },
                            shape = SnpSquareShape,
                            leadingIcon = logoRes?.let { res ->
                                {
                                    Image(
                                        painter = painterResource(res),
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    label,
                                    fontSize = 11.sp,
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
            }

            // Compact Single-Line Search Bar (Scrolls naturally, NOT sticky)
            item {
                Surface(
                    shape = SnpSquareShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, SnpBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .height(38.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SnpPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search by Ward # or Parshad Name...",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (filteredWards.isEmpty()) {
                item {
                    EmptyState(message = "No councillor found matching your search.")
                }
            } else {
                items(filteredWards, key = { it.wardNumber }) { ward ->
                        val official = OfficialParshadDirectory.getParshad(ward.wardNumber)

                        val (partyBg, partyFg, partyLogo) = when (official?.party) {
                            "भाजपा" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), R.drawable.ic_party_bjp)
                            "कांग्रेस" -> Triple(Color(0xFFE1F5FE), Color(0xFF0277BD), R.drawable.ic_party_inc)
                            "निर्दलीय" -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), R.drawable.ic_party_ind)
                            else -> Triple(SnpPrimaryLight, SnpPrimary, R.drawable.ic_party_bjp)
                        }

                        Surface(
                            shape = SnpSquareShape,
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Top row: Ward number + Party Badge with Symbol + Category Badge
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
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(SnpSquareShape)
                                                .background(partyBg)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(partyLogo),
                                                contentDescription = official?.party,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
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
                                                .clip(SnpSquareShape)
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

                                Spacer(modifier = Modifier.height(10.dp))

                                // Main row: Party Symbol Emblem Avatar + Councillor Name
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(partyBg)
                                            .padding(9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(partyLogo),
                                            contentDescription = official?.party,
                                            modifier = Modifier.fillMaxSize()
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
