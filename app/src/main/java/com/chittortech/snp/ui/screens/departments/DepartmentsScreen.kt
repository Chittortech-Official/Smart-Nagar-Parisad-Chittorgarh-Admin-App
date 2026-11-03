package com.chittortech.snp.ui.screens.departments

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.chittortech.snp.data.models.Department
import com.chittortech.snp.data.models.DepartmentStats
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentsScreen(
    departments: List<DepartmentStats>,
    isRefreshing: Boolean = false,
    onNotificationClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Real departments from Supabase database
    val cleanDepartments = remember(departments) {
        departments.filter { ds ->
            val d = ds.department
            val code = d.code.trim().uppercase()
            val name = d.name.trim()
            val officer = d.headOfficerName.orEmpty()
            !name.contains("hh", ignoreCase = true) &&
            !name.contains("ccc", ignoreCase = true) &&
            !name.contains("[[") &&
            !name.contains("''") &&
            !officer.contains("[[") &&
            name.length >= 3 &&
            code != "HH" && code != "CC" && code != "'"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SnpBackground)
    ) {
        SnpTopBar(
            title = "Municipal Departments",
            subtitle = "6 Official City Departments • Health & SLA",
            onNotificationClick = onNotificationClick,
            onRefreshClick = onRefreshClick
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onRefreshClick?.invoke() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SnpBackground)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
            if (cleanDepartments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No departments loaded from database. Pull down to refresh.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            items(
                items = cleanDepartments,
                key = { it.department.code },
                contentType = { "dept_card" }
            ) { ds ->
                val dept = ds.department
                val config = when (dept.code.uppercase()) {
                    "SAN" -> DeptUiConfig(Icons.Default.CleaningServices, Color(0xFF0F823F), Color(0xFFE8F5E9), "स्वास्थ्य एवं स्वच्छता विभाग")
                    "ELE" -> DeptUiConfig(Icons.Default.Lightbulb, Color(0xFFD97706), Color(0xFFFFFBEB), "विद्युत एवं प्रकाश व्यवस्था")
                    "ROA" -> DeptUiConfig(Icons.Default.AddRoad, Color(0xFF334155), Color(0xFFF1F5F9), "निर्माण एवं सड़क रखरखाव")
                    "DRN" -> DeptUiConfig(Icons.Default.Plumbing, Color(0xFF0284C7), Color(0xFFE0F2FE), "नाली एवं सीवरेज प्रबंधन")
                    "WAT" -> DeptUiConfig(Icons.Default.WaterDamage, Color(0xFF0D9488), Color(0xFFCCFBF1), "जलभराव एवं आपातकालीन निकासी")
                    "ENC" -> DeptUiConfig(Icons.Default.Shield, Color(0xFFDC2626), Color(0xFFFEE2E2), "अतिक्रमण निरोधक दस्ता")
                    else -> DeptUiConfig(Icons.Default.Business, SnpPrimary, SnpPrimaryLight, "नगर परिषद शाखा")
                }

                Surface(
                    shape = SnpSquareShape,
                    color = SnpCardWhite,
                    border = BorderStroke(1.dp, SnpBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Official Municipal Seal Emblem
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = config.colorLight,
                                border = BorderStroke(1.2.dp, config.color.copy(alpha = 0.4f)),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = config.icon,
                                        contentDescription = dept.name,
                                        tint = config.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = config.hindiName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${dept.name} (${dept.code})",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            dept.headOfficerPhone?.let { phone ->
                                IconButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = "Call Head Officer",
                                        tint = SnpGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Officer-in-Charge and SLA Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "प्रभारी: ${dept.headOfficerName ?: "Designated Officer"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )

                            Surface(
                                shape = SnpBadgeShape,
                                color = config.colorLight,
                                border = BorderStroke(1.dp, config.color.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "SLA Target: ${dept.defaultSlaHours}h",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = config.color,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SnpBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Real-time KPI Stats Grid with Sober Decent Colors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DeptStatItem(
                                label = "कुल दर्ज",
                                value = "${ds.total}",
                                textColor = Color(0xFF1E40AF),
                                bgColor = Color(0xFFEFF6FF),
                                borderColor = Color(0xFFBFDBFE),
                                modifier = Modifier.weight(1f)
                            )
                            DeptStatItem(
                                label = "प्रक्रियाधीन",
                                value = "${ds.pending + ds.inProgress}",
                                textColor = Color(0xFFB45309),
                                bgColor = Color(0xFFFFFBEB),
                                borderColor = Color(0xFFFDE68A),
                                modifier = Modifier.weight(1f)
                            )
                            DeptStatItem(
                                label = "निस्तारित",
                                value = "${ds.closed}",
                                textColor = Color(0xFF15803D),
                                bgColor = Color(0xFFF0FDF4),
                                borderColor = Color(0xFFBBF7D0),
                                modifier = Modifier.weight(1f)
                            )
                            DeptStatItem(
                                label = "समय सीमा पार",
                                value = "${ds.overdue}",
                                textColor = Color(0xFFDC2626),
                                bgColor = Color(0xFFFEF2F2),
                                borderColor = Color(0xFFFECACA),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun DeptStatItem(
    label: String,
    value: String,
    textColor: Color,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.85f),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class DeptUiConfig(
    val icon: ImageVector,
    val color: Color,
    val colorLight: Color,
    val hindiName: String
)
