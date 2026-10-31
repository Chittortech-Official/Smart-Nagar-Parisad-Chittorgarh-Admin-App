package com.chittortech.snp.ui.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.R
import com.chittortech.snp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Application", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            // Header Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(SnpPrimaryLight)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.chittortech_logo),
                            contentDescription = "ChittorTech Emblem",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SNPCMA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnpPrimary
                    )

                    Text(
                        text = "Smart Nagar Parishad Chittorgarh Monitoring App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SnpGreenLight,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "OFFICIAL RELEASE • v1.0.0",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SnpGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Executive Purpose Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Governance & Oversight",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AboutInfoRow(
                        icon = Icons.Default.AccountBalance,
                        title = "Municipal Council",
                        value = "Nagar Parishad Chittorgarh, Rajasthan (60 Wards)"
                    )
                    AboutInfoRow(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Authorized Authorities",
                        value = "Hon. Chairman (Anil Inani)\nHon. Vice Chairman (Sudarshan Rampuria)"
                    )
                    AboutInfoRow(
                        icon = Icons.Default.Visibility,
                        title = "Security Architecture",
                        value = "Strictly Read-Only Executive Oversight. Zero mobile data modification permitted. All administrative edits restricted to official Web Portal."
                    )
                }
            }

            // Key Modules Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Key System Capabilities",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ModuleBullet(title = "Live Grievance Tracking", desc = "Real-time sync of all citizen complaints from registration to resolution.")
                    ModuleBullet(title = "SLA & Escalation Control", desc = "Instant notifications on SLA breaches exceeding 48h/72h deadlines.")
                    ModuleBullet(title = "60 Wards Parshad Directory", desc = "Complete directory of elected municipal council parshads by party & category.")
                    ModuleBullet(title = "Department Performance", desc = "Live resolution metrics for Sanitation, Electrical, PWD, and Water departments.")
                }
            }

            // Engineering Credits Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SnpCardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Engineered By",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ChittorTech Official",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnpPrimary
                    )
                    Text(
                        text = "Empowering Smart Civic Governance with Modern Technology Architecture.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Website: chittortech.in • Email: contact@chittortech.in",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AboutInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SnpPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = SnpPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun ModuleBullet(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(SnpPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Text(desc, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(start = 14.dp, top = 2.dp))
    }
}

