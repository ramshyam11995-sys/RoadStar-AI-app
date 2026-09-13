package com.example.roadstar.data.model

enum class LoadStatus(val label: String) {
    AVAILABLE("Available"),
    ASSIGNED("Assigned"),
    IN_TRANSIT("In Transit"),
    DELIVERED("Delivered"),
    DELAYED("Delayed")
}

enum class CargoType(val label: String, val tempControlled: Boolean) {
    DRY_VAN("Dry Van Freight", false),
    REFRIGERATED("Reefer / Cold Chain", true),
    FLATBED("Flatbed Heavy Steel", false),
    HAZMAT("Hazmat Class 3 Chemical", false),
    AUTO_CARRIER("Auto Carrier (5 Units)", false)
}

data class Load(
    val id: String,
    val title: String,
    val pickupLocation: String,
    val deliveryLocation: String,
    val pickupDateTime: String,
    val deliveryDeadline: String,
    val cargoType: CargoType,
    val cargoWeightLbs: Int,
    val loadValueUsd: Double,
    val requiredTruckType: String,
    val status: LoadStatus,
    val assignedTruckId: String? = null,
    val assignedDriverName: String? = null,
    val distanceMiles: Int = 420,
    val ratePerMileUsd: Double = 3.25,
    val specialNotes: String = "No pallet exchange required. Dock appointment confirmed."
)

data class AiMatchRecommendation(
    val truckId: String,
    val truckModel: String,
    val driverId: String,
    val driverName: String,
    val matchScorePercent: Int,
    val reasonExplanation: String,
    val proximityMiles: Int,
    val driverHoursRemaining: Double,
    val fuelEfficiencyRating: String,
    val capacityUtilizationPercent: Int
)
