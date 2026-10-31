package com.chittortech.snp.ui.screens.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*

@Composable
fun MoreScreen(
    authorityName: String,
    authorityRole: String,
    authorityEmail: String,
    onNavigateToSla: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SnpTopBar(
                title = "Administrative Controls",
                subtitle = "Settings, Security & Overview",
                onNotificationClick = onNotificationClick
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
            // Profile Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SnpPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalance, null, tint = SnpPrimary, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(authorityName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            if (authorityRole == "CHAIRMAN") "Sabhapati (Chairman)" else "Up-Sabhapati (Vice Chairman)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SnpPrimary
                        )
                        Text(authorityEmail, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            // Menu Options
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    MoreMenuItem(
                        icon = Icons.Default.WarningAmber,
                        title = "SLA & Escalation Monitor",
                        subtitle = "Track overdue complaints & breaches",
                        onClick = onNavigateToSla
                    )
                    Divider(color = SnpBorder)
                    MoreMenuItem(
                        icon = Icons.Default.NotificationsNone,
                        title = "Notification History",
                        subtitle = "SMS, WhatsApp and authority logs",
                        onClick = onNavigateToNotifications
                    )
                    Divider(color = SnpBorder)
                    MoreMenuItem(
                        icon = Icons.Default.Security,
                        title = "Security & Encryption",
                        subtitle = "Supabase RLS & AES-256 Cloud Protection",
                        onClick = {}
                    )
                    Divider(color = SnpBorder)
                    MoreMenuItem(
                        icon = Icons.Default.Info,
                        title = "About Application",
                        subtitle = "Smart Nagar Parishad v1.0.0 by ChittorTech",
                        onClick = {}
                    )
                }
            }

            // Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = SnpRedLight),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = SnpRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout from Command Center", color = SnpRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SnpPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
    }
}

