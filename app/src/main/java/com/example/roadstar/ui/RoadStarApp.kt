package com.example.roadstar.ui

import android.app.Application
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roadstar.data.model.UserRole
import com.example.roadstar.ui.components.QuickOpsModal
import com.example.roadstar.ui.components.RoadStarBottomNav
import com.example.roadstar.ui.components.RoadStarSidebar
import com.example.roadstar.ui.components.RoadStarTopBar
import com.example.roadstar.ui.screens.*
import com.example.roadstar.ui.viewmodel.AppScreen
import com.example.roadstar.ui.viewmodel.RoadStarViewModel
import kotlinx.coroutines.launch

@Composable
fun RoadStarApp(
    viewModel: RoadStarViewModel = viewModel(
        factory = RoadStarViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authUserState by viewModel.authUserState.collectAsStateWithLifecycle()
    val loads by viewModel.loads.collectAsStateWithLifecycle()
    val trucks by viewModel.trucks.collectAsStateWithLifecycle()
    val drivers by viewModel.drivers.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val activeRoute by viewModel.activeRoute.collectAsStateWithLifecycle()
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Show toast message when present in state
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    if (uiState.currentScreen == AppScreen.SPLASH) {
        SplashScreen(
            onDismiss = { viewModel.finishSplashScreen() }
        )
    } else if (!uiState.isOnboarded || uiState.currentScreen == AppScreen.ONBOARDING) {
        OnboardingScreen(
            authUserState = authUserState,
            onSignInWithGoogle = { viewModel.signInWithGoogle(context) },
            onComplete = { selectedRole ->
                viewModel.completeOnboarding(selectedRole)
            }
        )
    } else {
        val unreadNotifications = notifications.count { !it.isRead }

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                RoadStarSidebar(
                    currentScreen = uiState.currentScreen,
                    trucksCount = trucks.size,
                    authUserState = authUserState,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    },
                    onCloseSidebar = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    RoadStarTopBar(
                        uiState = uiState,
                        authUserState = authUserState,
                        trucksCount = trucks.size,
                        onToggleSidebar = {
                            coroutineScope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        onOpenCopilot = { viewModel.navigateTo(AppScreen.COPILOT) },
                        onOpenQuickOps = { viewModel.toggleQuickOpsMenu(true) },
                        onAdvanceDemo = { viewModel.advanceDemoStep() },
                        onResetDemo = { viewModel.resetDemo() },
                        onSimulateGpsTick = { viewModel.simulateGpsMovementTick() }
                    )
                },
                bottomBar = {
                    RoadStarBottomNav(
                        currentScreen = uiState.currentScreen,
                        userRole = uiState.userRole,
                        unreadNotificationsCount = unreadNotifications,
                        onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                },
                contentWindowInsets = WindowInsets.statusBars
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (uiState.currentScreen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(
                                onDismiss = { viewModel.finishSplashScreen() }
                            )
                        }
                        AppScreen.ONBOARDING -> {
                            OnboardingScreen(
                                authUserState = authUserState,
                                onSignInWithGoogle = { viewModel.signInWithGoogle(context) },
                                onComplete = { selectedRole ->
                                    viewModel.completeOnboarding(selectedRole)
                                }
                            )
                        }
                        AppScreen.DASHBOARD -> {
                            DashboardScreen(
                                uiState = uiState,
                                loads = loads,
                                trucks = trucks,
                                analytics = analytics,
                                onNavigate = { screen -> viewModel.navigateTo(screen) },
                                onSelectLoad = { load -> viewModel.selectLoad(load) },
                                onOpenAiMatch = { load -> viewModel.openAiMatchModal(load) },
                                onOpenMaintenance = { truck -> viewModel.openMaintenanceModal(truck) }
                            )
                        }
                        AppScreen.LOADS -> {
                            LoadsScreen(
                                loads = loads,
                                selectedLoad = uiState.selectedLoad,
                                onSelectLoad = { load -> viewModel.selectLoad(load) },
                                onOpenAiMatch = { load -> viewModel.openAiMatchModal(load) },
                                onOpenCreateLoad = { viewModel.openCreateLoadDialog() }
                            )
                        }
                        AppScreen.ROUTES -> {
                            RoutesScreen(
                                routePlan = activeRoute,
                                selectedAlternativeId = uiState.selectedRouteAlternativeId,
                                onSelectAlternative = { altId -> viewModel.selectRouteAlternative(altId) }
                            )
                        }
                        AppScreen.FLEET -> {
                            FleetScreen(
                                trucks = trucks,
                                drivers = drivers,
                                selectedTruck = uiState.selectedTruck,
                                onSelectTruck = { truck -> viewModel.selectTruck(truck) },
                                onOpenMaintenance = { truck -> viewModel.openMaintenanceModal(truck) }
                            )
                        }
                        AppScreen.COPILOT -> {
                            AiCopilotScreen(
                                messages = uiState.chatMessages,
                                trucks = trucks,
                                loads = loads,
                                isLoading = uiState.isAiLoading,
                                onSendMessage = { query -> viewModel.sendAssistantMessage(query) }
                            )
                        }
                        AppScreen.DRIVER_PORTAL -> {
                            DriverScreen(
                                currentLoad = uiState.selectedLoad ?: loads.find { it.id == "LD-204" },
                                routePlan = activeRoute,
                                onOpenCompleteDelivery = { viewModel.openDriverDeliveryDialog() },
                                onOpenEmergencyReport = {
                                    Toast.makeText(context, "Roadside Alert dispatched to Apex Emergency Response Network!", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                        AppScreen.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                onMarkRead = { id -> viewModel.markNotificationRead(id) },
                                onMarkAllRead = { viewModel.markAllNotificationsRead() }
                            )
                        }
                        AppScreen.ANALYTICS -> {
                            AnalyticsScreen(
                                analytics = analytics,
                                drivers = drivers
                            )
                        }
                        AppScreen.ACCOUNT -> {
                            AccountScreen(
                                currentRole = uiState.userRole,
                                authUserState = authUserState,
                                onSignInWithGoogle = { viewModel.signInWithGoogle(context) },
                                onSignOut = { viewModel.signOut() },
                                onRoleChange = { role -> viewModel.setUserRole(role) },
                                onResetDemo = { viewModel.resetDemo() },
                                onOpenSplash = { viewModel.openSplashScreen() }
                            )
                        }
                    }
                }
            }
        }

        // Modals & Dialogs
        if (uiState.showQuickOpsMenu) {
            QuickOpsModal(
                currentRole = uiState.userRole,
                authUserState = authUserState,
                isLiveTelemetryActive = uiState.isLiveTelemetryActive,
                onRoleChange = { role: UserRole ->
                    viewModel.setUserRole(role)
                    viewModel.toggleQuickOpsMenu(false)
                },
                onToggleLiveTelemetry = { viewModel.toggleLiveTelemetry() },
                onSimulateGpsTick = { viewModel.simulateGpsMovementTick() },
                onTriggerBreakdown = { viewModel.triggerEmergencyBreakdown() },
                onQuickDispatchLoad = { viewModel.quickDispatchRushLoad() },
                onOpenCopilot = {
                    viewModel.navigateTo(AppScreen.COPILOT)
                    viewModel.toggleQuickOpsMenu(false)
                },
                onResetDemo = {
                    viewModel.resetDemo()
                    viewModel.toggleQuickOpsMenu(false)
                },
                onDismiss = { viewModel.toggleQuickOpsMenu(false) }
            )
        }

        if (uiState.showAiMatchDialog && uiState.selectedLoad != null) {
            AiMatchDialog(
                load = uiState.selectedLoad!!,
                recommendations = uiState.aiRecommendations,
                isLoading = uiState.isAiLoading,
                onAssign = { rec -> viewModel.assignRecommendation(rec) },
                onDismiss = { viewModel.closeAiMatchModal() }
            )
        }

        if (uiState.showCreateLoadDialog) {
            CreateLoadDialog(
                onDismiss = { viewModel.closeCreateLoadDialog() },
                onSubmit = { pickup, delivery, pickupTime, deadline, cargoType, weight, value, truckType ->
                    viewModel.createNewLoad(
                        pickup = pickup,
                        delivery = delivery,
                        pickupTime = pickupTime,
                        deadline = deadline,
                        cargoType = cargoType,
                        weightLbs = weight,
                        valueUsd = value,
                        truckType = truckType
                    )
                }
            )
        }

        if (uiState.showMaintenanceDialog && uiState.selectedTruck != null) {
            MaintenanceModal(
                truck = uiState.selectedTruck!!,
                aiInsightText = uiState.aiMaintenanceText,
                isLoading = uiState.isAiLoading,
                onDismiss = { viewModel.closeMaintenanceModal() },
                onScheduleService = {
                    Toast.makeText(context, "Service bay reservation requested for ${uiState.selectedTruck?.id}!", Toast.LENGTH_SHORT).show()
                    viewModel.closeMaintenanceModal()
                }
            )
        }

        if (uiState.showDriverDeliveryDialog) {
            DriverDeliveryDialog(
                load = uiState.selectedLoad ?: loads.find { it.id == "LD-204" },
                onDismiss = { viewModel.closeDriverDeliveryDialog() },
                onConfirmDelivery = { loadId, recipientName ->
                    viewModel.confirmDelivery(loadId, recipientName)
                }
            )
        }

        if (uiState.showAssistantDialog) {
            AssistantDialog(
                messages = uiState.chatMessages,
                isLoading = uiState.isAiLoading,
                onSendMessage = { query -> viewModel.sendAssistantMessage(query) },
                onDismiss = { viewModel.closeAssistantDialog() }
            )
        }
    }
}
