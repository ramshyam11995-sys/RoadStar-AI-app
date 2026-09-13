package com.example

import com.example.roadstar.data.model.CargoType
import com.example.roadstar.data.model.Load
import com.example.roadstar.data.model.LoadStatus
import com.example.roadstar.data.model.MaintenanceRisk
import com.example.roadstar.data.model.Truck
import com.example.roadstar.data.model.TruckStatus
import com.example.roadstar.data.repository.RealtimeLogisticsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RealtimeRepositoryUnitTest {

    @Test
    fun testTruckModelIntegrity() {
        val truck = Truck(
            id = "TRK-101",
            model = "Peterbilt 579 UltraLoft",
            currentLocation = "Dallas, TX",
            status = TruckStatus.ACTIVE,
            fuelLevelPercent = 88,
            mileage = 142000,
            maintenanceScore = 94,
            maintenanceRisk = MaintenanceRisk.LOW
        )

        assertEquals("TRK-101", truck.id)
        assertEquals(TruckStatus.ACTIVE, truck.status)
        assertEquals(94, truck.maintenanceScore)
        assertEquals(MaintenanceRisk.LOW, truck.maintenanceRisk)
    }

    @Test
    fun testLoadModelIntegrity() {
        val load = Load(
            id = "LD-201",
            title = "Pharmaceutical Supplies",
            pickupLocation = "Chicago, IL",
            deliveryLocation = "Detroit, MI",
            pickupDateTime = "08:00 AM",
            deliveryDeadline = "05:00 PM",
            cargoType = CargoType.REFRIGERATED,
            cargoWeightLbs = 28000,
            loadValueUsd = 85000.0,
            requiredTruckType = "Reefer",
            status = LoadStatus.AVAILABLE
        )

        assertEquals("LD-201", load.id)
        assertEquals(CargoType.REFRIGERATED, load.cargoType)
        assertEquals(LoadStatus.AVAILABLE, load.status)
        assertEquals(28000, load.cargoWeightLbs)
    }

    @Test
    fun testRepositoryConstants() {
        assertEquals("trucks", RealtimeLogisticsRepository.COLLECTION_TRUCKS)
        assertEquals("loads", RealtimeLogisticsRepository.COLLECTION_LOADS)
    }
}
