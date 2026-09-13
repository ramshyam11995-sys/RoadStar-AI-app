package com.example.roadstar.data.model

data class Waypoint(
    val title: String,
    val city: String,
    val state: String,
    val eta: String,
    val isStop: Boolean = false,
    val isPassed: Boolean = false,
    val distanceRemainingMiles: Int
)

data class RouteAlternative(
    val id: String,
    val name: String,
    val distanceMiles: Int,
    val durationFormatted: String,
    val estimatedFuelCost: Double,
    val fuelSavingsPercent: Int,
    val timeSavingsMinutes: Int,
    val isRecommended: Boolean,
    val riskFactor: String
)

data class RoutePlan(
    val id: String,
    val loadId: String,
    val truckId: String,
    val origin: String,
    val destination: String,
    val totalDistanceMiles: Int,
    val totalTimeFormatted: String,
    val currentProgressPercent: Float,
    val estimatedFuelCost: Double,
    val recommendedSavingsSummary: String,
    val delayRiskSummary: String,
    val weatherCondition: String,
    val alternatives: List<RouteAlternative>,
    val waypoints: List<Waypoint>
)
