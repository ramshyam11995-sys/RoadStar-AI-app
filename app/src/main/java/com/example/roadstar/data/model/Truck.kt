package com.example.roadstar.data.model

enum class TruckStatus(val label: String) {
    ACTIVE("Active"),
    AVAILABLE("Available"),
    MAINTENANCE("Maintenance"),
    OFFLINE("Offline")
}

enum class MaintenanceRisk(val label: String) {
    LOW("Healthy"),
    MEDIUM("Caution"),
    CRITICAL("Urgent Action Required")
}

data class Truck(
    val id: String,
    val model: String,
    val year: Int = 2024,
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val currentLocation: String,
    val status: TruckStatus,
    val fuelLevelPercent: Int,
    val mileage: Int,
    val currentLoadId: String? = null,
    val maintenanceScore: Int, // 0 - 100
    val maintenanceRisk: MaintenanceRisk,
    val maintenanceAlert: String? = null,
    val recommendedAction: String? = null,
    val capacityLbs: Int = 45000,
    val averageMpg: Double = 7.4,
    val lastServiceDate: String = "2026-08-15"
)
