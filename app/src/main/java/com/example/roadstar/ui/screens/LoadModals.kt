package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.roadstar.data.model.*
import com.example.ui.theme.*

@Composable
fun AiMatchDialog(
    load: Load,
    recommendations: List<AiMatchRecommendation>,
    isLoading: Boolean,
    onAssign: (AiMatchRecommendation) -> Unit,
    onDismiss: () -> Unit
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
                    .padding(20.dp)
            ) {
                // Modal Header
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
                                .background(OrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OrangePrimary)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("AI Load Matcher", fontWeight = FontWeight.Black, fontSize = 17.sp, color = NavyDark)
                            Text("Optimal Truck & Driver Engine", fontSize = 11.sp, color = SlateMuted)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Load Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(load.id, fontWeight = FontWeight.Black, fontSize = 13.sp, color = OrangePrimary)
                            Text("$${load.loadValueUsd.toInt()} • ${load.cargoType.label}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${load.pickupLocation} → ${load.deliveryLocation}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyDark,
                            maxLines = 1
                        )
                        Text("Weight: ${load.cargoWeightLbs} lbs • Distance: ${load.distanceMiles} mi", fontSize = 11.sp, color = SlateMedium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "AI Ranked Matches (${recommendations.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = OrangePrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Analyzing telemetry, HOS hours & proximity...", fontSize = 12.sp, color = SlateMedium)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(recommendations) { rec ->
                            val isTopMatch = rec.matchScorePercent >= 90
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isTopMatch) Color(0xFFFFF7ED) else Color.White
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isTopMatch) OrangePrimary else BorderSubtle
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Top Row: Match Score & Truck
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = rec.truckId,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp,
                                                    color = NavyDark
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = rec.truckModel,
                                                    fontSize = 12.sp,
                                                    color = SlateMedium
                                                )
                                            }
                                            Text(
                                                text = "Driver: ${rec.driverName}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = OrangeDark
                                            )
                                        }

                                        // Match Score Badge
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isTopMatch) OrangePrimary else InfoBlue
                                        ) {
                                            Text(
                                                text = "${rec.matchScorePercent}% Match",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Reason Explanation
                                    Text(
                                        text = rec.reasonExplanation,
                                        fontSize = 12.sp,
                                        color = SlateDark,
                                        lineHeight = 17.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Metric Specs Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Deadhead: ${rec.proximityMiles} mi", fontSize = 11.sp, color = SlateMedium)
                                        Text("HOS: ${rec.driverHoursRemaining} hrs left", fontSize = 11.sp, color = SlateMedium)
                                        Text("Capacity: ${rec.capacityUtilizationPercent}%", fontSize = 11.sp, color = SlateMedium)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Assign Action Button
                                    Button(
                                        onClick = { onAssign(rec) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isTopMatch) OrangePrimary else NavyDark,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .testTag("assign_match_${rec.truckId.lowercase()}")
                                    ) {
                                        Text(
                                            text = "Assign ${rec.truckId} & ${rec.driverName.split(" ").first()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateLoadDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        pickup: String,
        delivery: String,
        pickupTime: String,
        deadline: String,
        cargoType: CargoType,
        weightLbs: Int,
        valueUsd: Double,
        truckType: String
    ) -> Unit
) {
    var pickup by remember { mutableStateOf("Chicago Hub, IL") }
    var delivery by remember { mutableStateOf("Nashville Logistics Center, TN") }
    var pickupTime by remember { mutableStateOf("Tomorrow, 08:00 AM") }
    var deadline by remember { mutableStateOf("Tomorrow, 07:00 PM") }
    var selectedCargoType by remember { mutableStateOf(CargoType.DRY_VAN) }
    var weightText by remember { mutableStateOf("28000") }
    var valueText by remember { mutableStateOf("3200") }
    var truckType by remember { mutableStateOf("Dry Van 53ft") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Post Freight Load", fontWeight = FontWeight.Black, fontSize = 18.sp, color = NavyDark)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        OutlinedTextField(
                            value = pickup,
                            onValueChange = { pickup = it },
                            label = { Text("Pickup Location") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_pickup")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = delivery,
                            onValueChange = { delivery = it },
                            label = { Text("Delivery Location") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_delivery")
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pickupTime,
                                onValueChange = { pickupTime = it },
                                label = { Text("Pickup Time") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = deadline,
                                onValueChange = { deadline = it },
                                label = { Text("Deadline") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Text("Cargo Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            CargoType.values().forEach { cType ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedCargoType == cType) OrangeContainer else Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, if (selectedCargoType == cType) OrangePrimary else BorderSubtle),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedCargoType = cType }
                                ) {
                                    Text(
                                        text = cType.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedCargoType == cType) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedCargoType == cType) OrangeDark else NavyDark,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                label = { Text("Weight (lbs)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("input_weight")
                            )
                            OutlinedTextField(
                                value = valueText,
                                onValueChange = { valueText = it },
                                label = { Text("Load Value ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("input_value")
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = truckType,
                            onValueChange = { truckType = it },
                            label = { Text("Required Truck Trailer") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val weight = weightText.toIntOrNull()
                        val value = valueText.toDoubleOrNull()
                        if (pickup.isBlank() || delivery.isBlank()) {
                            errorMessage = "Please enter pickup and delivery locations."
                        } else if (weight == null || weight <= 0) {
                            errorMessage = "Please enter a valid cargo weight in lbs."
                        } else if (value == null || value <= 0) {
                            errorMessage = "Please enter a valid load value."
                        } else {
                            onSubmit(
                                pickup,
                                delivery,
                                pickupTime,
                                deadline,
                                selectedCargoType,
                                weight,
                                value,
                                truckType
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_load")
                ) {
                    Text("Publish Freight Load", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
