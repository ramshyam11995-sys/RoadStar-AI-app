package com.example.roadstar.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.RoutePlan
import com.example.ui.theme.*

@Composable
fun InteractiveMapCanvas(
    routePlan: RoutePlan,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE2E8F0)) // Modern map background
            .border(1.dp, BorderMedium, RoundedCornerShape(20.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Draw decorative background road network (mimicking real map grids)
            val secondaryRoadColor = Color(0xFFF1F5F9)
            val primaryRoadColor = Color(0xFFCBD5E1)

            // Horizontal roads
            drawLine(secondaryRoadColor, Offset(0f, h * 0.25f), Offset(w, h * 0.28f), strokeWidth = 3.dp.toPx())
            drawLine(secondaryRoadColor, Offset(0f, h * 0.55f), Offset(w, h * 0.52f), strokeWidth = 2.dp.toPx())
            drawLine(secondaryRoadColor, Offset(0f, h * 0.78f), Offset(w, h * 0.82f), strokeWidth = 3.dp.toPx())

            // Diagonal roads
            drawLine(primaryRoadColor, Offset(w * 0.1f, 0f), Offset(w * 0.9f, h), strokeWidth = 4.dp.toPx())
            drawLine(secondaryRoadColor, Offset(w * 0.35f, 0f), Offset(w * 0.2f, h), strokeWidth = 3.dp.toPx())
            drawLine(secondaryRoadColor, Offset(w * 0.85f, 0f), Offset(w * 0.75f, h), strokeWidth = 2.dp.toPx())

            // River / Lake feature
            val riverPath = Path().apply {
                moveTo(w * 0.95f, 0f)
                cubicTo(w * 0.82f, h * 0.3f, w * 0.92f, h * 0.6f, w * 0.78f, h)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFBFDBFE),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Main Freight Corridor Route Path (Chicago -> Dallas)
            val startPoint = Offset(w * 0.22f, h * 0.24f) // Pickup (A)
            val midPoint1 = Offset(w * 0.36f, h * 0.44f)
            val truckPos = Offset(w * 0.48f, h * 0.52f)  // Current telemetry point
            val midPoint2 = Offset(w * 0.62f, h * 0.68f)
            val endPoint = Offset(w * 0.78f, h * 0.82f)   // Delivery (B)

            // Alternative Route (Dotted Navy)
            val altPath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                quadraticBezierTo(w * 0.55f, h * 0.32f, endPoint.x, endPoint.y)
            }
            drawPath(
                path = altPath,
                color = SlateLight,
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )

            // Recommended Active AI Route (Vibrant Orange with glow)
            val mainRoutePath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                cubicTo(midPoint1.x, midPoint1.y, midPoint2.x, midPoint2.y, endPoint.x, endPoint.y)
            }

            // Route Shadow/Glow
            drawPath(
                path = mainRoutePath,
                color = OrangePrimary.copy(alpha = 0.25f),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Route Solid Core
            drawPath(
                path = mainRoutePath,
                color = OrangePrimary,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Draw Waypoint Nodes
            // Start Pickup Node (A)
            drawCircle(Color.White, radius = 10.dp.toPx(), center = startPoint)
            drawCircle(SuccessGreen, radius = 7.dp.toPx(), center = startPoint)

            // End Delivery Node (B)
            drawCircle(Color.White, radius = 10.dp.toPx(), center = endPoint)
            drawCircle(OrangeDark, radius = 7.dp.toPx(), center = endPoint)

            // Live Truck Radar Pulse
            drawCircle(
                color = OrangePrimary.copy(alpha = 0.22f),
                radius = 18.dp.toPx() * pulseScale,
                center = truckPos
            )
            drawCircle(Color.White, radius = 13.dp.toPx(), center = truckPos)
            drawCircle(NavyDark, radius = 10.dp.toPx(), center = truckPos)
        }

        // Overlay: Start (A) Marker Label
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chicago Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                }
            }
        }

        // Overlay: End (B) Marker Label
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 48.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(OrangeDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("B", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dallas DC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                }
            }
        }

        // Overlay: Truck Position Badge
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 30.dp, end = 20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavyDark,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TRK-104 • 64 mph", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Floating Bottom Map Telemetry Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${routePlan.totalDistanceMiles} mi • ${routePlan.totalTimeFormatted}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                }
                Text("Est. Fuel: $${routePlan.estimatedFuelCost.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessGreenContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("-18% Fuel", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                }
            }
        }
    }
}
