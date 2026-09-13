package com.example.roadstar.data.model

enum class UserRole(val label: String, val description: String) {
    FLEET_MANAGER("Fleet Manager", "Oversee total fleet operations, financial metrics, and asset health"),
    DISPATCHER("Dispatcher", "Match freight loads, optimize routes, and schedule drivers"),
    DRIVER("Driver", "Execute active loads, track waypoints, and confirm deliveries on the road")
}
