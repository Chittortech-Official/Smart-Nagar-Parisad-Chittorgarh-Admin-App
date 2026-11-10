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
import com.chittortech.snp.ui.components.SnpOfficialFooter
import com.chittortech.snp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "About Application",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "कमांड सेंटर एप्लिकेशन विवरण",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                windowInsets = WindowInsets.statusBars,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ─── Hero Header Card ─────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.2.dp, SnpGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dual Badges: Rajasthan Govt + ChittorTech (Large, Crisp, Unclipped)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.2.dp, SnpGold.copy(alpha = 0.7f)),
                            modifier = Modifier.size(width = 82.dp, height = 74.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.rajasthan_logo),
                                    contentDescription = "Rajasthan Government Emblem",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            border = BorderStroke(1.2.dp, SnpPrimary.copy(alpha = 0.35f)),
                            modifier = Modifier.size(width = 82.dp, height = 74.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
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
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "गढ़-सेवा (GarhSeva)",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SnpPrimary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "नगर परिषद चित्तौड़गढ़ • प्रशासनिक कमांड सेंटर",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "नगर परिषद चित्तौड़गढ़ • राजस्थान सरकार",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SnpGreenLight,
                        border = BorderStroke(1.dp, SnpGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(SnpGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "v1.0.0 (Production Build)",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SnpGreen
                            )
                        }
                    }
                }
            }

            // ─── Executive Governance & Authorities ───────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SnpPrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, null, tint = SnpPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Municipal Authorities",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "संवैधानिक एवं प्रशासनिक नेतृत्व",
                                fontSize = 10.5.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AuthorityTile(
                        designation = "माननीय सभापति (Chairman)",
                        name = "श्री अनिल इनानी (Shri Anil Inani)",
                        jurisdiction = "नगर परिषद चित्तौड़गढ़ (60 वार्ड)"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AuthorityTile(
                        designation = "माननीय उपसभापति (Vice Chairman)",
                        name = "श्री सुदर्शन रामपूरिया (Shri Sudarshan Rampuria)",
                        jurisdiction = "नगर परिषद चित्तौड़गढ़ (60 वार्ड)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Security Read-Only Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = SnpPrimary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Strictly Read-Only Executive Oversight: यह मोबाइल एप्लिकेशन केवल संवैधानिक नेतृत्व की त्वरित निगरानी हेतु पठन-मात्र मोड में है। डेटा प्रविष्टि एवं निस्तारण अधिकार केवल आधिकारिक वेब पोर्टल पर सुरक्षित हैं।",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // ─── Key System Capabilities ──────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                    Text(
                        text = "कमांड सेंटर की प्रमुख विशेषताएं",
                        fontSize = 10.5.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CapabilityItem(
                        icon = Icons.Default.People,
                        title = "60 वार्ड एवं पार्षद डायरेक्टरी",
                        subtitle = "सभी 60 वार्डों के निर्वाचित पार्षदों की सम्पूर्ण संपर्क सूची व दलवार विवरण।"
                    )
                    CapabilityItem(
                        icon = Icons.Default.Timer,
                        title = "11 आधिकारिक श्रेणियां व SLA मॉनिटरिंग",
                        subtitle = "डेड एनिमल (12h), कचरा (24h), स्ट्रीटलाइट (48h), सड़क (72h) की समय-सीमा ट्रैकिंग।"
                    )
                    CapabilityItem(
                        icon = Icons.Default.WarningAmber,
                        title = "समय-सीमा उल्लंघन व एस्केलेशन अलर्ट्स",
                        subtitle = "निर्धारित समय से अधिक पेंडिंग शिकायतों पर कमांड सेंटर को तत्काल चेतावनी।"
                    )
                    CapabilityItem(
                        icon = Icons.Default.Verified,
                        title = "2-स्टेप अनिवार्य क्लोजर बॉक्स",
                        subtitle = "फील्ड ऑफिसर फोन वेरिफिकेशन एवं नागरिक के स्टार रेटिंग फीडबैक का पारदर्शी रिकॉर्ड।"
                    )
                }
            }

            // ─── Engineering & Technology Credits (ChittorTech ONLY) ───────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, SnpBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Engineered & Maintained By",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ChittorTech",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = SnpPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SnpPrimaryLight,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = "CIVIC TECH",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SnpPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "चित्तौड़गढ़ के प्रशासनिक स्वचालन व डिजिटल सशक्तिकरण हेतु आधुनिक क्लाउड आर्किटेक्चर द्वारा निर्मित।",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    HorizontalDivider(color = SnpBorder.copy(alpha = 0.6f))

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = SnpPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Website: chittortech.in",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SnpPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Email: contact@chittortech.in",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "© 2026 ChittorTech. All rights reserved.",
                        fontSize = 10.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Official Nagar Parishad Branded Footer
            SnpOfficialFooter()

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun AuthorityTile(
    designation: String,
    name: String,
    jurisdiction: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SnpPrimaryLight.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, SnpPrimary.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = designation,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SnpPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = name,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = jurisdiction,
                fontSize = 10.5.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun CapabilityItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SnpPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = SnpPrimary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
