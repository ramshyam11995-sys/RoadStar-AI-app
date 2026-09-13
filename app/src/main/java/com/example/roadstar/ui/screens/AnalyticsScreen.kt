package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.AnalyticsSummary
import com.example.roadstar.data.model.Driver
import com.example.roadstar.ui.components.FleetUtilizationCard
import com.example.roadstar.ui.components.MetricCard
import com.example.roadstar.ui.components.RevenueExpenseChart
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    analytics: AnalyticsSummary,
    drivers: List<Driver>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Operational & Financial Analytics",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = NavyDark
            )
        }

        // Top Financial Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Revenue / Mile",
                    value = "$${analytics.revenuePerMileUsd}",
                    icon = Icons.Default.TrendingUp,
                    iconColor = SuccessGreen,
                    trendText = "+$0.24 margin",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Cost / Mile",
                    value = "$${analytics.costPerMileUsd}",
                    icon = Icons.Default.TrendingDown,
                    iconColor = OrangePrimary,
                    trendText = "-8% vs benchmark",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Average Fleet MPG",
                    value = "${analytics.averageFuelMpg} MPG",
                    icon = Icons.Default.LocalGasStation,
                    iconColor = InfoBlue,
                    trendText = "+0.7 with AI routing",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Avg Delivery Time",
                    value = "${analytics.averageDeliveryTimeHours} hrs",
                    icon = Icons.Default.Schedule,
                    iconColor = SuccessGreen,
                    trendText = "42 min saved/trip",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Visual Revenue vs Fuel Chart
        item {
            RevenueExpenseChart()
        }

        // Fleet Utilization Card
        item {
            FleetUtilizationCard(
                activeTrucks = 18,
                availableTrucks = 6,
                maintenanceTrucks = 3,
                utilizationPercent = analytics.fleetUtilizationPercent,
                deliverySuccessPercent = analytics.deliverySuccessRatePercent
            )
        }

        // Driver Performance Leaderboard
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Driver Performance Leaderboard", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
                        Text("Safety & Fuel Score", fontSize = 11.sp, color = SlateMuted)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    drivers.sortedByDescending { it.safetyRating }.forEachIndexed { index, driver ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (index == 0) OrangePrimary else Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index == 0) Color.White else SlateDark
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(driver.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = NavyDark)
                                    Text(driver.assignedTruckId ?: "Standby", fontSize = 11.sp, color = SlateMuted)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${driver.safetyRating}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                            }
                        }
                        if (index < drivers.size - 1) {
                            Divider(color = Color(0xFFF8FAFC))
                        }
                    }
                }
            }
        }
    }
}
