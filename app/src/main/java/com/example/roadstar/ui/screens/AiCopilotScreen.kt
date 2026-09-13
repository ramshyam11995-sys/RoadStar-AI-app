package com.example.roadstar.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.roadstar.data.model.ChatMessage
import com.example.roadstar.data.model.Load
import com.example.roadstar.data.model.Truck
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Strengthened Full-Screen AI Copilot providing real-time fleet intelligence,
 * automated load matching analysis, predictive route optimization, and chat assistance.
 */
@Composable
fun AiCopilotScreen(
    messages: List<ChatMessage>,
    trucks: List<Truck>,
    loads: List<Load>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickPrompts = listOf(
        "Which truck should take Load #204?",
        "Show maintenance alerts & risk scores",
        "How can I cut fleet fuel costs by 18%?",
        "Check Driver HOS violation risks",
        "Summarize today's active dispatches"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
    ) {
        // AI Copilot Hero Status Card
        Surface(
            color = NavyDark,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "RoadStar Copilot",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = "Gemini 2.5",
                                        color = OrangePrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Autonomous Dispatch & Route Co-Pilot",
                                fontSize = 11.sp,
                                color = SlateLight
                            )
                        }
                    }

                    // Fleet Telemetry Quick Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
                                text = "${trucks.size} Units Live",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Real-time Fleet Insights Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val availableTrucks = trucks.count { it.status.name == "AVAILABLE" }
                    val highRiskTrucks = trucks.count { it.maintenanceRisk.name != "LOW" }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Available Units", fontSize = 10.sp, color = SlateLight)
                            Text("$availableTrucks Trucks Ready", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Loads in Queue", fontSize = 10.sp, color = SlateLight)
                            Text("${loads.size} Active Loads", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Maint. Alerts", fontSize = 10.sp, color = SlateLight)
                            Text(
                                text = if (highRiskTrucks > 0) "$highRiskTrucks Attention" else "All Nominal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (highRiskTrucks > 0) OrangePrimary else SuccessGreen
                            )
                        }
                    }
                }
            }
        }

        // Quick Prompts Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(prompt) }
                        .testTag("quick_prompt_${prompt.take(10).replace(" ", "_").lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavyDark
                        )
                    }
                }
            }
        }

        // Chat Message Log
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        CircularProgressIndicator(
                            color = OrangePrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Copilot is analyzing fleet telemetry with Gemini...",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )
                    }
                }
            }
        }

        // Chat Input Box
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask Copilot: match loads, check fuel, routes...",
                            fontSize = 13.sp,
                            color = SlateMuted
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_copilot_input")
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .testTag("ai_copilot_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send to Copilot",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
