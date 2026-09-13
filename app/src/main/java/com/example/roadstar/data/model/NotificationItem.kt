package com.example.roadstar.data.model

enum class NotificationPriority(val label: String) {
    CRITICAL("Critical"),
    WARNING("Warning"),
    INFO("Info")
}

enum class NotificationCategory(val label: String) {
    DELIVERY_DELAY("Delivery Delay"),
    MAINTENANCE("Maintenance Alert"),
    LOAD_ASSIGNMENT("Load Assignment"),
    DRIVER_ISSUE("Driver Alert"),
    AI_RECOMMENDATION("AI Insight")
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val priority: NotificationPriority,
    val category: NotificationCategory,
    val isRead: Boolean = false,
    val relatedEntityId: String? = null
)
