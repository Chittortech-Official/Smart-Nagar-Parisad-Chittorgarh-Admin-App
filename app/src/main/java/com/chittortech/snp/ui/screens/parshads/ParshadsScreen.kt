package com.chittortech.snp.ui.screens.parshads

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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

    val filteredWards = remember(wards, searchQuery) {
        if (searchQuery.isBlank()) wards
        else {
            wards.filter { w ->
                w.wardNumber.toString() == searchQuery.trim() ||
                        w.wardName.contains(searchQuery, ignoreCase = true) ||
                        (w.councillorName?.contains(searchQuery, ignoreCase = true) == true) ||
                        (w.zone?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    Scaffold(
        topBar = {
            SnpTopBar(
                title = "Parshad Directory",
                subtitle = "All 60 Wards & Elected Councillors",
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Councillor name or Ward number...", fontSize = 13.sp) },
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            if (filteredWards.isEmpty()) {
                EmptyState(message = "No councillor found.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredWards, key = { it.wardNumber }) { ward ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(SnpPrimaryLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, null, tint = SnpPrimary, modifier = Modifier.size(24.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Ward ${ward.wardNumber}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SnpPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${ward.zone ?: "Central Zone"}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = ward.councillorName ?: "Designated Parshad",
                                        fontSize = 14.sp,
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
                        }
                    }
                }
            }
        }
    }
}

