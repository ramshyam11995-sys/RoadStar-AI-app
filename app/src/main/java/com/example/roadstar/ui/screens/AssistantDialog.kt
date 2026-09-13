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
import com.example.roadstar.data.model.ChatMessage
import com.example.ui.theme.*

@Composable
fun AssistantDialog(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "Which truck should take Load #204?",
        "Which deliveries are at risk today?",
        "How can I reduce fuel costs?",
        "Show available drivers",
        "Why is Truck #104 delayed?"
    )

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
                    .padding(16.dp)
            ) {
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
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("RoadStar Copilot", fontWeight = FontWeight.Black, fontSize = 16.sp, color = NavyDark)
                            Text("AI Operations Intelligence", fontSize = 11.sp, color = SlateMuted)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                // Chat Messages List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = OrangePrimary,
                                    strokeWidth = 2.dp
                                )
                                Text("RoadStar AI analyzing fleet telemetry...", fontSize = 12.sp, color = SlateMedium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Prompt Chips
                Text("Suggested Queries:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickQuestions) { q ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = OrangeContainer,
                            border = BorderStroke(1.dp, Color(0xFFFFD8BF)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSendMessage(q) }
                                .testTag("quick_prompt_${q.take(10).replace(" ", "_").lowercase()}")
                        ) {
                            Text(
                                text = q,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangeDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask about fleet, loads, or routes...", fontSize = 13.sp, color = SlateMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("assistant_input")
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
                            .size(46.dp)
                            .testTag("send_assistant_msg")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) OrangePrimary else Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, if (message.isUser) OrangePrimary else BorderSubtle),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = if (message.isUser) Color.White else NavyDark,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = if (message.isUser) Color.White.copy(alpha = 0.7f) else SlateMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
