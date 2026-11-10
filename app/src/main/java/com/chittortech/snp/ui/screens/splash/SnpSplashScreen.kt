package com.chittortech.snp.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.R
import kotlinx.coroutines.delay

@Composable
fun SnpSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        // Keep splash screen for 2.8 seconds so users comfortably read the slogans
        delay(2800)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSplashFinished() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // ─── Center Hero Content ─────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // 1. Official Nagar Parishad Circular Seal (100% Round)
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(2.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(155.dp)
                        .scale(scale.value)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chittorgarh_emblem),
                        contentDescription = "Official Nagar Parishad Chittorgarh Seal",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 2. App Name
                Text(
                    text = "GarhSeva",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF7A0016), // Royal Rajputana Maroon
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "नगर परिषद् चित्तौड़गढ़",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "स्मार्ट नागरिक सेवा एवं कमाण्ड सेन्टर",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Core Governance Slogan Badge (BJP Good Governance)
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFFFF7ED), // Subtle Saffron Tint
                    border = BorderStroke(1.dp, Color(0xFFFDBA74))
                ) {
                    Text(
                        text = "सबका साथ, सबका विकास, सबका विश्वास, सबका प्रयास",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC2410C), // Deep Saffron
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Governance Pillars Slogan
                Text(
                    text = "सुशासन • सेवा संकल्प • अंत्योदय कल्याण",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF15803D), // Forest Green
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "स्वच्छ, सुंदर एवं विकसित चित्तौड़गढ़",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )
            }

            // ─── Bottom Footer ────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(0.8.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = "🏛️ राजस्थान सरकार • स्वायत्त शासन विभाग",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "पारदर्शी प्रशासन • त्वरित जन-सुनवाई एवं समाधान",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
