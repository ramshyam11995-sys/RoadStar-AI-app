package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun AccountScreen(
    currentRole: UserRole,
    authUserState: AuthUserState? = null,
    onSignInWithGoogle: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onRoleChange: (UserRole) -> Unit,
    onResetDemo: () -> Unit,
    onOpenSplash: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var aiEcoRoutingEnabled by remember { mutableStateOf(true) }
    var predictiveMaintenanceAlerts by remember { mutableStateOf(true) }
    var automatedDispatchNotifications by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RoadStar Official Branding Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_roadstar_ai_logo),
                                contentDescription = "RoadStar AI Modern Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("RoadStar", fontWeight = FontWeight.Black, fontSize = 18.sp, color = NavyDark)
                                Text(" AI", fontWeight = FontWeight.Black, fontSize = 18.sp, color = OrangePrimary)
                            }
                            Text("Autonomous & AI-Powered Logistics OS", fontSize = 12.sp, color = SlateMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Apex Logistics Holdings • DOT #3928104", fontSize = 11.sp, color = SlateDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onOpenSplash,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateDark),
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("preview_splash_screen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View RoadStar AI Splash Screen",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Google & Firebase Authentication Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(
                    1.5.dp,
                    if (authUserState?.isAuthenticated == true) Color(0xFF4285F4).copy(alpha = 0.5f) else BorderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Google 'G' Icon Badge
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color(0xFF4285F4)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Account & Firebase",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = NavyDark
                                )
                                Text(
                                    text = if (authUserState?.isAuthenticated == true) "Connected via Firebase Auth" else "Not Signed In",
                                    fontSize = 11.sp,
                                    color = if (authUserState?.isAuthenticated == true) Color(0xFF16A34A) else SlateMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Auth Status Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (authUserState?.isAuthenticated == true) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (authUserState?.isAuthenticated == true) Color(0xFF16A34A) else SlateMuted)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (authUserState?.isAuthenticated == true) "Active" else "Offline",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (authUserState?.isAuthenticated == true) Color(0xFF166534) else SlateDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (authUserState?.isAuthenticated == true) {
                        // User info when signed in
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!authUserState.photoUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = authUserState.photoUrl,
                                        contentDescription = "Google Avatar",
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4285F4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = authUserState.displayName?.take(1)?.uppercase() ?: "G",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = authUserState.displayName ?: "Google User",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NavyDark
                                    )
                                    Text(
                                        text = authUserState.email ?: "user@google.com",
                                        fontSize = 12.sp,
                                        color = SlateDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "UID: ${authUserState.uid?.take(14)}... • Provider: ${authUserState.providerId}",
                                        fontSize = 10.sp,
                                        color = SlateMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Logout Button
                        Button(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEF2F2),
                                contentColor = Color(0xFFDC2626)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("google_sign_out_button")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign Out of Google", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Sign-In invitation when signed out
                        Text(
                            text = "Sign in with your Google account to sync fleet dispatch, autonomous telemetry, and cloud operations with Firebase.",
                            fontSize = 12.sp,
                            color = SlateDark,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onSignInWithGoogle,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("google_sign_in_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("G", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF4285F4))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sign In with Google", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Operational Role Switcher
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current User Role Perspective", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
                    Text("Switch between logistics personas to test each interface", fontSize = 12.sp, color = SlateMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    UserRole.values().forEach { role ->
                        val isSelected = currentRole == role
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) OrangeContainer else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isSelected) OrangePrimary else BorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onRoleChange(role) }
                                .padding(vertical = 3.dp)
                                .testTag("account_role_${role.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = role.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) OrangeDark else NavyDark
                                    )
                                    Text(
                                        text = when (role) {
                                            UserRole.FLEET_MANAGER -> "Overview, Financials, Fleet health & KPIs"
                                            UserRole.DISPATCHER -> "Load matching, route alternative selection & scheduling"
                                            UserRole.DRIVER -> "In-cab console, route navigation, POD signature"
                                        },
                                        fontSize = 11.sp,
                                        color = SlateMedium
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Platform & AI Configuration Toggles
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Automation Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automated Eco-Routing", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)
                            Text("Prioritize fuel efficiency over shortest toll mileage", fontSize = 11.sp, color = SlateMuted)
                        }
                        Switch(
                            checked = aiEcoRoutingEnabled,
                            onCheckedChange = { aiEcoRoutingEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary, checkedTrackColor = OrangeContainer)
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Predictive Maintenance Telemetry", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)
                            Text("Alert dispatch 500 miles before component failure", fontSize = 11.sp, color = SlateMuted)
                        }
                        Switch(
                            checked = predictiveMaintenanceAlerts,
                            onCheckedChange = { predictiveMaintenanceAlerts = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary, checkedTrackColor = OrangeContainer)
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Instant Driver Push Notifications", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyDark)
                            Text("Dispatch alerts via in-cab tablet instantly", fontSize = 11.sp, color = SlateMuted)
                        }
                        Switch(
                            checked = automatedDispatchNotifications,
                            onCheckedChange = { automatedDispatchNotifications = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary, checkedTrackColor = OrangeContainer)
                        )
                    }
                }
            }
        }

        // Developer & Demo Control Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hackathon Demo Controls", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyDark)
                    Text("Reset loads, trucks, drivers, and notifications to original catalog", fontSize = 12.sp, color = SlateMuted)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onResetDemo,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangePrimary),
                        border = BorderStroke(1.dp, OrangePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("reset_demo_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Demo Data to Initial State", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
