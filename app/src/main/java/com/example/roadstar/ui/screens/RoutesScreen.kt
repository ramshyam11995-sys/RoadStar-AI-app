package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.roadstar.data.model.RouteAlternative
import com.example.roadstar.data.model.RoutePlan
import com.example.roadstar.data.model.Waypoint
import com.example.roadstar.ui.components.InteractiveMapCanvas
import com.example.ui.theme.*

@Composable
fun RoutesScreen(
    routePlan: RoutePlan,
    selectedAlternativeId: String,
    onSelectAlternative: (String) -> Unit,
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
        // Map Canvas Section
        item {
            InteractiveMapCanvas(routePlan = routePlan)
        }

        // AI Route Recommendation Highlight Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = OrangeContainer),
                border = BorderStroke(1.dp, Color(0xFFFFD8BF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "AI Route Recommendation",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = OnOrangeContainer
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreen
                            ) {
                                Text(
                                    text = "-18% Fuel",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = routePlan.recommendedSavingsSummary,
                            fontSize = 12.sp,
                            color = SlateDark,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Delay Risks Assessment Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Transit & Delay Risk Analysis", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyDark)
                        }
                        Text(routePlan.weatherCondition, fontSize = 11.sp, color = SlateMuted)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = routePlan.delayRiskSummary,
                        fontSize = 12.sp,
                        color = SlateDark,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Route Options / Comparison
        item {
            Text(
                text = "Route Alternatives Comparison",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyDark
            )
        }

        items(routePlan.alternatives, key = { it.id }) { alt ->
            val isSelected = alt.id == selectedAlternativeId
            RouteAlternativeCard(
                alternative = alt,
                isSelected = isSelected,
                onSelect = { onSelectAlternative(alt.id) }
            )
        }

        // Turn-by-Turn Waypoints Schedule
        item {
            Text(
                text = "Turn-by-Turn Waypoint Schedule",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyDark
            )
        }

        items(routePlan.waypoints) { wp ->
            WaypointItemRow(waypoint = wp)
        }
    }
}

@Composable
fun RouteAlternativeCard(
    alternative: RouteAlternative,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF7ED) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) OrangePrimary else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("route_alt_${alternative.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect,
                        colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = alternative.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyDark
                        )
                        Text(
                            text = alternative.riskFactor,
                            fontSize = 11.sp,
                            color = SlateMuted
                        )
                    }
                }

                if (alternative.isRecommended) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OrangePrimary
                    ) {
                        Text(
                            text = "AI Best",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Distance: ${alternative.distanceMiles} mi", fontSize = 12.sp, color = SlateDark)
                Text("Duration: ${alternative.durationFormatted}", fontSize = 12.sp, color = SlateDark)
                Text("Fuel: $${alternative.estimatedFuelCost.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangeDark)
            }
        }
    }
}

@Composable
fun WaypointItemRow(waypoint: Waypoint) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            waypoint.isPassed -> SuccessGreenContainer
                            waypoint.isStop -> OrangeContainer
                            else -> Color(0xFFF1F5F9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        waypoint.isPassed -> Icons.Default.Check
                        waypoint.isStop -> Icons.Default.PauseCircle
                        else -> Icons.Default.LocationOn
                    },
                    contentDescription = null,
                    tint = when {
                        waypoint.isPassed -> SuccessGreen
                        waypoint.isStop -> OrangePrimary
                        else -> SlateMedium
                    },
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = waypoint.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NavyDark
                )
                Text(
                    text = "${waypoint.city}, ${waypoint.state} • ${waypoint.distanceRemainingMiles} mi remaining",
                    fontSize = 11.sp,
                    color = SlateMuted
                )
            }

            Text(
                text = waypoint.eta,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (waypoint.isPassed) SlateMuted else OrangeDark
            )
        }
    }
}
