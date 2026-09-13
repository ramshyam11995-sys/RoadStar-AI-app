package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.roadstar.data.model.Load
import com.example.ui.theme.*

@Composable
fun DriverDeliveryDialog(
    load: Load?,
    onDismiss: () -> Unit,
    onConfirmDelivery: (loadId: String, recipientName: String) -> Unit
) {
    var recipientName by remember { mutableStateOf("Marcus Vance (Dallas Receiving Manager)") }
    var sealNumber by remember { mutableStateOf("SL-99482-OK") }
    var notes by remember { mutableStateOf("All 24 pallets inspected at 34°F. Zero damage.") }
    val points = remember { mutableStateListOf<Offset>() }

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
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
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
                                    .background(SuccessGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, tint = SuccessGreen)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Proof of Delivery (POD)", fontWeight = FontWeight.Black, fontSize = 16.sp, color = NavyDark)
                                Text("Load: ${load?.id ?: "LD-204"}", fontSize = 12.sp, color = SlateMuted)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Consignee / Recipient Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("pod_recipient_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sealNumber,
                            onValueChange = { sealNumber = it },
                            label = { Text("Cargo Seal #") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = "Dallas Dock #14",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bay Location") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Digital Touch Signature Canvas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Consignee Digital Signature:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        TextButton(
                            onClick = { points.clear() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Clear", fontSize = 12.sp, color = SlateMuted)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, BorderMedium, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    points.add(change.position)
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (points.isNotEmpty()) {
                                val path = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = NavyDark,
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }

                        if (points.isEmpty()) {
                            Text(
                                text = "Sign here with finger or stylus...",
                                color = SlateLight,
                                fontSize = 13.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // GPS Timestamp Stamp
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GPS Geofence Verified: Dallas DC (32.7767° N, 96.7970° W)",
                                fontSize = 11.sp,
                                color = SlateDark
                            )
                        }
                    }
                }

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onConfirmDelivery(load?.id ?: "LD-204", recipientName)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_pod_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Delivery & Transmit POD", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("Cancel", color = SlateMedium, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
