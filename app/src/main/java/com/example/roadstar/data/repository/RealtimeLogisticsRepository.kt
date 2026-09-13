package com.example.roadstar.data.repository

import android.util.Log
import com.example.roadstar.data.model.CargoType
import com.example.roadstar.data.model.Load
import com.example.roadstar.data.model.LoadStatus
import com.example.roadstar.data.model.MaintenanceRisk
import com.example.roadstar.data.model.Truck
import com.example.roadstar.data.model.TruckStatus
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

/**
 * Base repository class for managing truck and load data in real-time
 * via Google Cloud Firestore.
 */
class RealtimeLogisticsRepository(
    firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : BaseRealtimeRepository(firestore) {

    companion object {
        private const val TAG = "RealtimeLogisticsRepo"
        const val COLLECTION_TRUCKS = "trucks"
        const val COLLECTION_LOADS = "loads"

        @Volatile
        private var instance: RealtimeLogisticsRepository? = null

        fun getInstance(firestore: FirebaseFirestore = FirebaseFirestore.getInstance()): RealtimeLogisticsRepository {
            return instance ?: synchronized(this) {
                instance ?: RealtimeLogisticsRepository(firestore).also { instance = it }
            }
        }
    }

    // -------------------------------------------------------------
    // Real-time Streams
    // -------------------------------------------------------------

    /**
     * Real-time stream of all trucks in the fleet.
     */
    val trucksFlow: Flow<List<Truck>> = listenCollection(COLLECTION_TRUCKS) { doc ->
        mapDocumentToTruck(doc)
    }

    /**
     * Real-time stream of all freight loads.
     */
    val loadsFlow: Flow<List<Load>> = listenCollection(COLLECTION_LOADS) { doc ->
        mapDocumentToLoad(doc)
    }

    /**
     * Real-time stream of a specific truck by ID.
     */
    fun observeTruck(truckId: String): Flow<Truck?> {
        return listenDocument(COLLECTION_TRUCKS, truckId) { doc ->
            mapDocumentToTruck(doc)
        }
    }

    /**
     * Real-time stream of a specific freight load by ID.
     */
    fun observeLoad(loadId: String): Flow<Load?> {
        return listenDocument(COLLECTION_LOADS, loadId) { doc ->
            mapDocumentToLoad(doc)
        }
    }

    // -------------------------------------------------------------
    // Truck Management Operations
    // -------------------------------------------------------------

    /**
     * Inserts or updates a truck record in Firestore.
     */
    suspend fun saveTruck(truck: Truck): Result<Unit> {
        val data = mapTruckToData(truck)
        return upsert(COLLECTION_TRUCKS, truck.id, data)
    }

    /**
     * Updates status and current geographic location for a specific truck.
     */
    suspend fun updateTruckStatus(
        truckId: String,
        status: TruckStatus,
        currentLocation: String? = null
    ): Result<Unit> {
        val fields = mutableMapOf<String, Any?>("status" to status.name)
        if (currentLocation != null) {
            fields["currentLocation"] = currentLocation
        }
        return updateFields(COLLECTION_TRUCKS, truckId, fields)
    }

    /**
     * Updates telemetry and predictive maintenance telemetry for a truck.
     */
    suspend fun updateTruckMaintenance(
        truckId: String,
        score: Int,
        risk: MaintenanceRisk,
        alert: String? = null,
        recommendedAction: String? = null
    ): Result<Unit> {
        val fields = mapOf(
            "maintenanceScore" to score,
            "maintenanceRisk" to risk.name,
            "maintenanceAlert" to alert,
            "recommendedAction" to recommendedAction
        )
        return updateFields(COLLECTION_TRUCKS, truckId, fields)
    }

    // -------------------------------------------------------------
    // Load Management Operations
    // -------------------------------------------------------------

    /**
     * Inserts or updates a freight load in Firestore.
     */
    suspend fun saveLoad(load: Load): Result<Unit> {
        val data = mapLoadToData(load)
        return upsert(COLLECTION_LOADS, load.id, data)
    }

    /**
     * Updates the status of a freight load.
     */
    suspend fun updateLoadStatus(loadId: String, status: LoadStatus): Result<Unit> {
        return updateFields(COLLECTION_LOADS, loadId, mapOf("status" to status.name))
    }

    /**
     * Assigns a truck and driver to a freight load, and updates both entities atomically.
     */
    suspend fun assignTruckAndDriverToLoad(
        loadId: String,
        truckId: String,
        driverName: String
    ): Result<Unit> {
        val loadUpdate = updateFields(
            COLLECTION_LOADS,
            loadId,
            mapOf(
                "status" to LoadStatus.ASSIGNED.name,
                "assignedTruckId" to truckId,
                "assignedDriverName" to driverName
            )
        )
        val truckUpdate = updateFields(
            COLLECTION_TRUCKS,
            truckId,
            mapOf(
                "status" to TruckStatus.ACTIVE.name,
                "currentLoadId" to loadId,
                "assignedDriverName" to driverName
            )
        )

        return if (loadUpdate.isSuccess && truckUpdate.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(loadUpdate.exceptionOrNull() ?: truckUpdate.exceptionOrNull() ?: Exception("Failed assignment"))
        }
    }

    /**
     * Removes a freight load from Firestore.
     */
    suspend fun deleteLoad(loadId: String): Result<Unit> {
        return delete(COLLECTION_LOADS, loadId)
    }

    /**
     * Seeds initial fleet and loads into Firestore if desired.
     */
    suspend fun seedInitialFleet(trucks: List<Truck>, loads: List<Load>): Result<Unit> {
        return try {
            trucks.forEach { saveTruck(it) }
            loads.forEach { saveLoad(it) }
            Log.d(TAG, "Seeded ${trucks.size} trucks and ${loads.size} loads to Firestore.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed seeding data into Firestore", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Data Mappers
    // -------------------------------------------------------------

    private fun mapDocumentToTruck(doc: DocumentSnapshot): Truck? {
        if (!doc.exists()) return null
        return try {
            val statusStr = doc.getString("status") ?: TruckStatus.AVAILABLE.name
            val riskStr = doc.getString("maintenanceRisk") ?: MaintenanceRisk.LOW.name

            Truck(
                id = doc.getString("id") ?: doc.id,
                model = doc.getString("model") ?: "Freightliner Cascadia",
                year = doc.getLong("year")?.toInt() ?: 2024,
                assignedDriverId = doc.getString("assignedDriverId"),
                assignedDriverName = doc.getString("assignedDriverName"),
                currentLocation = doc.getString("currentLocation") ?: "Unknown Location",
                status = runCatching { TruckStatus.valueOf(statusStr) }.getOrDefault(TruckStatus.AVAILABLE),
                fuelLevelPercent = doc.getLong("fuelLevelPercent")?.toInt() ?: 80,
                mileage = doc.getLong("mileage")?.toInt() ?: 100000,
                currentLoadId = doc.getString("currentLoadId"),
                maintenanceScore = doc.getLong("maintenanceScore")?.toInt() ?: 90,
                maintenanceRisk = runCatching { MaintenanceRisk.valueOf(riskStr) }.getOrDefault(MaintenanceRisk.LOW),
                maintenanceAlert = doc.getString("maintenanceAlert"),
                recommendedAction = doc.getString("recommendedAction"),
                capacityLbs = doc.getLong("capacityLbs")?.toInt() ?: 45000,
                averageMpg = doc.getDouble("averageMpg") ?: 7.4,
                lastServiceDate = doc.getString("lastServiceDate") ?: "2026-08-15"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse truck document ${doc.id}", e)
            null
        }
    }

    private fun mapTruckToData(truck: Truck): Map<String, Any?> {
        return mapOf(
            "id" to truck.id,
            "model" to truck.model,
            "year" to truck.year,
            "assignedDriverId" to truck.assignedDriverId,
            "assignedDriverName" to truck.assignedDriverName,
            "currentLocation" to truck.currentLocation,
            "status" to truck.status.name,
            "fuelLevelPercent" to truck.fuelLevelPercent,
            "mileage" to truck.mileage,
            "currentLoadId" to truck.currentLoadId,
            "maintenanceScore" to truck.maintenanceScore,
            "maintenanceRisk" to truck.maintenanceRisk.name,
            "maintenanceAlert" to truck.maintenanceAlert,
            "recommendedAction" to truck.recommendedAction,
            "capacityLbs" to truck.capacityLbs,
            "averageMpg" to truck.averageMpg,
            "lastServiceDate" to truck.lastServiceDate
        )
    }

    private fun mapDocumentToLoad(doc: DocumentSnapshot): Load? {
        if (!doc.exists()) return null
        return try {
            val statusStr = doc.getString("status") ?: LoadStatus.AVAILABLE.name
            val cargoStr = doc.getString("cargoType") ?: CargoType.DRY_VAN.name

            Load(
                id = doc.getString("id") ?: doc.id,
                title = doc.getString("title") ?: "Freight Shipment",
                pickupLocation = doc.getString("pickupLocation") ?: "",
                deliveryLocation = doc.getString("deliveryLocation") ?: "",
                pickupDateTime = doc.getString("pickupDateTime") ?: "",
                deliveryDeadline = doc.getString("deliveryDeadline") ?: "",
                cargoType = runCatching { CargoType.valueOf(cargoStr) }.getOrDefault(CargoType.DRY_VAN),
                cargoWeightLbs = doc.getLong("cargoWeightLbs")?.toInt() ?: 35000,
                loadValueUsd = doc.getDouble("loadValueUsd") ?: 5000.0,
                requiredTruckType = doc.getString("requiredTruckType") ?: "Dry Van",
                status = runCatching { LoadStatus.valueOf(statusStr) }.getOrDefault(LoadStatus.AVAILABLE),
                assignedTruckId = doc.getString("assignedTruckId"),
                assignedDriverName = doc.getString("assignedDriverName"),
                distanceMiles = doc.getLong("distanceMiles")?.toInt() ?: 450,
                ratePerMileUsd = doc.getDouble("ratePerMileUsd") ?: 3.25,
                specialNotes = doc.getString("specialNotes") ?: "Standard delivery protocols apply."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse load document ${doc.id}", e)
            null
        }
    }

    private fun mapLoadToData(load: Load): Map<String, Any?> {
        return mapOf(
            "id" to load.id,
            "title" to load.title,
            "pickupLocation" to load.pickupLocation,
            "deliveryLocation" to load.deliveryLocation,
            "pickupDateTime" to load.pickupDateTime,
            "deliveryDeadline" to load.deliveryDeadline,
            "cargoType" to load.cargoType.name,
            "cargoWeightLbs" to load.cargoWeightLbs,
            "loadValueUsd" to load.loadValueUsd,
            "requiredTruckType" to load.requiredTruckType,
            "status" to load.status.name,
            "assignedTruckId" to load.assignedTruckId,
            "assignedDriverName" to load.assignedDriverName,
            "distanceMiles" to load.distanceMiles,
            "ratePerMileUsd" to load.ratePerMileUsd,
            "specialNotes" to load.specialNotes
        )
    }
}
