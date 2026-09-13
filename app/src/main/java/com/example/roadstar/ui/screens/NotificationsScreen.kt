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
import com.example.roadstar.data.model.NotificationCategory
import com.example.roadstar.data.model.NotificationItem
import com.example.roadstar.data.model.NotificationPriority
import com.example.roadstar.ui.components.NotificationPriorityBadge
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    var priorityFilter by remember { mutableStateOf<NotificationPriority?>(null) }

    val filteredNotifications = notifications.filter {
        priorityFilter == null || it.priority == priorityFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Row: Title & Mark All Read
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Operational Alerts (${notifications.size})",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = NavyDark
            )

            TextButton(
                onClick = onMarkAllRead,
                modifier = Modifier.testTag("mark_all_read_btn")
            ) {
                Text("Mark All Read", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = priorityFilter == null,
                    onClick = { priorityFilter = null },
                    label = { Text("All (${notifications.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            items(NotificationPriority.values()) { priority ->
                val count = notifications.count { it.priority == priority }
                FilterChip(
                    selected = priorityFilter == priority,
                    onClick = { priorityFilter = if (priorityFilter == priority) null else priority },
                    label = { Text("${priority.label} ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No alerts in this category. Operations nominal!", fontSize = 13.sp, color = SlateMedium)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredNotifications, key = { it.id }) { item ->
                    NotificationCardItem(
                        item = item,
                        onMarkRead = { onMarkRead(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCardItem(
    item: NotificationItem,
    onMarkRead: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) Color.White else Color(0xFFFFF7ED)
        ),
        border = BorderStroke(
            1.dp,
            if (!item.isRead) Color(0xFFFFD8BF) else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarkRead() }
            .testTag("notification_${item.id.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Priority icon badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.category) {
                            NotificationCategory.DELIVERY_DELAY -> ErrorRedContainer
                            NotificationCategory.MAINTENANCE -> WarningAmberContainer
                            NotificationCategory.LOAD_ASSIGNMENT -> SuccessGreenContainer
                            NotificationCategory.DRIVER_ISSUE -> InfoBlueContainer
                            NotificationCategory.AI_RECOMMENDATION -> OrangeContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.category) {
                        NotificationCategory.DELIVERY_DELAY -> Icons.Default.Schedule
                        NotificationCategory.MAINTENANCE -> Icons.Default.Build
                        NotificationCategory.LOAD_ASSIGNMENT -> Icons.Default.Inventory2
                        NotificationCategory.DRIVER_ISSUE -> Icons.Default.Person
                        NotificationCategory.AI_RECOMMENDATION -> Icons.Default.AutoAwesome
                    },
                    contentDescription = null,
                    tint = when (item.category) {
                        NotificationCategory.DELIVERY_DELAY -> ErrorRed
                        NotificationCategory.MAINTENANCE -> WarningAmber
                        NotificationCategory.LOAD_ASSIGNMENT -> SuccessGreen
                        NotificationCategory.DRIVER_ISSUE -> InfoBlue
                        NotificationCategory.AI_RECOMMENDATION -> OrangePrimary
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NotificationPriorityBadge(priority = item.priority)
                    Text(item.timestamp, fontSize = 10.sp, color = SlateMuted)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.message,
                    fontSize = 12.sp,
                    color = SlateDark,
                    lineHeight = 17.sp
                )

                if (item.relatedEntityId != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ref: ${item.relatedEntityId} • Action Required",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeDark
                    )
                }
            }

            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary)
                )
            }
        }
    }
}
