package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.Load
import com.example.roadstar.data.model.LoadStatus
import com.example.roadstar.data.model.RoutePlan
import com.example.roadstar.ui.components.InteractiveMapCanvas
import com.example.roadstar.ui.components.LoadStatusBadge
import com.example.ui.theme.*

data class DeliveryTask(
    val id: String,
    val title: String,
    val detail: String,
    var isCompleted: Boolean = false
)

@Composable
fun DriverScreen(
    currentLoad: Load?,
    routePlan: RoutePlan,
    onOpenCompleteDelivery: () -> Unit,
    onOpenEmergencyReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                DeliveryTask("T-1", "Pre-Trip Inspection", "Walkaround, tire pressures, light test", true),
                DeliveryTask("T-2", "Pre-Cool Reefer Trailer", "Set thermal controller to 34°F", true),
                DeliveryTask("T-3", "Bill of Lading Verification", "Confirm 38,000 lbs sealed pallets", true),
                DeliveryTask("T-4", "Check-in at Dallas Receiver Gate", "Bay 14 delivery slot booked for 16:30", false),
                DeliveryTask("T-5", "Consignee Signature & POD Stamp", "Capture digital proof of delivery", false)
            )
        )
    }

    var tripStatus by remember { mutableStateOf(LoadStatus.IN_TRANSIT) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver In-Cab Header Card (Inspired by GoBus mobile view)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Dave Miller", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                                Text("Truck #104 (Kenworth T680)", fontSize = 12.sp, color = SlateLight)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = OrangePrimary
                        ) {
                            Text(
                                text = "9.5h Drive Time Left",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = SlateDark, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Speed", fontSize = 10.sp, color = SlateLight)
                            Text("64 MPH (Eco Cruise)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Next Rest Area", fontSize = 10.sp, color = SlateLight)
                            Text("Love's Travel Stop (42 mi)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Reefer Temp", fontSize = 10.sp, color = SlateLight)
                            Text("34.2°F (OK)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                    }
                }
            }
        }

        // Active Freight Manifest Card
        item {
            val load = currentLoad ?: Load(
                id = "LD-204",
                title = "Fresh Organic Produce - Midwest Express",
                pickupLocation = "Chicago Hub, IL",
                deliveryLocation = "Dallas Distribution Center, TX",
                pickupDateTime = "Today, 06:00 AM",
                deliveryDeadline = "Today, 08:30 PM",
                cargoType = com.example.roadstar.data.model.CargoType.REFRIGERATED,
                cargoWeightLbs = 38000,
                loadValueUsd = 4850.0,
                requiredTruckType = "Reefer 53ft",
                status = LoadStatus.IN_TRANSIT,
                assignedTruckId = "TRK-104",
                assignedDriverName = "Dave Miller"
            )

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
                        Text(
                            text = "Active Load: ${load.id}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = NavyDark
                        )
                        LoadStatusBadge(status = tripStatus)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PICKUP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                            Text(load.pickupLocation, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            Text("Departed: ${load.pickupDateTime}", fontSize = 11.sp, color = SlateMedium)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = OrangePrimary, modifier = Modifier.padding(horizontal = 8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DELIVERY DESTINATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                            Text(load.deliveryLocation, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            Text("ETA: ${load.deliveryDeadline}", fontSize = 11.sp, color = OrangeDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status Updater Chips
                    Text("Update Trip Status:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(LoadStatus.IN_TRANSIT, LoadStatus.DELAYED, LoadStatus.DELIVERED).forEach { s ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (tripStatus == s) OrangePrimary else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { tripStatus = s }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = s.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tripStatus == s) Color.White else NavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // In-Cab Navigation Map Preview
        item {
            InteractiveMapCanvas(routePlan = routePlan)
        }

        // Delivery Checklist with Interactive Ticks
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Delivery SOP Checklist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    tasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    tasks = tasks.map {
                                        if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it
                                    }
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { checked ->
                                    tasks = tasks.map {
                                        if (it.id == task.id) it.copy(isCompleted = checked) else it
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (task.isCompleted) SlateMuted else NavyDark
                                )
                                Text(
                                    text = task.detail,
                                    fontSize = 11.sp,
                                    color = SlateMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Primary Touch Actions (Large min 48dp buttons)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Large Primary Button: Complete Delivery & Sign POD
                Button(
                    onClick = onOpenCompleteDelivery,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("driver_complete_delivery_btn")
                ) {
                    Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete Delivery (Sign POD)", fontSize = 15.sp, fontWeight = FontWeight.Black)
                }

                // Secondary Button: Report Incident / Roadside Assistance
                OutlinedButton(
                    onClick = onOpenEmergencyReport,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("report_issue_btn")
                ) {
                    Icon(Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report Delay / Roadside Issue", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
