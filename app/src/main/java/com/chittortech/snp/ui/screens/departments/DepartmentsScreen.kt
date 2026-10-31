package com.chittortech.snp.ui.screens.departments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chittortech.snp.data.models.DepartmentStats
import com.chittortech.snp.ui.components.SnpTopBar
import com.chittortech.snp.ui.theme.*

@Composable
fun DepartmentsScreen(
    departments: List<DepartmentStats>,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SnpTopBar(
                title = "Municipal Departments",
                subtitle = "Departmental Health & Performance",
                onNotificationClick = onNotificationClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SnpBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(departments, key = { it.department.id }) { ds ->
                val dept = ds.department
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SnpCardWhite,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, SnpBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SnpPrimaryLight
                                ) {
                                    Text(
                                        dept.code,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SnpPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SLA: ${dept.defaultSlaHours}h",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            dept.headOfficerPhone?.let {
                                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Phone, null, tint = SnpGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(dept.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        dept.headOfficerName?.let {
                            Text("Officer: $it", fontSize = 12.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DeptStatItem(label = "Total", value = "${ds.total}", color = SnpPrimary)
                            DeptStatItem(label = "Pending", value = "${ds.pending}", color = SnpAmber)
                            DeptStatItem(label = "Resolved", value = "${ds.closed}", color = SnpGreen)
                            DeptStatItem(label = "Overdue", value = "${ds.overdue}", color = SnpRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeptStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

