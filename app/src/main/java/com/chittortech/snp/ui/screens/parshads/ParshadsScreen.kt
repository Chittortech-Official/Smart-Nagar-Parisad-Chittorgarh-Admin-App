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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.R
import com.chittortech.snp.data.OfficialParshadDirectory
import com.chittortech.snp.data.models.Ward
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.components.SnpSearchBar
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParshadsScreen(
    wards: List<Ward>,
    isRefreshing: Boolean = false,
    onNotificationClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPartyFilter by remember { mutableStateOf("All") }

    // Precompute UI model to ensure ultra-fast 120Hz stutter-free scrolling
    val displayList = remember(wards, searchQuery, selectedPartyFilter) {
        val query = searchQuery.trim().lowercase()
        (1..60).mapNotNull { wNum ->
            val dbWard = wards.find { it.wardNumber == wNum }
            val official = OfficialParshadDirectory.getParshad(wNum)

            val party = official?.party ?: "Ward Council"
            val matchesParty = when (selectedPartyFilter) {
                "भाजपा" -> party == "भाजपा"
                "कांग्रेस" -> party == "कांग्रेस"
                "निर्दलीय" -> party == "निर्दलीय"
                else -> true
            }

            val councillorName = official?.councillorName ?: dbWard?.councillorName ?: "Designated Parshad"
            val nameEnglish = official?.nameEnglish ?: ""
            val category = official?.category ?: "सामान्य"
            val runnerUp = official?.runnerUp ?: "-"
            val votes = official?.votes ?: "-"
            val margin = official?.margin ?: "-"

            val cleanQuery = query.removePrefix("ward").removePrefix("w").trim()
            val matchesWardNumber = wNum.toString() == query ||
                    wNum.toString() == cleanQuery ||
                    "ward $wNum" == query ||
                    "ward$wNum" == query ||
                    "w$wNum" == query ||
                    "w $wNum" == query

            val matchesPartyText = when {
                query == "bjp" || query.startsWith("bjp") || query == "bhajpa" -> party == "भाजपा"
                query == "inc" || query == "congress" || query.startsWith("cong") -> party == "कांग्रेस"
                query == "ind" || query == "nir" || query.startsWith("indep") -> party == "निर्दलीय"
                else -> false
            }

            val matchesSearch = query.isBlank() ||
                    matchesWardNumber ||
                    councillorName.lowercase().contains(query) ||
                    nameEnglish.lowercase().contains(query) ||
                    party.lowercase().contains(query) ||
                    matchesPartyText ||
                    category.lowercase().contains(query) ||
                    runnerUp.lowercase().contains(query)

            if (matchesParty && matchesSearch) {
                val (partyBg, partyFg, partyLogo) = when (party) {
                    "भाजपा" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), R.drawable.ic_party_bjp)
                    "कांग्रेस" -> Triple(Color(0xFFE1F5FE), Color(0xFF0277BD), R.drawable.ic_party_inc)
                    "निर्दलीय" -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), R.drawable.ic_party_ind)
                    else -> Triple(SnpPrimaryLight, SnpPrimary, R.drawable.ic_party_bjp)
                }

                ParshadItemUiModel(
                    wardNumber = wNum,
                    councillorName = councillorName,
                    nameEnglish = nameEnglish,
                    councillorPhone = dbWard?.councillorPhone,
                    party = party,
                    category = category,
                    runnerUp = runnerUp,
                    votes = votes,
                    margin = margin,
                    partyBg = partyBg,
                    partyFg = partyFg,
                    partyLogo = partyLogo
                )
            } else null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = "Parshad Directory",
            subtitle = "60 Wards • BJP 33 · INC 23 · IND 4",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick
        )

        // Unified Search Bar (Matches Complaints Central Exactly)
        SnpSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search by Parshad Name, Ward, Party..."
        )

        // Party Filter Strip with Official Symbols
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
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

        Spacer(modifier = Modifier.height(4.dp))

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onRefreshClick?.invoke() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SnpBackground),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
            if (displayList.isEmpty()) {
                item(key = "empty_state") {
                    EmptyState(message = "No councillor found matching your search.")
                }
            } else {
                items(
                    items = displayList,
                    key = { it.wardNumber },
                    contentType = { "parshad_card" }
                ) { item ->
                    ParshadCard(item = item)
                }
            }

            item(key = "snp_official_footer") {
                SnpOfficialFooter()
            }
        }
    }
}
}

// ─── 120Hz Ultra Smooth Dedicated Card ───────────────────────────────────────
data class ParshadItemUiModel(
    val wardNumber: Int,
    val councillorName: String,
    val nameEnglish: String,
    val councillorPhone: String?,
    val party: String,
    val category: String,
    val runnerUp: String,
    val votes: String,
    val margin: String,
    val partyBg: Color,
    val partyFg: Color,
    val partyLogo: Int
)

@Composable
private fun ParshadCard(
    item: ParshadItemUiModel,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = SnpSquareShape,
        color = SnpCardWhite,
        border = BorderStroke(1.dp, SnpBorder),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "Ward ${item.wardNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnpPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(SnpBadgeShape)
                            .background(item.partyBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Image(
                            painter = painterResource(item.partyLogo),
                            contentDescription = item.party,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.party,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.partyFg
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(SnpBadgeShape)
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category,
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main row: Official Party Logo Avatar + Councillor Name (Hindi + English)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.5.dp, item.partyFg.copy(alpha = 0.35f)),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(item.partyLogo),
                            contentDescription = item.party,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.councillorName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (item.nameEnglish.isNotBlank() && item.nameEnglish != item.councillorName) {
                        Text(
                            text = item.nameEnglish,
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                item.councillorPhone?.let {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = SnpGreen)
                    }
                }
            }

            // Runner-up and Vote Margin
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SnpBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2nd: ${item.runnerUp}",
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Votes: ${item.votes} (${item.margin})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
