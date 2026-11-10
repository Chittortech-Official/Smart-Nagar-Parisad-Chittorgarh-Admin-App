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
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*

@Composable
fun MoreScreen(
    authorityName: String,
    authorityRole: String,
    authorityEmail: String,
    onNavigateToSla: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToControlRoom: () -> Unit,
    onLogout: () -> Unit,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = "Settings",
            subtitle = "Executive Controls & Administration",
            onNotificationClick = onNotificationClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SnpBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SnpPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = SnpPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = authorityName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = when (authorityRole.uppercase()) {
                                "CHAIRMAN" -> "सभापति • नगर परिषद चित्तौड़गढ़"
                                "VICE_CHAIRMAN" -> "उपसभापति • नगर परिषद चित्तौड़गढ़"
                                "SUPER_ADMIN" -> "सिस्टम एडमिनिस्ट्रेटर • चित्तौड़टेक"
                                "PORTAL_ADMIN" -> "पोर्टल एडमिनिस्ट्रेटर • नगर परिषद चित्तौड़गढ़"
                                else -> "अधिशाषी अधिकारी • नगर परिषद"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SnpPrimary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = authorityEmail,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Settings Options Card (Clean, NO Notification History)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsMenuItem(
                        icon = Icons.Default.ContactPhone,
                        iconTint = SnpPrimary,
                        title = "City Control Room Directory",
                        subtitle = "Landline, Fire Brigade & AEN/JEN Hotlines",
                        onClick = onNavigateToControlRoom
                    )
                    HorizontalDivider(color = SnpBorder.copy(alpha = 0.6f))
                    SettingsMenuItem(
                        icon = Icons.Default.Info,
                        iconTint = SnpPrimary,
                        title = "About Application",
                        subtitle = "ChittorTech • Command Center Details",
                        onClick = onNavigateToAbout
                    )
                    HorizontalDivider(color = SnpBorder.copy(alpha = 0.6f))
                    SettingsMenuItem(
                        icon = Icons.Default.HeadsetMic,
                        iconTint = Color(0xFF0D9488),
                        title = "Contact Developers",
                        subtitle = "Email, WhatsApp & Direct Phone Support",
                        onClick = onNavigateToContact
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = SnpRedLight),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SnpRed.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = SnpRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout from Command Center",
                    color = SnpRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }

            // Official Nagar Parishad Branded Footer
            SnpOfficialFooter()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextSecondary
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}
