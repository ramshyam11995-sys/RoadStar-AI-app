package com.example.roadstar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.*
import com.example.roadstar.ui.components.*
import com.example.ui.theme.*
import com.example.roadstar.ui.viewmodel.AppScreen
import com.example.roadstar.ui.viewmodel.RoadStarUiState

@Composable
fun DashboardScreen(
    uiState: RoadStarUiState,
    loads: List<Load>,
    trucks: List<Truck>,
    analytics: AnalyticsSummary,
    onNavigate: (AppScreen) -> Unit,
    onSelectLoad: (Load) -> Unit,
    onOpenAiMatch: (Load) -> Unit,
    onOpenMaintenance: (Truck) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTrucksCount = trucks.count { it.status == TruckStatus.ACTIVE }
    val availableTrucksCount = trucks.count { it.status == TruckStatus.AVAILABLE }
    val maintenanceTrucksCount = trucks.count { it.status == TruckStatus.MAINTENANCE || it.maintenanceRisk == MaintenanceRisk.CRITICAL }
    val activeLoadsCount = loads.count { it.status == LoadStatus.IN_TRANSIT || it.status == LoadStatus.ASSIGNED }
    val availableLoads = loads.filter { it.status == LoadStatus.AVAILABLE }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner & Date
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Fleet Operations Hub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Midwest & National Freight Network • Live",
                            fontSize = 12.sp,
                            color = SlateLight
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = OrangePrimary
                    ) {
                        Text(
                            text = "AI Engine Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Key Logistics Metrics Grid (Row 1: Active Trucks, Available Trucks)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Active Trucks",
                    value = "$activeTrucksCount Units",
                    icon = Icons.Default.LocalShipping,
                    iconColor = OrangePrimary,
                    trendText = "84% Utilization",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f).testTag("metric_active_trucks")
                )
                MetricCard(
                    title = "Available Trucks",
                    value = "$availableTrucksCount Ready",
                    icon = Icons.Default.CheckCircle,
                    iconColor = SuccessGreen,
                    trendText = "Ready for dispatch",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f).testTag("metric_available_trucks")
                )
            }
        }

        // Key Logistics Metrics Grid (Row 2: Active Loads, Deliveries Today)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Active Loads",
                    value = "$activeLoadsCount Freight",
                    icon = Icons.Default.Inventory2,
                    iconColor = InfoBlue,
                    trendText = "${availableLoads.size} Unmatched",
                    isPositiveTrend = false,
                    modifier = Modifier.weight(1f).testTag("metric_active_loads")
                )
                MetricCard(
                    title = "Deliveries Today",
                    value = "${analytics.deliveriesTodayCount} Total",
                    icon = Icons.Default.DoneAll,
                    iconColor = SuccessGreen,
                    trendText = "98.4% On-Time",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f).testTag("metric_deliveries_today")
                )
            }
        }

        // Key Logistics Metrics Grid (Row 3: Revenue, Fuel Cost, Maintenance)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Gross Revenue",
                    value = "$142,850",
                    icon = Icons.Default.AttachMoney,
                    iconColor = SuccessGreen,
                    trendText = "+14.8% vs last wk",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Fuel Expenses",
                    value = "$28,420",
                    icon = Icons.Default.LocalGasStation,
                    iconColor = WarningAmber,
                    trendText = "-18% with AI Eco",
                    isPositiveTrend = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Fleet Utilization Progress & On-Time Performance Card
        item {
            FleetUtilizationCard(
                activeTrucks = activeTrucksCount,
                availableTrucks = availableTrucksCount,
                maintenanceTrucks = maintenanceTrucksCount,
                utilizationPercent = analytics.fleetUtilizationPercent,
                deliverySuccessPercent = analytics.deliverySuccessRatePercent
            )
        }

        // Revenue vs Fuel Visual Chart
        item {
            RevenueExpenseChart()
        }

        // AI Priority Action Banner (Highlighting Load #204 for Demo Flow)
        item {
            val keyLoad = loads.find { it.id == "LD-204" }
            if (keyLoad != null && keyLoad.status == LoadStatus.AVAILABLE) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD8BF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Load Recommendation",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = OnOrangeContainer
                                )
                            }
                            Text(
                                text = "96% Match",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = OrangeDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${keyLoad.pickupLocation.split(",").first()} → ${keyLoad.deliveryLocation.split(",").first()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Truck #104 (Dave Miller) is closest (12 mi) and ready to haul ${keyLoad.cargoWeightLbs} lbs Reefer freight ($4,850).",
                            fontSize = 12.sp,
                            color = SlateDark,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onSelectLoad(keyLoad)
                                onOpenAiMatch(keyLoad)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("ai_match_demo_cta")
                        ) {
                            Text("Open AI Load Matcher →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Maintenance Alert Card (Truck #415 & Truck #104)
        item {
            val criticalTruck = trucks.find { it.maintenanceRisk == MaintenanceRisk.CRITICAL } ?: trucks.first()
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Predictive Maintenance Alert", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ErrorRed)
                        }
                        Text("3 Alerts Pending", fontSize = 11.sp, color = SlateMuted)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${criticalTruck.model} (${criticalTruck.id})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyDark
                    )
                    Text(
                        text = criticalTruck.maintenanceAlert ?: "Inspection overdue.",
                        fontSize = 12.sp,
                        color = SlateDark,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { onOpenMaintenance(criticalTruck) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("View AI Diagnostic Telemetry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
