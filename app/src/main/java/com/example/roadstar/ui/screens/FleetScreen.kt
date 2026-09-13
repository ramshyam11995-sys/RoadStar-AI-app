package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.*
import com.example.roadstar.ui.components.MaintenanceRiskBadge
import com.example.roadstar.ui.components.TruckStatusBadge
import com.example.ui.theme.*

@Composable
fun FleetScreen(
    trucks: List<Truck>,
    drivers: List<Driver>,
    selectedTruck: Truck?,
    onSelectTruck: (Truck) -> Unit,
    onOpenMaintenance: (Truck) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(0) } // 0 = Trucks, 1 = Drivers & HOS
    var statusFilter by remember { mutableStateOf<TruckStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTrucks = trucks.filter { truck ->
        val matchesStatus = statusFilter == null || truck.status == statusFilter
        val matchesSearch = searchQuery.isBlank() ||
                truck.id.contains(searchQuery, ignoreCase = true) ||
                truck.model.contains(searchQuery, ignoreCase = true) ||
                (truck.assignedDriverName?.contains(searchQuery, ignoreCase = true) == true) ||
                truck.currentLocation.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Strengthened Fleet Operations Header Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Apex Commercial Fleet",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Real-time Telemetry & Cloud Firestore Sync",
                                fontSize = 10.sp,
                                color = SlateLight
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Text(
                                text = "Live",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Key Fleet KPIs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val activeCount = trucks.count { it.status == TruckStatus.ACTIVE }
                    val avgMpg = if (trucks.isNotEmpty()) String.format("%.1f", trucks.map { it.averageMpg }.average()) else "7.4"

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Active Units", fontSize = 10.sp, color = SlateLight)
                            Text("$activeCount / ${trucks.size}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Avg Economy", fontSize = 10.sp, color = SlateLight)
                            Text("$avgMpg MPG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Fleet Health", fontSize = 10.sp, color = SlateLight)
                            Text("94% Optimal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar for Quick Fleet Lookup
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Truck #, model, driver or city...", fontSize = 12.sp, color = SlateMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateMuted, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("fleet_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Section Switcher (Trucks vs Drivers)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFE2E8F0))
                .padding(4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selectedSection == 0) Color.White else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { selectedSection = 0 }
                    .testTag("tab_fleet_trucks")
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Fleet Trucks (${trucks.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedSection == 0) NavyDark else SlateMedium
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selectedSection == 1) Color.White else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { selectedSection = 1 }
                    .testTag("tab_fleet_drivers")
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Driver HOS Schedules",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedSection == 1) NavyDark else SlateMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedSection == 0) {
            // Trucks view with Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = statusFilter == null,
                        onClick = { statusFilter = null },
                        label = { Text("All Units (${trucks.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                items(TruckStatus.values()) { status ->
                    val count = trucks.count { it.status == status }
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { statusFilter = if (statusFilter == status) null else status },
                        label = { Text("${status.label} ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTrucks, key = { it.id }) { truck ->
                    TruckCardItem(
                        truck = truck,
                        isSelected = selectedTruck?.id == truck.id,
                        onSelect = { onSelectTruck(truck) },
                        onMaintenance = { onOpenMaintenance(truck) }
                    )
                }
            }
        } else {
            // Driver HOS & Duty Status View
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("FMCSA Hours of Service (11-Hour Rule Compliance)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Automatic electronic logging (ELD) prevents dispatching drivers with violation risks.", color = SlateLight, fontSize = 11.sp)
                        }
                    }
                }

                items(drivers, key = { it.id }) { driver ->
                    DriverCardItem(driver = driver)
                }
            }
        }
    }
}

@Composable
fun TruckCardItem(
    truck: Truck,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onMaintenance: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isSelected) OrangePrimary else BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("truck_card_${truck.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID, Model & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = truck.id,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = truck.model,
                            fontSize = 12.sp,
                            color = SlateMedium
                        )
                    }
                    Text(
                        text = "Driver: ${truck.assignedDriverName ?: "Unassigned (Available)"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangeDark
                    )
                }

                TruckStatusBadge(status = truck.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Telemetry Specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Current Location", fontSize = 10.sp, color = SlateMuted)
                    Text(truck.currentLocation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Fuel Level", fontSize = 10.sp, color = SlateMuted)
                    Text("${truck.fuelLevelPercent}% • ${truck.averageMpg} MPG", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fuel Bar
            LinearProgressIndicator(
                progress = { truck.fuelLevelPercent / 100f },
                color = if (truck.fuelLevelPercent > 30) OrangePrimary else ErrorRed,
                trackColor = Color(0xFFF1F5F9),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = BorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Predictive Maintenance Snapshot Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MaintenanceRiskBadge(risk = truck.maintenanceRisk, score = truck.maintenanceScore)

                OutlinedButton(
                    onClick = onMaintenance,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) ErrorRed else OrangeDark
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) Color(0xFFFECACA) else Color(0xFFFFD8BF)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("maintenance_btn_${truck.id.lowercase()}")
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Telemetry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DriverCardItem(driver: Driver) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(driver.name, fontWeight = FontWeight.Black, fontSize = 15.sp, color = NavyDark)
                    Text("Assigned: ${driver.assignedTruckId ?: "Standby Pool"}", fontSize = 12.sp, color = SlateMedium)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (driver.status) {
                        DriverStatus.ON_DUTY -> OrangeContainer
                        DriverStatus.AVAILABLE -> SuccessGreenContainer
                        DriverStatus.RESTING -> Color(0xFFF1F5F9)
                        DriverStatus.OFF_DUTY -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = driver.status.label,
                        color = when (driver.status) {
                            DriverStatus.ON_DUTY -> OrangeDark
                            DriverStatus.AVAILABLE -> Color(0xFF047857)
                            else -> SlateDark
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hours of Service Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Remaining Drive Time (11h Limit)", fontSize = 11.sp, color = SlateMuted)
                Text("${driver.hoursOfServiceRemaining} hrs left", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (driver.hoursOfServiceRemaining / 11.0).toFloat().coerceIn(0f, 1f) },
                color = OrangePrimary,
                trackColor = Color(0xFFF1F5F9),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Safety Score: ${driver.safetyRating}/5.0", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                Text("Home Hub: ${driver.homeTerminal}", fontSize = 11.sp, color = SlateMedium)
            }
        }
    }
}
