package com.example.roadstar.data.repository

import com.example.roadstar.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RoadStarRepository {

    private val _loads = MutableStateFlow<List<Load>>(getInitialLoads())
    val loads: StateFlow<List<Load>> = _loads.asStateFlow()

    private val _trucks = MutableStateFlow<List<Truck>>(getInitialTrucks())
    val trucks: StateFlow<List<Truck>> = _trucks.asStateFlow()

    private val _drivers = MutableStateFlow<List<Driver>>(getInitialDrivers())
    val drivers: StateFlow<List<Driver>> = _drivers.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(getInitialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _activeRoute = MutableStateFlow(getInitialRoutePlan())
    val activeRoute: StateFlow<RoutePlan> = _activeRoute.asStateFlow()

    private val _analytics = MutableStateFlow(AnalyticsSummary())
    val analytics: StateFlow<AnalyticsSummary> = _analytics.asStateFlow()

    fun updateLoadStatus(loadId: String, newStatus: LoadStatus) {
        _loads.update { list ->
            list.map { if (it.id == loadId) it.copy(status = newStatus) else it }
        }
    }

    fun assignTruckAndDriverToLoad(loadId: String, truckId: String, driverName: String) {
        _loads.update { list ->
            list.map {
                if (it.id == loadId) {
                    it.copy(
                        status = LoadStatus.ASSIGNED,
                        assignedTruckId = truckId,
                        assignedDriverName = driverName
                    )
                } else it
            }
        }
        _trucks.update { list ->
            list.map {
                if (it.id == truckId) {
                    it.copy(
                        status = TruckStatus.ACTIVE,
                        currentLoadId = loadId
                    )
                } else it
            }
        }
        // Add notification
        val newNotif = NotificationItem(
            id = "NT-${System.currentTimeMillis() % 10000}",
            title = "Load $loadId Assigned",
            message = "Assigned to Truck $truckId ($driverName). Route initialized.",
            timestamp = "Just now",
            priority = NotificationPriority.INFO,
            category = NotificationCategory.LOAD_ASSIGNMENT,
            relatedEntityId = loadId
        )
        _notifications.update { listOf(newNotif) + it }
    }

    fun createLoad(
        pickupLocation: String,
        deliveryLocation: String,
        pickupDateTime: String,
        deliveryDeadline: String,
        cargoType: CargoType,
        weightLbs: Int,
        loadValue: Double,
        requiredTruckType: String
    ): Load {
        val newId = "LD-${(211..299).random()}"
        val newLoad = Load(
            id = newId,
            title = "$pickupLocation to $deliveryLocation",
            pickupLocation = pickupLocation,
            deliveryLocation = deliveryLocation,
            pickupDateTime = pickupDateTime,
            deliveryDeadline = deliveryDeadline,
            cargoType = cargoType,
            cargoWeightLbs = weightLbs,
            loadValueUsd = loadValue,
            requiredTruckType = requiredTruckType,
            status = LoadStatus.AVAILABLE,
            distanceMiles = (250..850).random(),
            ratePerMileUsd = 3.40
        )
        _loads.update { listOf(newLoad) + it }
        _notifications.update {
            listOf(
                NotificationItem(
                    id = "NT-${System.currentTimeMillis() % 10000}",
                    title = "New Freight Load $newId Posted",
                    message = "$weightLbs lbs ${cargoType.label} ready for AI dispatch.",
                    timestamp = "Just now",
                    priority = NotificationPriority.INFO,
                    category = NotificationCategory.LOAD_ASSIGNMENT,
                    relatedEntityId = newId
                )
            ) + it
        }
        return newLoad
    }

    fun markNotificationRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun updateTruckTelemetry(truckId: String, location: String, fuelPercent: Int, avgMpg: Double) {
        _trucks.update { list ->
            list.map {
                if (it.id == truckId) it.copy(currentLocation = location, fuelLevelPercent = fuelPercent, averageMpg = avgMpg)
                else it
            }
        }
    }

    fun triggerEmergencyBreakdown(truckId: String = "TRK-104") {
        _trucks.update { list ->
            list.map {
                if (it.id == truckId) {
                    it.copy(
                        maintenanceRisk = MaintenanceRisk.CRITICAL,
                        maintenanceScore = 24,
                        status = TruckStatus.MAINTENANCE,
                        currentLocation = "I-80 Mile Marker 152 (Pulled Over - Overheat)"
                    )
                } else it
            }
        }
        val alert = NotificationItem(
            id = "NT-EMERGENCY-${System.currentTimeMillis() % 10000}",
            title = "🚨 CRITICAL: Coolant Overheat on $truckId",
            message = "Telemetry indicates engine coolant 242°F at I-80 Mile Marker 152. Driver pulled over safely. Nearest service bay: Des Moines Truck Center (14 mi).",
            timestamp = "Just now",
            priority = NotificationPriority.CRITICAL,
            category = NotificationCategory.MAINTENANCE,
            relatedEntityId = truckId
        )
        _notifications.update { listOf(alert) + it }
    }

    fun simulateAllTrucksMovement() {
        _trucks.update { list ->
            list.mapIndexed { index, truck ->
                if (truck.status == TruckStatus.ACTIVE) {
                    val newFuel = (truck.fuelLevelPercent - 1).coerceAtLeast(18)
                    val newMpg = String.format("%.1f", 7.2 + ((index * 3) % 8) * 0.1).toDoubleOrNull() ?: 7.4
                    truck.copy(
                        fuelLevelPercent = newFuel,
                        averageMpg = newMpg,
                        currentLocation = when (truck.id) {
                            "TRK-101" -> "I-55 Mile Marker ${(150..220).random()}"
                            "TRK-104" -> if (truck.maintenanceRisk == MaintenanceRisk.CRITICAL) truck.currentLocation else "I-80 Mile Marker ${(160..240).random()}"
                            "TRK-102" -> "I-94 E Mile Marker ${(80..130).random()}"
                            "TRK-105" -> "US-281 N Mile Marker ${(40..90).random()}"
                            else -> truck.currentLocation
                        }
                    )
                } else truck
            }
        }
    }

    fun completeDelivery(loadId: String, recipientName: String) {
        updateLoadStatus(loadId, LoadStatus.DELIVERED)
        _trucks.update { list ->
            list.map {
                if (it.currentLoadId == loadId) {
                    it.copy(status = TruckStatus.AVAILABLE, currentLoadId = null)
                } else it
            }
        }
        _notifications.update {
            listOf(
                NotificationItem(
                    id = "NT-${System.currentTimeMillis() % 10000}",
                    title = "Delivery Complete - $loadId",
                    message = "Delivered & signed by $recipientName. Proof of delivery uploaded.",
                    timestamp = "Just now",
                    priority = NotificationPriority.INFO,
                    category = NotificationCategory.LOAD_ASSIGNMENT,
                    relatedEntityId = loadId
                )
            ) + it
        }
    }

    fun resetDemoData() {
        _loads.value = getInitialLoads()
        _trucks.value = getInitialTrucks()
        _drivers.value = getInitialDrivers()
        _notifications.value = getInitialNotifications()
        _activeRoute.value = getInitialRoutePlan()
    }

    companion object {
        fun getInitialLoads(): List<Load> = listOf(
            Load(
                id = "LD-204",
                title = "Chicago to Dallas Express",
                pickupLocation = "Chicago Logistics Hub, IL",
                deliveryLocation = "Dallas Metro Distribution Center, TX",
                pickupDateTime = "Today, 08:00 AM",
                deliveryDeadline = "Tomorrow, 06:00 PM",
                cargoType = CargoType.REFRIGERATED,
                cargoWeightLbs = 38000,
                loadValueUsd = 4850.0,
                requiredTruckType = "Reefer 53ft",
                status = LoadStatus.AVAILABLE,
                distanceMiles = 925,
                ratePerMileUsd = 3.65,
                specialNotes = "Temperature must remain locked at -4°F. Continuous recorder required."
            ),
            Load(
                id = "LD-205",
                title = "Atlanta to Orlando FMCG",
                pickupLocation = "Atlanta South Intermodal, GA",
                deliveryLocation = "Orlando Fulfillment Park, FL",
                pickupDateTime = "Today, 10:30 AM",
                deliveryDeadline = "Tonight, 11:00 PM",
                cargoType = CargoType.DRY_VAN,
                cargoWeightLbs = 24500,
                loadValueUsd = 2650.0,
                requiredTruckType = "Dry Van 53ft",
                status = LoadStatus.ASSIGNED,
                assignedTruckId = "TRK-208",
                assignedDriverName = "Elena Rostova",
                distanceMiles = 440,
                ratePerMileUsd = 3.20
            ),
            Load(
                id = "LD-206",
                title = "Los Angeles to Phoenix Structural Steel",
                pickupLocation = "Long Beach Port Terminal 4, CA",
                deliveryLocation = "Phoenix Industrial Center, AZ",
                pickupDateTime = "Yesterday, 02:00 PM",
                deliveryDeadline = "Today, 04:30 PM",
                cargoType = CargoType.FLATBED,
                cargoWeightLbs = 42000,
                loadValueUsd = 3900.0,
                requiredTruckType = "Flatbed Step-Deck",
                status = LoadStatus.IN_TRANSIT,
                assignedTruckId = "TRK-312",
                assignedDriverName = "James Cooper",
                distanceMiles = 380,
                ratePerMileUsd = 3.80
            ),
            Load(
                id = "LD-207",
                title = "Seattle to Denver Specialty Chemicals",
                pickupLocation = "Tacoma Freight Yard, WA",
                deliveryLocation = "Denver Rocky Mountain Terminal, CO",
                pickupDateTime = "Sep 11, 06:00 AM",
                deliveryDeadline = "Today, 01:00 PM (Overdue)",
                cargoType = CargoType.HAZMAT,
                cargoWeightLbs = 31000,
                loadValueUsd = 6200.0,
                requiredTruckType = "Hazmat Certified Van",
                status = LoadStatus.DELAYED,
                assignedTruckId = "TRK-415",
                assignedDriverName = "Tyrone Davis",
                distanceMiles = 1310,
                ratePerMileUsd = 4.70,
                specialNotes = "Delayed in I-84 mountain pass due to winter storm advisory."
            ),
            Load(
                id = "LD-208",
                title = "Detroit to Louisville Automotive Parts",
                pickupLocation = "Detroit Assembly Gateway, MI",
                deliveryLocation = "Louisville Truck Plant, KY",
                pickupDateTime = "Sep 10, 07:00 AM",
                deliveryDeadline = "Sep 11, 03:00 PM",
                cargoType = CargoType.DRY_VAN,
                cargoWeightLbs = 34000,
                loadValueUsd = 3150.0,
                requiredTruckType = "Dry Van 53ft",
                status = LoadStatus.DELIVERED,
                assignedTruckId = "TRK-519",
                assignedDriverName = "Sarah Lin",
                distanceMiles = 360,
                ratePerMileUsd = 3.35
            ),
            Load(
                id = "LD-209",
                title = "Houston to New Orleans Oil Equipment",
                pickupLocation = "Houston Ship Channel, TX",
                deliveryLocation = "New Orleans River Port, LA",
                pickupDateTime = "Tomorrow, 09:00 AM",
                deliveryDeadline = "Tomorrow, 08:00 PM",
                cargoType = CargoType.FLATBED,
                cargoWeightLbs = 29000,
                loadValueUsd = 2850.0,
                requiredTruckType = "Flatbed 48ft",
                status = LoadStatus.AVAILABLE,
                distanceMiles = 350,
                ratePerMileUsd = 3.50
            ),
            Load(
                id = "LD-210",
                title = "Memphis to St. Louis Pharmaceuticals",
                pickupLocation = "Memphis Airport Logistics, TN",
                deliveryLocation = "St. Louis Cold Logistics, MO",
                pickupDateTime = "Tomorrow, 01:00 PM",
                deliveryDeadline = "Tomorrow, 10:00 PM",
                cargoType = CargoType.REFRIGERATED,
                cargoWeightLbs = 16000,
                loadValueUsd = 3400.0,
                requiredTruckType = "Reefer 53ft",
                status = LoadStatus.AVAILABLE,
                distanceMiles = 290,
                ratePerMileUsd = 4.10
            )
        )

        fun getInitialTrucks(): List<Truck> = listOf(
            Truck(
                id = "TRK-104",
                model = "Kenworth T680 NextGen",
                year = 2024,
                assignedDriverId = "DRV-01",
                assignedDriverName = "Dave Miller",
                currentLocation = "Chicago Terminal (12 mi to Hub)",
                status = TruckStatus.AVAILABLE,
                fuelLevelPercent = 88,
                mileage = 128450,
                currentLoadId = null,
                maintenanceScore = 74,
                maintenanceRisk = MaintenanceRisk.MEDIUM,
                maintenanceAlert = "Brake pad thickness is 3.2mm. Recommend disc & lining inspection within 7 days.",
                recommendedAction = "Schedule preventive brake maintenance at Dallas depot after current transit.",
                capacityLbs = 45000,
                averageMpg = 7.6,
                lastServiceDate = "2026-08-02"
            ),
            Truck(
                id = "TRK-208",
                model = "Freightliner Cascadia",
                year = 2023,
                assignedDriverId = "DRV-02",
                assignedDriverName = "Elena Rostova",
                currentLocation = "Atlanta South Gateway, GA",
                status = TruckStatus.ACTIVE,
                fuelLevelPercent = 78,
                mileage = 94200,
                currentLoadId = "LD-205",
                maintenanceScore = 92,
                maintenanceRisk = MaintenanceRisk.LOW,
                maintenanceAlert = null,
                recommendedAction = "All sensors within optimal operational tolerances.",
                capacityLbs = 46000,
                averageMpg = 7.8,
                lastServiceDate = "2026-08-28"
            ),
            Truck(
                id = "TRK-312",
                model = "Peterbilt 579 UltraLoft",
                year = 2024,
                assignedDriverId = "DRV-03",
                assignedDriverName = "James Cooper",
                currentLocation = "I-10 Mile 124, Quartzsite, AZ",
                status = TruckStatus.ACTIVE,
                fuelLevelPercent = 54,
                mileage = 164300,
                currentLoadId = "LD-206",
                maintenanceScore = 86,
                maintenanceRisk = MaintenanceRisk.LOW,
                maintenanceAlert = null,
                capacityLbs = 48000,
                averageMpg = 7.1,
                lastServiceDate = "2026-08-12"
            ),
            Truck(
                id = "TRK-415",
                model = "Volvo VNL 860",
                year = 2023,
                assignedDriverId = "DRV-04",
                assignedDriverName = "Tyrone Davis",
                currentLocation = "Boise Industrial Park, ID",
                status = TruckStatus.ACTIVE,
                fuelLevelPercent = 38,
                mileage = 212800,
                currentLoadId = "LD-207",
                maintenanceScore = 42,
                maintenanceRisk = MaintenanceRisk.CRITICAL,
                maintenanceAlert = "DPF particulate filter differential pressure exceeding safety limit (4.8 psi). Engine derate possible within 120 miles.",
                recommendedAction = "Immediate stationary forced DPF regeneration required at Boise Service Bay.",
                capacityLbs = 45000,
                averageMpg = 6.8,
                lastServiceDate = "2026-06-14"
            ),
            Truck(
                id = "TRK-519",
                model = "Mack Anthem 70-inch Sleeper",
                year = 2024,
                assignedDriverId = "DRV-05",
                assignedDriverName = "Sarah Lin",
                currentLocation = "Kansas City Distribution Hub, MO",
                status = TruckStatus.AVAILABLE,
                fuelLevelPercent = 94,
                mileage = 48200,
                currentLoadId = null,
                maintenanceScore = 98,
                maintenanceRisk = MaintenanceRisk.LOW,
                maintenanceAlert = null,
                capacityLbs = 45000,
                averageMpg = 8.1,
                lastServiceDate = "2026-09-01"
            ),
            Truck(
                id = "TRK-620",
                model = "International LT625",
                year = 2022,
                assignedDriverId = null,
                assignedDriverName = null,
                currentLocation = "Chicago Maintenance Depot, Bay 3",
                status = TruckStatus.MAINTENANCE,
                fuelLevelPercent = 20,
                mileage = 285000,
                currentLoadId = null,
                maintenanceScore = 35,
                maintenanceRisk = MaintenanceRisk.CRITICAL,
                maintenanceAlert = "Transmission clutch actuator replacement in progress.",
                recommendedAction = "Technician estimated completion: Friday 4:00 PM.",
                capacityLbs = 44000,
                averageMpg = 6.5,
                lastServiceDate = "2026-09-10"
            )
        )

        fun getInitialDrivers(): List<Driver> = listOf(
            Driver(
                id = "DRV-01",
                name = "Dave Miller",
                phone = "(312) 555-0144",
                status = DriverStatus.AVAILABLE,
                hoursOfServiceRemaining = 9.5,
                safetyRating = 4.95,
                assignedTruckId = "TRK-104",
                currentLoadId = null,
                homeTerminal = "Chicago Hub, IL",
                tripsCompleted = 214,
                tasks = listOf(
                    DriverTask("TK-1", "Pre-trip vehicle safety inspection", "Chicago Yard", "07:30 AM", isCompleted = true),
                    DriverTask("TK-2", "Check in at Shipper Gate 4", "Chicago Logistics Hub", "08:00 AM", isKeyMilestone = true),
                    DriverTask("TK-3", "Verify Reefer temp at -4°F & BOL sign-off", "Chicago Bay 12", "08:45 AM"),
                    DriverTask("TK-4", "Mid-trip rest break (30 min)", "Mt. Vernon Travel Plaza, IL", "01:30 PM"),
                    DriverTask("TK-5", "Final delivery appointment", "Dallas Distribution Hub", "Tomorrow 05:00 PM", isKeyMilestone = true)
                )
            ),
            Driver(
                id = "DRV-02",
                name = "Elena Rostova",
                phone = "(404) 555-0182",
                status = DriverStatus.ON_DUTY,
                hoursOfServiceRemaining = 6.2,
                safetyRating = 4.88,
                assignedTruckId = "TRK-208",
                currentLoadId = "LD-205",
                homeTerminal = "Atlanta Terminal, GA",
                tripsCompleted = 168
            ),
            Driver(
                id = "DRV-03",
                name = "James Cooper",
                phone = "(213) 555-0199",
                status = DriverStatus.ON_DUTY,
                hoursOfServiceRemaining = 4.0,
                safetyRating = 4.92,
                assignedTruckId = "TRK-312",
                currentLoadId = "LD-206",
                homeTerminal = "Los Angeles Terminal, CA",
                tripsCompleted = 295
            ),
            Driver(
                id = "DRV-04",
                name = "Tyrone Davis",
                phone = "(206) 555-0173",
                status = DriverStatus.RESTING,
                hoursOfServiceRemaining = 1.0,
                safetyRating = 4.76,
                assignedTruckId = "TRK-415",
                currentLoadId = "LD-207",
                homeTerminal = "Seattle Port Yard, WA",
                tripsCompleted = 142
            ),
            Driver(
                id = "DRV-05",
                name = "Sarah Lin",
                phone = "(816) 555-0129",
                status = DriverStatus.AVAILABLE,
                hoursOfServiceRemaining = 10.5,
                safetyRating = 4.98,
                assignedTruckId = "TRK-519",
                currentLoadId = null,
                homeTerminal = "Kansas City Depot, MO",
                tripsCompleted = 96
            )
        )

        fun getInitialNotifications(): List<NotificationItem> = listOf(
            NotificationItem(
                id = "NT-101",
                title = "Critical DPF Sensor Alert",
                message = "Truck #415 (Boise, ID) reports high DPF differential pressure (4.8 psi). Engine derate risk in 120 mi.",
                timestamp = "8 min ago",
                priority = NotificationPriority.CRITICAL,
                category = NotificationCategory.MAINTENANCE,
                relatedEntityId = "TRK-415"
            ),
            NotificationItem(
                id = "NT-102",
                title = "Predictive Maintenance Alert",
                message = "AI Telemetry: Truck #104 brake lining at 3.2mm. Recommend inspection within 7 days.",
                timestamp = "25 min ago",
                priority = NotificationPriority.WARNING,
                category = NotificationCategory.MAINTENANCE,
                relatedEntityId = "TRK-104"
            ),
            NotificationItem(
                id = "NT-103",
                title = "Severe Weather Delay Warning",
                message = "Load LD-207 delayed in I-84 pass due to snow squall advisory. ETA pushed +3.5 hours.",
                timestamp = "1 hour ago",
                priority = NotificationPriority.WARNING,
                category = NotificationCategory.DELIVERY_DELAY,
                relatedEntityId = "LD-207"
            ),
            NotificationItem(
                id = "NT-104",
                title = "High-Value Freight Available",
                message = "New load LD-204 posted: Chicago -> Dallas ($4,850, Reefer 38k lbs). Best AI match ready.",
                timestamp = "2 hours ago",
                priority = NotificationPriority.INFO,
                category = NotificationCategory.LOAD_ASSIGNMENT,
                relatedEntityId = "LD-204"
            ),
            NotificationItem(
                id = "NT-105",
                title = "Driver On-Duty Check-In",
                message = "Dave Miller (TRK-104) completed pre-trip safety checklist with 100% pass score.",
                timestamp = "3 hours ago",
                priority = NotificationPriority.INFO,
                category = NotificationCategory.DRIVER_ISSUE,
                relatedEntityId = "DRV-01"
            )
        )

        fun getInitialRoutePlan(): RoutePlan = RoutePlan(
            id = "RT-925",
            loadId = "LD-204",
            truckId = "TRK-104",
            origin = "Chicago Logistics Hub, IL",
            destination = "Dallas Distribution Center, TX",
            totalDistanceMiles = 925,
            totalTimeFormatted = "14h 22m",
            currentProgressPercent = 0.28f,
            estimatedFuelCost = 455.0,
            recommendedSavingsSummary = "Recommended Route saves 18% fuel ($98) and 42 minutes by bypassing I-55 construction via Route 57/40 corridor.",
            delayRiskSummary = "Low congestion risk. Clear weather between St. Louis and Little Rock.",
            weatherCondition = "72°F Clear Skies • Dry Pavement",
            alternatives = listOf(
                RouteAlternative(
                    id = "ALT-1",
                    name = "AI Smart Eco Route (Recommended)",
                    distanceMiles = 925,
                    durationFormatted = "14h 22m",
                    estimatedFuelCost = 455.0,
                    fuelSavingsPercent = 18,
                    timeSavingsMinutes = 42,
                    isRecommended = true,
                    riskFactor = "Low Risk • Optimized Grade"
                ),
                RouteAlternative(
                    id = "ALT-2",
                    name = "Interstate 55 Direct",
                    distanceMiles = 938,
                    durationFormatted = "15h 04m",
                    estimatedFuelCost = 553.0,
                    fuelSavingsPercent = 0,
                    timeSavingsMinutes = 0,
                    isRecommended = false,
                    riskFactor = "Moderate Congestion • St. Louis Lane Closures"
                ),
                RouteAlternative(
                    id = "ALT-3",
                    name = "Highway 67 Scenic (Toll-Free)",
                    distanceMiles = 964,
                    durationFormatted = "16h 15m",
                    estimatedFuelCost = 585.0,
                    fuelSavingsPercent = -6,
                    timeSavingsMinutes = -73,
                    isRecommended = false,
                    riskFactor = "Multiple Signal Lights • Mountain Grade"
                )
            ),
            waypoints = listOf(
                Waypoint("Chicago Logistics Hub", "Chicago", "IL", "08:00 AM", isStop = true, isPassed = true, distanceRemainingMiles = 925),
                Waypoint("Interstate 57 Mile 112", "Champaign", "IL", "10:15 AM", isStop = false, isPassed = true, distanceRemainingMiles = 780),
                Waypoint("Current Telemetry Position", "Effingham", "IL", "11:45 AM", isStop = false, isPassed = true, distanceRemainingMiles = 690),
                Waypoint("Love's Travel Stop #420 (Mandatory Break)", "Mt Vernon", "IL", "01:30 PM", isStop = true, isPassed = false, distanceRemainingMiles = 580),
                Waypoint("Little Rock Gateway", "Little Rock", "AR", "06:45 PM", isStop = false, isPassed = false, distanceRemainingMiles = 310),
                Waypoint("Texarkana State Line Weigh Station", "Texarkana", "TX", "09:30 PM", isStop = false, isPassed = false, distanceRemainingMiles = 185),
                Waypoint("Dallas Metro Distribution Park", "Dallas", "TX", "Tomorrow 05:00 PM", isStop = true, isPassed = false, distanceRemainingMiles = 0)
            )
        )
    }
}
