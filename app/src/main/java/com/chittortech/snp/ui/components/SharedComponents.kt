package com.chittortech.snp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Box(
        modifier = modifier
            .clip(SnpBadgeShape)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

// ─── KPI Card ─────────────────────────────────────────────────────────────────
@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: ImageVector,
    iconBg: Color = SnpPrimaryLight,
    iconTint: Color = SnpPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = SnpCardShape,
        colors = CardDefaults.cardColors(containerColor = SnpCardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, SnpBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
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

