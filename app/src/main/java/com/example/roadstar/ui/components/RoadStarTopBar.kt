package com.example.roadstar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.roadstar.data.auth.AuthUserState
import com.example.roadstar.data.model.UserRole
import com.example.ui.theme.*
import com.example.roadstar.ui.viewmodel.AppScreen
import com.example.roadstar.ui.viewmodel.RoadStarUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadStarTopBar(
    uiState: RoadStarUiState,
    authUserState: AuthUserState? = null,
    trucksCount: Int = 6,
    onToggleSidebar: () -> Unit,
    onOpenCopilot: () -> Unit,
    onOpenQuickOps: () -> Unit,
    onAdvanceDemo: () -> Unit,
    onResetDemo: () -> Unit,
    onSimulateGpsTick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Sidebar Toggle Button + Brand Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sidebar Toggle Button
                    IconButton(
                        onClick = onToggleSidebar,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("sidebar_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Sidebar",
                            tint = NavyDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RoadStar",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = NavyDark
                            )
                            Text(
                                text = " AI",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = OrangePrimary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isLiveTelemetryActive) SuccessGreen else SlateMuted)
                            )
                            Text(
                                text = if (uiState.isLiveTelemetryActive) "Live Telemetry" else "Paused",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.isLiveTelemetryActive) SuccessGreen else SlateMuted
                            )
                        }
                    }
                }

                // Right: Quick Actions (AI Copilot & Operations Avatar)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Interactive AI Copilot Quick Button
                    FilledTonalButton(
                        onClick = onOpenCopilot,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = OrangeContainer,
                            contentColor = OrangeDark
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("top_bar_copilot_quick")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = OrangePrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copilot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Interactive Profile Avatar with Live Status Dot
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier.testTag("top_bar_google_avatar")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (authUserState?.isAuthenticated == true) Color(0xFFE8F0FE) else Color(0xFFFFF7ED),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { onOpenQuickOps() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (authUserState?.isAuthenticated == true && !authUserState.photoUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = authUserState.photoUrl,
                                        contentDescription = "Profile Avatar",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Open Operations Menu",
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        // Live status indicator dot on avatar
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                }
            }

            // Interactive Live Telemetry & Quick Action Bar
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary)
                        )
                        Text(
                            text = "TRK-104 I-80 W • 68 MPH • 94% Nominal",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Quick GPS Tick Action
                        OutlinedButton(
                            onClick = onSimulateGpsTick,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp).testTag("quick_gps_tick_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Tick GPS", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        // Demo Tour Advance Button
                        Button(
                            onClick = onAdvanceDemo,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(24.dp)
                                .testTag("advance_demo_step")
                        ) {
                            Text(
                                text = "Next Tour →",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
