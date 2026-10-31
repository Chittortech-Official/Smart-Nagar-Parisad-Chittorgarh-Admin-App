package com.chittortech.snp.ui.screens.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.models.NotificationLog
import com.chittortech.snp.ui.components.EmptyState
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<NotificationLog>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Executive Notification Center", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .padding(16.dp)
        ) {
            if (notifications.isEmpty()) {
                EmptyState(icon = Icons.Default.NotificationsActive, message = "No recent notifications.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SnpCardWhite,
                            shadowElevation = 1.dp,
                            border = BorderStroke(1.dp, SnpBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.recipientType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SnpPrimary
                                    )
                                    Text(
                                        text = DateUtils.formatIsoToDisplay(notif.sentAt),
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = notif.messageContent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

