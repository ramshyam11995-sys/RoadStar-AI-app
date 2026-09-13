package com.example.roadstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.UserRole
import com.example.ui.theme.*
import com.example.roadstar.ui.viewmodel.AppScreen

data class NavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun RoadStarBottomNav(
    currentScreen: AppScreen,
    userRole: UserRole,
    unreadNotificationsCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = if (userRole == UserRole.DRIVER) {
        listOf(
            NavItem(AppScreen.DRIVER_PORTAL, "In-Cab", Icons.Default.LocalShipping, Icons.Outlined.LocalShipping),
            NavItem(AppScreen.ROUTES, "Map Route", Icons.Default.Navigation, Icons.Outlined.Navigation),
            NavItem(AppScreen.NOTIFICATIONS, "Alerts", Icons.Default.Notifications, Icons.Outlined.Notifications, unreadNotificationsCount),
            NavItem(AppScreen.ACCOUNT, "Account", Icons.Default.Person, Icons.Outlined.Person)
        )
    } else {
        listOf(
            NavItem(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
            NavItem(AppScreen.LOADS, "Loads", Icons.Default.Inventory2, Icons.Outlined.Inventory2),
            NavItem(AppScreen.ROUTES, "Routes", Icons.Default.AltRoute, Icons.Outlined.AltRoute),
            NavItem(AppScreen.FLEET, "Fleet", Icons.Default.LocalShipping, Icons.Outlined.LocalShipping),
            NavItem(AppScreen.NOTIFICATIONS, "Alerts", Icons.Default.Notifications, Icons.Outlined.Notifications, unreadNotificationsCount),
            NavItem(AppScreen.ACCOUNT, "Account", Icons.Default.Person, Icons.Outlined.Person)
        )
    }

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentScreen == item.screen
                val activeColor = OrangePrimary
                val inactiveColor = SlateMuted

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("nav_item_${item.label.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge(
                                    containerColor = ErrorRed,
                                    contentColor = Color.White
                                ) {
                                    Text(item.badgeCount.toString(), fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) OrangeContainer else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) activeColor else inactiveColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                }
            }
        }
    }
}
