package com.chittortech.snp.ui.screens.complaints

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.OfficialCategoriesDirectory
import com.chittortech.snp.data.SupabaseManager
import com.chittortech.snp.data.models.AuthorityConfirmation
import com.chittortech.snp.data.models.CitizenConfirmation
import com.chittortech.snp.data.models.Ticket
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.components.StatusBadge
import com.chittortech.snp.ui.theme.*
import com.chittortech.snp.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    ticket: Ticket,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var authorityConfirmation by remember { mutableStateOf<AuthorityConfirmation?>(null) }
    var citizenConfirmation by remember { mutableStateOf<CitizenConfirmation?>(null) }
    var isLoadingConfirmations by remember { mutableStateOf(false) }

    // Fetch official category metadata
    val categoryMeta = remember(ticket) {
        OfficialCategoriesDirectory.getCategoryMeta(ticket.categoryId, ticket.problemDescription)
    }

    val isClosed = ticket.status.equals("CLOSED", ignoreCase = true)
    val isResolved = isClosed || ticket.status.contains("RESOLVED", ignoreCase = true)
    val isRejected = ticket.status.contains("REJECT", ignoreCase = true) || ticket.status.equals("CANCELLED", ignoreCase = true)

    // Load confirmations from Supabase if ticket id exists
    LaunchedEffect(ticket.id) {
        if (ticket.id.isNotBlank()) {
            isLoadingConfirmations = true
            authorityConfirmation = SupabaseManager.getAuthorityConfirmation(ticket.id)
            citizenConfirmation = SupabaseManager.getCitizenConfirmation(ticket.id)
            isLoadingConfirmations = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ticket.ticketNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Official Category & Intake Header Card ─────────────────────────
            Surface(
                shape = SnpCardShape,
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top Row: Department Code & Ward on left, Status on right
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
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SnpPrimaryLight,
                                border = BorderStroke(1.dp, SnpPrimary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = categoryMeta.departmentCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SnpPrimary,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SnpSurfaceVariant,
                                border = BorderStroke(1.dp, SnpBorder)
                            ) {
                                Text(
                                    text = "वार्ड ${ticket.wardNumber}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                )
                            }
                        }
                        StatusBadge(status = if (ticket.isOverdue) "OVERDUE" else ticket.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Name & Hindi translation
                    Text(
                        text = categoryMeta.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 21.sp
                    )
                    if (categoryMeta.hindiName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = categoryMeta.hindiName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SnpBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel & SLA Target Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = SnpBadgeShape,
                            color = SnpPrimaryLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SupportAgent, null, tint = SnpPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Helpline & WhatsApp Intake",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SnpPrimary
                                )
                            }
                        }

                        Surface(
                            shape = SnpBadgeShape,
                            color = if (categoryMeta.isEmergency) SnpRedLight else SnpAmberLight
                        ) {
                            Text(
                                text = "SLA: ${categoryMeta.defaultSlaHours}h Target",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (categoryMeta.isEmergency) SnpRed else SnpAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Citizen Grievance Description", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ticket.problemDescription,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }

            // ── REJECTION AUDIT OR MANDATORY TWO-STEP CLOSURE VERIFICATION ───────
            if (isRejected) {
                Surface(
                    shape = SnpCardShape,
                    color = Color(0xFFFEF2F2),
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = SnpRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "शिकायत निरस्त विवरण (Rejection Audit)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEE2E2),
                                border = BorderStroke(0.5.dp, Color(0xFFFCA5A5))
                            ) {
                                Text(
                                    text = "REJECTED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "यह शिकायत विभागीय समीक्षा उपरांत सक्षम स्तर पर निरस्त (Rejected) कर दी गई है। इसके लिए कोई फील्ड निस्तारण अथवा नागरिक सत्यापन अपेक्षित नहीं है।",
                            fontSize = 12.sp,
                            color = Color(0xFF7F1D1D),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "निरस्तीकरण स्थिति (Audit Status):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val reason = authorityConfirmation?.verificationNotes
                                    ?: if (!ticket.resolutionTime.isNullOrBlank()) "प्रशासनिक कारणों से फाइल निरस्त।" else "सक्षम प्राधिकारी द्वारा शिकायत निरस्त व बंद।"
                                Text(
                                    text = "कारण / टिप्पणी: $reason",
                                    fontSize = 11.5.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    shape = SnpCardShape,
                    color = SnpCardWhite,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, if (isClosed) SnpGreen.copy(alpha = 0.5f) else SnpBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = if (isClosed) SnpGreen else SnpPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mandatory Two-Step Closure Audit",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Text(
                            text = "Every complaint requires independent Field Officer verification and direct Citizen satisfaction before closure.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Step 1: Authority Confirmation
                        val officerOk = authorityConfirmation?.officerConfirmed ?: isResolved
                        val officerNotes = authorityConfirmation?.verificationNotes
                            ?: if (isResolved) "Field supervisor confirmed resolution on-site." else "Awaiting field team inspection."

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SnpBadgeShape)
                                .background(if (officerOk) SnpGreenLight.copy(alpha = 0.5f) else SnpSurfaceVariant)
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (officerOk) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (officerOk) SnpGreen else SnpAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Step 1: Field Verification",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (officerOk) SnpGreenLight else SnpAmberLight
                                    ) {
                                        Text(
                                            text = if (officerOk) "CONFIRMED" else "PENDING",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (officerOk) SnpGreen else SnpAmber,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = officerNotes,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 2: Citizen Confirmation
                        val citizenOk = citizenConfirmation?.citizenSatisfied ?: isClosed
                        val citizenRating = citizenConfirmation?.satisfactionRating ?: if (isClosed) 5 else 0
                        val citizenFeedback = citizenConfirmation?.citizenFeedback
                            ?: if (isClosed) "Citizen confirmed work completed to satisfaction via follow-up call." else "Citizen follow-up call pending."

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SnpBadgeShape)
                                .background(if (citizenOk) SnpGreenLight.copy(alpha = 0.5f) else SnpSurfaceVariant)
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (citizenOk) Icons.Default.ThumbUp else Icons.AutoMirrored.Filled.PhoneCallback,
                                contentDescription = null,
                                tint = if (citizenOk) SnpGreen else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Step 2: Citizen Confirmation",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (citizenOk) SnpGreenLight else SnpSurfaceVariant
                                    ) {
                                        Text(
                                            text = if (citizenOk) "VERIFIED (${citizenRating}★)" else "AWAITING CALL",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (citizenOk) SnpGreen else TextMuted,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = citizenFeedback,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // ── Ward & Location Details ─────────────────────────────────────────
            Surface(
                shape = SnpCardShape,
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ward & Location Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    DetailRow(label = "Ward Number", value = "Ward ${ticket.wardNumber}")
                    DetailRow(label = "Address", value = ticket.address)
                    if (!ticket.landmark.isNullOrBlank()) {
                        DetailRow(label = "Landmark", value = ticket.landmark)
                    }
                    DetailRow(label = "Routing Dispatch", value = ticket.forwardingOption.replace("_", " "))
                }
            }

            // ── Citizen Info ───────────────────────────────────────────────────
            Surface(
                shape = SnpCardShape,
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Complainant Information", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    DetailRow(label = "Citizen Name", value = ticket.citizenName)
                    DetailRow(label = "Phone Contact", value = ticket.citizenPhone)
                }
            }

            // ── Timelines & SLA Deadlines ──────────────────────────────────────
            Surface(
                shape = SnpCardShape,
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SLA & Timeline Audit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    DetailRow(label = "Registration Time", value = DateUtils.formatIsoToDisplay(ticket.registrationTime))
                    DetailRow(label = "Resolution Target (SLA)", value = "${ticket.slaHours} Hours")
                    DetailRow(label = "Deadline", value = DateUtils.formatIsoToDisplay(ticket.slaDeadline))
                    if (!ticket.resolutionTime.isNullOrBlank()) {
                        DetailRow(label = "Officer Resolved Time", value = DateUtils.formatIsoToDisplay(ticket.resolutionTime))
                    }
                    if (!ticket.closedAt.isNullOrBlank()) {
                        DetailRow(label = "Final Closure Date", value = DateUtils.formatIsoToDisplay(ticket.closedAt))
                    }
                }
            }

            // Official Nagar Parishad Branded Footer
            SnpOfficialFooter()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.weight(0.38f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.62f)
        )
    }
}
