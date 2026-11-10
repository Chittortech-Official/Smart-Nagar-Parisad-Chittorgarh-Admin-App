package com.chittortech.snp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.R
import com.chittortech.snp.ui.theme.*

// ─── Status Badge ─────────────────────────────────────────────────────────────
@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when (status.uppercase()) {
        "CLOSED", "RESOLVED", "RESOLVED_AWAITING_CITIZEN" -> Triple(SnpGreenLight, SnpGreen, if (status == "CLOSED") "CLOSED" else "RESOLVED")
        "IN_PROGRESS", "IN PROGRESS" -> Triple(SnpAmberLight, SnpAmber, "IN PROGRESS")
        "OVERDUE", "REJECTED" -> Triple(SnpRedLight, SnpRed, status)
        "REGISTERED" -> Triple(SnpBlueLight, SnpBlue, "REGISTERED")
        "REOPENED" -> Triple(SnpGoldLight, SnpGold, "REOPENED")
        else -> Triple(SnpSurfaceVariant, TextSecondary, status)
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
        )
    }
}

// ─── KPI Card ─────────────────────────────────────────────────────────────────
@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: ImageVector,
    cardBg: Color = SnpCardWhite,
    borderColor: Color = SnpBorder,
    iconBg: Color = SnpPrimaryLight,
    iconTint: Color = SnpPrimary,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                fontSize = 12.5.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String = "",
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (actionLabel.isNotBlank()) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                Text(actionLabel, fontSize = 12.sp, color = SnpPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Snp Executive Top Bar ─────────────────────────────────────────────────────
@Composable
fun SnpTopBar(
    title: String,
    subtitle: String = "",
    onNotificationClick: () -> Unit = {},
    onRefreshClick: (() -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(SnpGradStart, SnpGradEnd))
            )
            .statusBarsPadding()
            .displayCutoutPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = if (onRefreshClick != null) 44.dp else 12.dp)
                .then(
                    if (onTitleClick != null) Modifier.clickable(onClick = onTitleClick) else Modifier
                )
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), maxLines = 1)
            }
        }
        if (onRefreshClick != null) {
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(36.dp)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.Inbox,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, fontSize = 13.sp, color = TextMuted)
    }
}

// ─── Snp Unified Executive Search Bar ──────────────────────────────────────────
@Composable
fun SnpSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (isFocused) 1.5.dp else 1.dp,
            color = if (isFocused) SnpPrimary else SnpBorder
        ),
        shadowElevation = 0.5.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(46.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isFocused || query.isNotEmpty()) SnpPrimary else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 13.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.5.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ─── Branded Official GarhSeva / SNP Footer ─────────────────────────────────
@Composable
fun SnpOfficialFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, SnpBorder),
            shadowElevation = 1.5.dp,
            modifier = Modifier.size(44.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.chittorgarh_emblem),
                contentDescription = "Garh-Seva Official Seal",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "गढ़-सेवा (Garh-Seva) • नगर परिषद चित्तौड़गढ़",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = SnpPrimary
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "स्वायत्त शासन विभाग (DLB) • राजस्थान सरकार",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "नागरिक शिकायत निवारण, 60 वार्ड मॉनिटरिंग एवं प्रशासनिक कमान केंद्र",
            fontSize = 10.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = "Designed & Engineered by ChittorTech Software • v2.4",
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}


