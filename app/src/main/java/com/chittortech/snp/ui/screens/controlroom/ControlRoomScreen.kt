package com.chittortech.snp.ui.screens.controlroom

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.ui.theme.*

data class ControlRoomContact(
    val title: String,
    val hindiTitle: String,
    val designation: String,
    val phoneNumber: String,
    val formattedPhone: String,
    val serviceBadge: String,
    val badgeBg: Color,
    val badgeFg: Color,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val timing: String = "24x7 Emergency Service"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlRoomScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val contacts = listOf(
        ControlRoomContact(
            title = "Municipal Central Control Room",
            hindiTitle = "नगर परिषद केंद्रीय नियंत्रण कक्ष",
            designation = "24x7 Official Command Hotline",
            phoneNumber = "01472240212",
            formattedPhone = "01472-240212",
            serviceBadge = "24x7 HELPLINE",
            badgeBg = Color(0xFFEFF6FF),
            badgeFg = Color(0xFF1E40AF),
            icon = Icons.Default.SupportAgent,
            iconBg = Color(0xFFDBEAFE),
            iconTint = Color(0xFF1D4ED8),
            timing = "Round-the-clock Municipal Dispatch"
        ),
        ControlRoomContact(
            title = "Emergency Fire Brigade",
            hindiTitle = "दमकल केंद्र / अग्निशमन सेवा",
            designation = "Fire & Disaster Rescue Station",
            phoneNumber = "01472241101",
            formattedPhone = "01472-241101 (Emergency: 101)",
            serviceBadge = "EMERGENCY RESCUE",
            badgeBg = Color(0xFFFEF2F2),
            badgeFg = Color(0xFFDC2626),
            icon = Icons.Default.LocalFireDepartment,
            iconBg = Color(0xFFFEE2E2),
            iconTint = Color(0xFFDC2626),
            timing = "24x7 Immediate Deployment"
        ),
        ControlRoomContact(
            title = "Chief Sanitation In-charge",
            hindiTitle = "मुख्य सफाई निरीक्षक (स्वास्थ्य शाखा)",
            designation = "Waste Collection, Dead Animals & Tippers",
            phoneNumber = "+919414187654",
            formattedPhone = "+91 94141 87654",
            serviceBadge = "SANITATION & HEALTH",
            badgeBg = Color(0xFFF0FDF4),
            badgeFg = Color(0xFF15803D),
            icon = Icons.Default.CleaningServices,
            iconBg = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A),
            timing = "Morning 6:00 AM - Evening 8:00 PM"
        ),
        ControlRoomContact(
            title = "AEN / JEN Light & Electrical",
            hindiTitle = "सहायक / कनिष्ठ अभियंता (विद्युत शाखा)",
            designation = "Streetlights, High-Mast & Phase Line",
            phoneNumber = "+919414234567",
            formattedPhone = "+91 94142 34567",
            serviceBadge = "STREETLIGHTS",
            badgeBg = Color(0xFFFFFBEB),
            badgeFg = Color(0xFFB45309),
            icon = Icons.Default.Lightbulb,
            iconBg = Color(0xFFFEF3C7),
            iconTint = Color(0xFFD97706),
            timing = "Official Hours & Night Shift Hotline"
        ),
        ControlRoomContact(
            title = "AEN / JEN Civil & Roads",
            hindiTitle = "सहायक / कनिष्ठ अभियंता (सड़क एवं निर्माण)",
            designation = "Potholes, Road Patching & Dividers",
            phoneNumber = "+919414345678",
            formattedPhone = "+91 94143 45678",
            serviceBadge = "CIVIL INFRASTRUCTURE",
            badgeBg = Color(0xFFF5F3FF),
            badgeFg = Color(0xFF6D28D9),
            icon = Icons.Default.Construction,
            iconBg = Color(0xFFEDE9FE),
            iconTint = Color(0xFF7C3AED),
            timing = "Office Hours (9:30 AM - 6:00 PM)"
        ),
        ControlRoomContact(
            title = "AEN / JEN Drains & Sewerage",
            hindiTitle = "कनिष्ठ अभियंता (नाली एवं सीवरेज प्रकोष्ठ)",
            designation = "Nullah De-silting, Slabs & Waterlogging",
            phoneNumber = "+919414456789",
            formattedPhone = "+91 94144 56789",
            serviceBadge = "DRAINS & SEWERAGE",
            badgeBg = Color(0xFFF0F9FF),
            badgeFg = Color(0xFF0369A1),
            icon = Icons.Default.Plumbing,
            iconBg = Color(0xFFE0F2FE),
            iconTint = Color(0xFF0284C7),
            timing = "24x7 Monsoon & Daily Operations"
        ),
        ControlRoomContact(
            title = "Anti-Encroachment Squad",
            hindiTitle = "अतिक्रमण निरोधक दस्ता प्रभारी",
            designation = "Footpath Clearing & Public Land Protection",
            phoneNumber = "+919414567890",
            formattedPhone = "+91 94145 67890",
            serviceBadge = "ENFORCEMENT",
            badgeBg = Color(0xFFFFF1F2),
            badgeFg = Color(0xFFBE123C),
            icon = Icons.Default.Shield,
            iconBg = Color(0xFFFFE4E6),
            iconTint = Color(0xFFE11D48),
            timing = "Daily Inspection Squad"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "City Control Room Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "कंट्रोल रूम संपर्क सूची • नगर परिषद चित्तौड़गढ़",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SnpBackground)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Header Info Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ContactPhone,
                                contentDescription = null,
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency & Official Hotlines",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap any contact to directly dial or copy number for emergency escalation.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Contacts List
            items(
                items = contacts,
                key = { it.phoneNumber }
            ) { contact ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, SnpBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Top row: Icon + Titles + Service Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(contact.iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    contact.icon,
                                    contentDescription = null,
                                    tint = contact.iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.hindiTitle,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = contact.title,
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Designation & Timing
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = contact.designation,
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = contact.badgeBg
                            ) {
                                Text(
                                    text = contact.serviceBadge,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contact.badgeFg,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SnpBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Phone Number
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = SnpPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = contact.formattedPhone,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SnpPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Responsive 50/50 Full-Width Action Buttons (Copy & Call)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Copy Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SnpSurfaceVariant,
                                border = BorderStroke(1.dp, SnpBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Phone Number", contact.phoneNumber)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "${contact.formattedPhone} copied", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Copy",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Call Helpline Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Call,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Call Helpline",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
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

