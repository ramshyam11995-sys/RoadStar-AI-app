package com.example.roadstar.data.model

enum class DriverStatus(val label: String) {
    AVAILABLE("Available"),
    ON_DUTY("On Duty (Driving)"),
    RESTING("Mandatory Rest Break"),
    OFF_DUTY("Off Duty")
}

data class DriverTask(
    val id: String,
    val title: String,
    val location: String,
    val timeFormatted: String,
    val isCompleted: Boolean = false,
    val isKeyMilestone: Boolean = false
)

data class Driver(
    val id: String,
    val name: String,
    val phone: String,
    val status: DriverStatus,
    val hoursOfServiceRemaining: Double, // max 11.0
    val safetyRating: Double = 4.9,
    val assignedTruckId: String? = null,
    val currentLoadId: String? = null,
    val homeTerminal: String = "Chicago Hub",
    val tripsCompleted: Int = 184,
    val tasks: List<DriverTask> = emptyList()
)

data class ProofOfDelivery(
    val loadId: String,
    val recipientName: String,
    val receivedTimestamp: String,
    val signatureNotes: String = "Signed electronically on delivery terminal",
    val photoConfirmed: Boolean = true,
    val cargoCondition: String = "Good / Intact Seal"
)
