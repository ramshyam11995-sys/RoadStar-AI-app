package com.example.roadstar.data.model

data class AnalyticsSummary(
    val grossRevenueUsd: Double = 142850.0,
    val revenueChangePercent: Double = 14.8,
    val costPerMileUsd: Double = 1.82,
    val costPerMileTarget: Double = 1.95,
    val fuelEfficiencyMpg: Double = 7.3,
    val revenuePerMileUsd: Double = 3.24,
    val averageFuelMpg: Double = 7.3,
    val averageDeliveryTimeHours: Double = 11.2,
    val deliverySuccessRatePercent: Double = 98.4,
    val averageDeliveryHours: Double = 11.2,
    val fleetUtilizationPercent: Int = 84,
    val totalActiveLoads: Int = 14,
    val activeTrucksCount: Int = 18,
    val availableTrucksCount: Int = 6,
    val totalMaintenanceAlerts: Int = 3,
    val deliveriesTodayCount: Int = 9
)

data class RevenueDataPoint(
    val dayLabel: String,
    val revenueUsd: Float,
    val fuelExpenseUsd: Float
)

data class FleetPerformanceMetric(
    val category: String,
    val percentage: Int,
    val statusText: String
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String,
    val suggestedActions: List<String> = emptyList()
)
