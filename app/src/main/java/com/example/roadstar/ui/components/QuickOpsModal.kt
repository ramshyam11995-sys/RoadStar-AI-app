package com.example.roadstar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.roadstar.data.auth.AuthUserState
import com.example.roadstar.data.model.UserRole
import com.example.ui.theme.*

/**
 * Interactive Operations Command Dialog triggered by tapping the profile avatar.
 * Allows the user to switch roles immediately, simulate live GPS movements,
 * trigger critical breakdown alerts, and dispatch rush loads.
 */
@Composable
fun QuickOpsModal(
    currentRole: UserRole,
    authUserState: AuthUserState?,
    isLiveTelemetryActive: Boolean,
    onRoleChange: (UserRole) -> Unit,
    onToggleLiveTelemetry: () -> Unit,
    onSimulateGpsTick: () -> Unit,
    onTriggerBreakdown: () -> Unit,
    onQuickDispatchLoad: () -> Unit,
    onOpenCopilot: () -> Unit,
    onResetDemo: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with User / Profile Avatar & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (authUserState?.isAuthenticated == true) Color(0xFFE8F0FE) else Color(0xFFFFF7ED),
                            border = BorderStroke(2.dp, OrangePrimary),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (authUserState?.isAuthenticated == true && !authUserState.photoUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = authUserState.photoUrl,
                                        contentDescription = "User Avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = authUserState?.displayName?.take(1)?.uppercase() ?: "R",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = OrangeDark
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = authUserState?.displayName ?: "Apex Fleet Admin",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = NavyDark
                            )
                            Text(
                                text = authUserState?.email ?: "DOT #3928104 • Chicago Hub",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Interactive Role Switcher
                Text(
                    text = "SWITCH ACTIVE ROLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoleSelectionButton(
                        title = "Fleet Mgr",
                        icon = Icons.Default.Business,
                        isSelected = currentRole == UserRole.FLEET_MANAGER,
                        onClick = { onRoleChange(UserRole.FLEET_MANAGER) },
                        modifier = Modifier.weight(1f)
                    )
                    RoleSelectionButton(
                        title = "Dispatcher",
                        icon = Icons.Default.Hub,
                        isSelected = currentRole == UserRole.DISPATCHER,
                        onClick = { onRoleChange(UserRole.DISPATCHER) },
                        modifier = Modifier.weight(1f)
                    )
                    RoleSelectionButton(
                        title = "Driver",
                        icon = Icons.Default.LocalShipping,
                        isSelected = currentRole == UserRole.DRIVER,
                        onClick = { onRoleChange(UserRole.DRIVER) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Real-time Operational Actions ("DO REAL THINGS")
                Text(
                    text = "OPERATIONAL SIMULATION & ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Action 1: Live GPS Telemetry Movement
                    OpsActionRow(
                        title = "Simulate Live GPS Movement",
                        subtitle = "Advances truck coordinates & speeds in real-time",
                        icon = Icons.Default.Navigation,
                        iconTint = Color(0xFF2563EB),
                        onClick = onSimulateGpsTick
                    )

                    // Action 2: Trigger Emergency Breakdown
                    OpsActionRow(
                        title = "Simulate Engine Breakdown",
                        subtitle = "Overheats Truck #104 coolant & generates critical alert",
                        icon = Icons.Default.Warning,
                        iconTint = ErrorRed,
                        onClick = onTriggerBreakdown
                    )

                    // Action 3: Quick Dispatch Rush Load
                    OpsActionRow(
                        title = "Dispatch Priority Rush Load",
                        subtitle = "Creates $3,200 rush automotive load to queue",
                        icon = Icons.Default.AddBox,
                        iconTint = OrangePrimary,
                        onClick = onQuickDispatchLoad
                    )

                    // Action 4: Launch AI Copilot
                    OpsActionRow(
                        title = "Launch Gemini 2.5 Copilot",
                        subtitle = "Autonomous route dispatch intelligence",
                        icon = Icons.Default.AutoAwesome,
                        iconTint = OrangeDark,
                        onClick = onOpenCopilot
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                // Footer with Live Telemetry Toggle & Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onResetDemo,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = SlateMuted
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Demo", color = SlateMuted, fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = onToggleLiveTelemetry,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isLiveTelemetryActive) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                            contentColor = if (isLiveTelemetryActive) SuccessGreen else SlateMuted
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isLiveTelemetryActive) SuccessGreen else SlateMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiveTelemetryActive) "Live GPS Active" else "GPS Paused",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleSelectionButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) OrangePrimary else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isSelected) OrangePrimary else BorderSubtle),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else NavyDark,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else NavyDark
            )
        }
    }
}

@Composable
private fun OpsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NavyDark
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = SlateMuted
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SlateMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
