package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import com.example.roadstar.data.model.MaintenanceRisk
import com.example.roadstar.data.model.Truck
import com.example.roadstar.ui.components.MaintenanceRiskBadge
import com.example.ui.theme.*

@Composable
fun MaintenanceModal(
    truck: Truck,
    aiInsightText: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onScheduleService: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) ErrorRedContainer else OrangeContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = null,
                                    tint = if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) ErrorRed else OrangePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Predictive Maintenance", fontWeight = FontWeight.Black, fontSize = 16.sp, color = NavyDark)
                                Text("${truck.model} (${truck.id})", fontSize = 12.sp, color = SlateMedium)
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Health Score & Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MaintenanceRiskBadge(risk = truck.maintenanceRisk, score = truck.maintenanceScore)
                        Text(
                            text = "Odometer: ${truck.mileage.toString().reversed().chunked(3).joinToString(",").reversed()} mi",
                            fontSize = 11.sp,
                            color = SlateMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Diagnostic Component Telemetry Grid (Brakes, Engine, Tires, Oil)
                    Text("Component Telemetry Sensors", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TelemetryRow(
                                name = "Brake Lining Wear",
                                status = if (truck.id == "TRK-104") "3.2mm (Alert: Replace in 500 mi)" else "11.4mm (Nominal)",
                                isWarning = truck.id == "TRK-104"
                            )
                            TelemetryRow(
                                name = "DPF Particulate / Engine",
                                status = if (truck.id == "TRK-415") "4.8 psi (Regeneration Overdue)" else "0.8 psi (Clean)",
                                isWarning = truck.id == "TRK-415"
                            )
                            TelemetryRow(
                                name = "Tire Pressure & Tread (TPMS)",
                                status = "104 PSI across all 18 positions (Good)",
                                isWarning = false
                            )
                            TelemetryRow(
                                name = "Engine Oil & Viscosity",
                                status = "91% Life remaining • 15W-40 Synthetic",
                                isWarning = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Predictive Telemetry Insight Box
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) Color(0xFFFEF2F2) else Color(0xFFFFF7ED)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) Color(0xFFFECACA) else Color(0xFFFFD8BF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Diagnostic Recommendation",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OrangeDark
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally),
                                    color = OrangePrimary
                                )
                            } else {
                                Text(
                                    text = aiInsightText.ifBlank {
                                        truck.maintenanceAlert ?: "Sensors report vehicle is safe for highway haulage."
                                    },
                                    fontSize = 12.sp,
                                    color = NavyDark,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onScheduleService,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("schedule_service_button")
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Schedule Preventive Service Bay", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("Close Telemetry", color = SlateDark, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryRow(name: String, status: String, isWarning: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isWarning) ErrorRed else SuccessGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(name, fontSize = 12.sp, color = SlateDark, fontWeight = FontWeight.Medium)
        }
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = if (isWarning) FontWeight.Bold else FontWeight.Normal,
            color = if (isWarning) ErrorRed else SlateMedium
        )
    }
}
