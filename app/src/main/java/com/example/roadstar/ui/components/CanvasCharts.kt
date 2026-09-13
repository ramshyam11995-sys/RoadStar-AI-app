package com.example.roadstar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun RevenueExpenseChart(
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val revenues = listOf(18.5f, 22.4f, 28.0f, 24.2f, 31.5f, 19.0f, 14.8f) // in $k
    val fuelCosts = listOf(3.6f, 4.2f, 5.1f, 4.8f, 6.2f, 3.8f, 2.9f) // in $k
    val maxVal = 35.0f

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revenue vs Fuel Expense",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDark
                    )
                    Text(
                        text = "Past 7 Days (Fleet Aggregation)",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }

                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(OrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revenue", fontSize = 11.sp, color = SlateDark, fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(NavyDark)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fuel", fontSize = 11.sp, color = SlateDark, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val barWidth = 14.dp.toPx()
                val gap = 4.dp.toPx()
                val totalWidth = size.width
                val chartHeight = size.height - 24.dp.toPx()
                val stepX = totalWidth / days.size

                // Draw horizontal grid lines
                for (i in 1..3) {
                    val y = chartHeight * (i / 4f)
                    drawLine(
                        color = Color(0xFFF1F5F9),
                        start = Offset(0f, y),
                        end = Offset(totalWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw bars for each day
                days.forEachIndexed { index, _ ->
                    val centerX = (index * stepX) + (stepX / 2f)

                    val revHeight = (revenues[index] / maxVal) * chartHeight
                    val fuelHeight = (fuelCosts[index] / maxVal) * chartHeight

                    // Revenue Bar (Orange)
                    drawRoundRect(
                        color = OrangePrimary,
                        topLeft = Offset(centerX - barWidth - (gap / 2f), chartHeight - revHeight),
                        size = Size(barWidth, revHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Fuel Bar (Navy)
                    drawRoundRect(
                        color = NavyDark,
                        topLeft = Offset(centerX + (gap / 2f), chartHeight - fuelHeight),
                        size = Size(barWidth, fuelHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }

            // Days Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateMuted
                    )
                }
            }
        }
    }
}

@Composable
fun FleetUtilizationCard(
    activeTrucks: Int,
    availableTrucks: Int,
    maintenanceTrucks: Int,
    utilizationPercent: Int,
    deliverySuccessPercent: Double,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fleet Utilization & Health",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NavyDark
                )
                Text(
                    text = "$utilizationPercent% Active",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = OrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-segment progress bar
            val total = (activeTrucks + availableTrucks + maintenanceTrucks).coerceAtLeast(1).toFloat()
            val activeFrac = activeTrucks / total
            val availFrac = availableTrucks / total
            val maintFrac = maintenanceTrucks / total

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                val w = size.width
                val h = size.height

                val activeW = w * activeFrac
                val availW = w * availFrac
                val maintW = w * maintFrac

                drawRect(color = OrangePrimary, topLeft = Offset(0f, 0f), size = Size(activeW, h))
                drawRect(color = InfoBlue, topLeft = Offset(activeW, 0f), size = Size(availW, h))
                drawRect(color = WarningAmber, topLeft = Offset(activeW + availW, 0f), size = Size(maintW, h))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segment Breakdown Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(OrangePrimary))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Active: $activeTrucks", fontSize = 12.sp, color = SlateDark, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(InfoBlue))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Available: $availableTrucks", fontSize = 12.sp, color = SlateDark, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(WarningAmber))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Maint: $maintenanceTrucks", fontSize = 12.sp, color = SlateDark, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom KPI row: Delivery performance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "On-Time Delivery Success Rate",
                    fontSize = 12.sp,
                    color = SlateMuted
                )
                Text(
                    text = "$deliverySuccessPercent%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            }
        }
    }
}
