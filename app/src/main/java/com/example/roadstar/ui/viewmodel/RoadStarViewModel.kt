package com.example.roadstar.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roadstar.data.ai.GeminiService
import com.example.roadstar.data.auth.AuthUserState
import com.example.roadstar.data.auth.FirebaseAuthService
import com.example.roadstar.data.model.*
import com.example.roadstar.data.repository.RoadStarRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen(val title: String) {
    SPLASH("RoadStar AI"),
    ONBOARDING("Welcome to RoadStar AI"),
    DASHBOARD("Dashboard"),
    LOADS("Freight Loads"),
    ROUTES("AI Route Optimizer"),
    FLEET("Fleet Management"),
    COPILOT("AI Copilot"),
    NOTIFICATIONS("Notifications"),
    ACCOUNT("Account & Settings"),
    DRIVER_PORTAL("Driver In-Cab Console"),
    ANALYTICS("Fleet Analytics")
}

data class DemoStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val targetScreen: AppScreen,
    val actionButtonText: String
)

data class RoadStarUiState(
    val currentScreen: AppScreen = AppScreen.SPLASH,
    val userRole: UserRole = UserRole.FLEET_MANAGER,
    val isOnboarded: Boolean = true,
    val selectedLoad: Load? = null,
    val selectedTruck: Truck? = null,
    val showCreateLoadDialog: Boolean = false,
    val showAiMatchDialog: Boolean = false,
    val showMaintenanceDialog: Boolean = false,
    val showDriverDeliveryDialog: Boolean = false,
    val showAssistantDialog: Boolean = false,
    val aiRecommendations: List<AiMatchRecommendation> = emptyList(),
    val aiMaintenanceText: String = "",
    val isAiLoading: Boolean = false,
    val selectedRouteAlternativeId: String = "ALT-1",
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "MSG-1",
            text = "Hello! I am your RoadStar AI Logistics Copilot. How can I optimize your fleet, loads, or routes today?",
            isUser = false,
            timestamp = "Just now",
            suggestedActions = listOf(
                "Which truck should take Load #204?",
                "Which deliveries are at risk today?",
                "How can I reduce fuel costs?",
                "Show available drivers"
            )
        )
    ),
    val activeDemoStep: Int = 1,
    val isDemoTourActive: Boolean = true,
    val toastMessage: String? = null,
    val showQuickOpsMenu: Boolean = false,
    val isLiveTelemetryActive: Boolean = true
)

class RoadStarViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: RoadStarRepository = RoadStarRepository(),
    private val geminiService: GeminiService = GeminiService()
) : AndroidViewModel(application) {

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RoadStarViewModel(application) as T
                }
            }
    }

    private val authService = FirebaseAuthService.getInstance(application)
    val authUserState: StateFlow<AuthUserState> = authService.authState

    val loads: StateFlow<List<Load>> = repository.loads
    val trucks: StateFlow<List<Truck>> = repository.trucks
    val drivers: StateFlow<List<Driver>> = repository.drivers
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
    val activeRoute: StateFlow<RoutePlan> = repository.activeRoute
    val analytics: StateFlow<AnalyticsSummary> = repository.analytics

    private val _uiState = MutableStateFlow(RoadStarUiState())
    val uiState: StateFlow<RoadStarUiState> = _uiState.asStateFlow()

    init {
        // Set default selected load to LD-204
        val initialLoads = repository.loads.value
        val defaultLoad = initialLoads.find { it.id == "LD-204" } ?: initialLoads.firstOrNull()
        val defaultTruck = repository.trucks.value.find { it.id == "TRK-104" }
        _uiState.update {
            it.copy(
                selectedLoad = defaultLoad,
                selectedTruck = defaultTruck
            )
        }
        startLiveTelemetryTicker()
    }

    private fun startLiveTelemetryTicker() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(7000)
                if (_uiState.value.isLiveTelemetryActive) {
                    repository.simulateAllTrucksMovement()
                }
            }
        }
    }

    fun toggleQuickOpsMenu(show: Boolean? = null) {
        _uiState.update { it.copy(showQuickOpsMenu = show ?: !it.showQuickOpsMenu) }
    }

    fun toggleLiveTelemetry() {
        val next = !_uiState.value.isLiveTelemetryActive
        _uiState.update {
            it.copy(
                isLiveTelemetryActive = next,
                toastMessage = if (next) "🟢 Live GPS Telemetry: Streaming Active" else "⏸ Telemetry Stream Paused"
            )
        }
    }

    fun triggerEmergencyBreakdown() {
        repository.triggerEmergencyBreakdown("TRK-104")
        _uiState.update {
            it.copy(
                showQuickOpsMenu = false,
                currentScreen = AppScreen.FLEET,
                toastMessage = "🚨 CRITICAL: Truck #104 Coolant Overheat! Diverted to Des Moines."
            )
        }
    }

    fun simulateGpsMovementTick() {
        repository.simulateAllTrucksMovement()
        _uiState.update {
            it.copy(
                showQuickOpsMenu = false,
                toastMessage = "⚡ Live GPS Coordinates & Speeds Updated Across Fleet"
            )
        }
    }

    fun quickDispatchRushLoad() {
        val newLoad = repository.createLoad(
            pickupLocation = "Chicago, IL",
            deliveryLocation = "Detroit, MI",
            pickupDateTime = "Immediate (Rush)",
            deliveryDeadline = "Today 6:00 PM",
            cargoType = CargoType.AUTO_CARRIER,
            weightLbs = 38500,
            loadValue = 3200.0,
            requiredTruckType = "Dry Van 53'"
        )
        _uiState.update {
            it.copy(
                showQuickOpsMenu = false,
                selectedLoad = newLoad,
                currentScreen = AppScreen.LOADS,
                toastMessage = "📦 Rush Load ${newLoad.id} ($3,200) Dispatched to Queue!"
            )
        }
    }

    fun signInWithGoogle(context: Context = getApplication(), email: String = "marse0666@gmail.com") {
        viewModelScope.launch {
            val result = authService.signInWithGoogle(context, email)
            result.onSuccess { state ->
                _uiState.update {
                    it.copy(toastMessage = "Google Connected: ${state.email}")
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(toastMessage = "Google Sign-in: ${err.localizedMessage ?: "Failed"}")
                }
            }
        }
    }

    fun signOut(context: Context = getApplication()) {
        viewModelScope.launch {
            authService.signOut(context)
            _uiState.update {
                it.copy(toastMessage = "Signed out of Google & Firebase")
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setUserRole(role: UserRole) {
        _uiState.update {
            it.copy(
                userRole = role,
                currentScreen = if (role == UserRole.DRIVER) AppScreen.DRIVER_PORTAL else AppScreen.DASHBOARD
            )
        }
    }

    fun completeOnboarding(selectedRole: UserRole) {
        _uiState.update {
            it.copy(
                isOnboarded = true,
                userRole = selectedRole,
                currentScreen = if (selectedRole == UserRole.DRIVER) AppScreen.DRIVER_PORTAL else AppScreen.DASHBOARD
            )
        }
    }

    fun finishSplashScreen() {
        _uiState.update {
            it.copy(
                currentScreen = if (!it.isOnboarded) AppScreen.ONBOARDING else AppScreen.DASHBOARD
            )
        }
    }

    fun openSplashScreen() {
        _uiState.update {
            it.copy(currentScreen = AppScreen.SPLASH)
        }
    }

    fun selectLoad(load: Load) {
        _uiState.update { it.copy(selectedLoad = load) }
    }

    fun selectTruck(truck: Truck) {
        _uiState.update { it.copy(selectedTruck = truck) }
    }

    fun openAiMatchModal(load: Load) {
        _uiState.update {
            it.copy(
                selectedLoad = load,
                showAiMatchDialog = true,
                isAiLoading = true
            )
        }
        viewModelScope.launch {
            val recs = geminiService.getAiLoadRecommendations(load, trucks.value)
            _uiState.update {
                it.copy(
                    aiRecommendations = recs,
                    isAiLoading = false
                )
            }
        }
    }

    fun closeAiMatchModal() {
        _uiState.update { it.copy(showAiMatchDialog = false) }
    }

    fun assignRecommendation(recommendation: AiMatchRecommendation) {
        val load = _uiState.value.selectedLoad ?: return
        repository.assignTruckAndDriverToLoad(load.id, recommendation.truckId, recommendation.driverName)
        _uiState.update {
            it.copy(
                showAiMatchDialog = false,
                toastMessage = "Assigned ${recommendation.truckId} & ${recommendation.driverName} to Load ${load.id}!"
            )
        }
    }

    fun openMaintenanceModal(truck: Truck) {
        _uiState.update {
            it.copy(
                selectedTruck = truck,
                showMaintenanceDialog = true,
                isAiLoading = true
            )
        }
        viewModelScope.launch {
            val insight = geminiService.getAiMaintenanceInsight(truck)
            _uiState.update {
                it.copy(
                    aiMaintenanceText = insight,
                    isAiLoading = false
                )
            }
        }
    }

    fun closeMaintenanceModal() {
        _uiState.update { it.copy(showMaintenanceDialog = false) }
    }

    fun openCreateLoadDialog() {
        _uiState.update { it.copy(showCreateLoadDialog = true) }
    }

    fun closeCreateLoadDialog() {
        _uiState.update { it.copy(showCreateLoadDialog = false) }
    }

    fun createNewLoad(
        pickup: String,
        delivery: String,
        pickupTime: String,
        deadline: String,
        cargoType: CargoType,
        weightLbs: Int,
        valueUsd: Double,
        truckType: String
    ) {
        val newLoad = repository.createLoad(
            pickupLocation = pickup,
            deliveryLocation = delivery,
            pickupDateTime = pickupTime,
            deliveryDeadline = deadline,
            cargoType = cargoType,
            weightLbs = weightLbs,
            loadValue = valueUsd,
            requiredTruckType = truckType
        )
        _uiState.update {
            it.copy(
                showCreateLoadDialog = false,
                selectedLoad = newLoad,
                toastMessage = "Load ${newLoad.id} created successfully!"
            )
        }
    }

    fun selectRouteAlternative(altId: String) {
        _uiState.update { it.copy(selectedRouteAlternativeId = altId) }
    }

    fun openAssistantDialog() {
        _uiState.update { it.copy(showAssistantDialog = true) }
    }

    fun closeAssistantDialog() {
        _uiState.update { it.copy(showAssistantDialog = false) }
    }

    fun sendAssistantMessage(query: String) {
        val userMsg = ChatMessage(
            id = "USR-${System.currentTimeMillis()}",
            text = query,
            isUser = true,
            timestamp = "Just now"
        )
        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg,
                isAiLoading = true
            )
        }

        viewModelScope.launch {
            val context = "Active Trucks: ${trucks.value.count { it.status == TruckStatus.ACTIVE }}, Available: ${trucks.value.count { it.status == TruckStatus.AVAILABLE }}, Loads: ${loads.value.size}, Key Load: LD-204 Chicago to Dallas."
            val replyText = geminiService.askAssistant(query, context)
            val aiMsg = ChatMessage(
                id = "AI-${System.currentTimeMillis()}",
                text = replyText,
                isUser = false,
                timestamp = "Just now"
            )
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + aiMsg,
                    isAiLoading = false
                )
            }
        }
    }

    fun openDriverDeliveryDialog() {
        _uiState.update { it.copy(showDriverDeliveryDialog = true) }
    }

    fun closeDriverDeliveryDialog() {
        _uiState.update { it.copy(showDriverDeliveryDialog = false) }
    }

    fun confirmDelivery(loadId: String, recipientName: String) {
        repository.completeDelivery(loadId, recipientName)
        _uiState.update {
            it.copy(
                showDriverDeliveryDialog = false,
                toastMessage = "Delivery confirmed! Proof of delivery verified."
            )
        }
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun resetDemo() {
        repository.resetDemoData()
        _uiState.value = RoadStarUiState(
            selectedLoad = repository.loads.value.firstOrNull(),
            selectedTruck = repository.trucks.value.firstOrNull(),
            toastMessage = "Demo state reset to initial catalog."
        )
    }

    // Guided Hackathon Demo Tour Step Logic
    fun advanceDemoStep() {
        val current = _uiState.value.activeDemoStep
        when (current) {
            1 -> { // Move from Dashboard to Loads screen
                _uiState.update {
                    it.copy(
                        activeDemoStep = 2,
                        currentScreen = AppScreen.LOADS,
                        selectedLoad = loads.value.find { l -> l.id == "LD-204" }
                    )
                }
            }
            2 -> { // Open AI Matcher for LD-204
                val load = loads.value.find { l -> l.id == "LD-204" } ?: loads.value.first()
                openAiMatchModal(load)
                _uiState.update { it.copy(activeDemoStep = 3) }
            }
            3 -> { // Auto-assign top recommendation and go to Routes
                val bestRec = _uiState.value.aiRecommendations.firstOrNull() ?: AiMatchRecommendation(
                    truckId = "TRK-104",
                    truckModel = "Kenworth T680",
                    driverId = "DRV-01",
                    driverName = "Dave Miller",
                    matchScorePercent = 96,
                    reasonExplanation = "Truck #104 is closest to Chicago pickup and has available capacity.",
                    proximityMiles = 12,
                    driverHoursRemaining = 9.5,
                    fuelEfficiencyRating = "7.6 MPG (High)",
                    capacityUtilizationPercent = 84
                )
                assignRecommendation(bestRec)
                _uiState.update {
                    it.copy(
                        activeDemoStep = 4,
                        currentScreen = AppScreen.ROUTES
                    )
                }
            }
            4 -> { // From Routes to Driver Mobile Experience
                _uiState.update {
                    it.copy(
                        activeDemoStep = 5,
                        userRole = UserRole.DRIVER,
                        currentScreen = AppScreen.DRIVER_PORTAL
                    )
                }
            }
            5 -> { // From Driver to Predictive Maintenance on Fleet
                _uiState.update {
                    it.copy(
                        activeDemoStep = 6,
                        userRole = UserRole.FLEET_MANAGER,
                        currentScreen = AppScreen.FLEET
                    )
                }
                val trk104 = trucks.value.find { t -> t.id == "TRK-104" }
                if (trk104 != null) {
                    openMaintenanceModal(trk104)
                }
            }
            6 -> { // From Maintenance to AI Logistics Assistant
                closeMaintenanceModal()
                _uiState.update {
                    it.copy(
                        activeDemoStep = 7,
                        currentScreen = AppScreen.DASHBOARD
                    )
                }
                openAssistantDialog()
            }
            7 -> { // From Assistant to Full Notifications & Analytics
                closeAssistantDialog()
                _uiState.update {
                    it.copy(
                        activeDemoStep = 8,
                        currentScreen = AppScreen.NOTIFICATIONS
                    )
                }
            }
            8 -> { // Final step: Analytics
                _uiState.update {
                    it.copy(
                        activeDemoStep = 1,
                        currentScreen = AppScreen.ANALYTICS,
                        toastMessage = "Demo tour complete! Explore all live tabs."
                    )
                }
            }
        }
    }
}
