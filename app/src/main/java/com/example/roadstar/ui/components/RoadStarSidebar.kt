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
import androidx.compose.runtime.Composable
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
import com.example.roadstar.ui.viewmodel.AppScreen
import com.example.ui.theme.*

/**
 * Sidebar Navigation Drawer featuring strictly:
 * 1. 🚛 Fleet
 * 2. ✨ AI Copilot
 * Kept directly above:
 * 3. 👤 Account
 */
@Composable
fun RoadStarSidebar(
    currentScreen: AppScreen,
    trucksCount: Int,
    authUserState: AuthUserState?,
    onNavigate: (AppScreen) -> Unit,
    onCloseSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            // Header: Brand & Cloud Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_roadstar_ai_logo),
                            contentDescription = "RoadStar AI Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RoadStar",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = NavyDark
                            )
                            Text(
                                text = " AI",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = OrangePrimary
                            )
                        }
                        Text(
                            text = "Next-Gen Logistics OS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateMuted
                        )
                    }
                }

                IconButton(
                    onClick = onCloseSidebar,
                    modifier = Modifier.testTag("close_sidebar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Sidebar",
                        tint = SlateMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Realtime Connectivity Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Column {
                        Text(
                            text = "Cloud Firestore Live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "Apex Logistics Hub • USDOT #3928104",
                            fontSize = 10.sp,
                            color = SlateMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "OPERATIONS CONSOLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 1. 🚛 Fleet Functional Section
            val isFleetSelected = currentScreen == AppScreen.FLEET
            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Fleet",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isFleetSelected) OrangePrimary else NavyDark
                            )
                            Text(
                                text = "Real-time Units & Driver ELD",
                                fontSize = 11.sp,
                                color = if (isFleetSelected) OrangeDark else SlateMuted
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isFleetSelected) OrangePrimary else OrangeContainer
                        ) {
                            Text(
                                text = "$trucksCount Units",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFleetSelected) Color.White else OrangeDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "Fleet Section",
                        tint = if (isFleetSelected) OrangePrimary else SlateDark,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = isFleetSelected,
                onClick = {
                    onNavigate(AppScreen.FLEET)
                    onCloseSidebar()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = OrangeContainer,
                    unselectedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("sidebar_item_fleet")
            )

            // 2. ✨ AI Copilot Functional Section (directly around/after Fleet)
            val isCopilotSelected = currentScreen == AppScreen.COPILOT
            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AI Copilot",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isCopilotSelected) OrangePrimary else NavyDark
                            )
                            Text(
                                text = "Gemini Dispatch Intelligence",
                                fontSize = 11.sp,
                                color = if (isCopilotSelected) OrangeDark else SlateMuted
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCopilotSelected) OrangePrimary else Color(0xFFEFF6FF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = if (isCopilotSelected) Color.White else Color(0xFF2563EB)
                                )
                                Text(
                                    text = "AI Ready",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCopilotSelected) Color.White else Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Copilot Section",
                        tint = OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = isCopilotSelected,
                onClick = {
                    onNavigate(AppScreen.COPILOT)
                    onCloseSidebar()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = OrangeContainer,
                    unselectedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("sidebar_item_ai_copilot")
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = BorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // 3. 👤 Account (Kept directly below Fleet & AI Copilot)
            Text(
                text = "USER & SYSTEM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            val isAccountSelected = currentScreen == AppScreen.ACCOUNT
            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isAccountSelected) OrangePrimary else NavyDark
                            )
                            Text(
                                text = authUserState?.displayName ?: "Profile & Settings",
                                fontSize = 11.sp,
                                color = SlateMuted,
                                maxLines = 1
                            )
                        }
                        if (authUserState?.isAuthenticated == true) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F0FE)
                            ) {
                                Text(
                                    text = "Google Sync",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1A73E8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                icon = {
                    if (authUserState?.isAuthenticated == true && !authUserState.photoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = authUserState.photoUrl,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account Section",
                            tint = if (isAccountSelected) OrangePrimary else SlateDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                selected = isAccountSelected,
                onClick = {
                    onNavigate(AppScreen.ACCOUNT)
                    onCloseSidebar()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = OrangeContainer,
                    unselectedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("sidebar_item_account")
            )

            Spacer(modifier = Modifier.weight(1f))

            // Sidebar Footer
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Apex Fleet Secure",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time ELD compliance & autonomous dispatch active.",
                        color = SlateLight,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
