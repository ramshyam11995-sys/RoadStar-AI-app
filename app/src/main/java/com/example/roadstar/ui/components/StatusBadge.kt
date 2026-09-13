package com.example.roadstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.*
import com.example.ui.theme.*

@Composable
fun LoadStatusBadge(status: LoadStatus, modifier: Modifier = Modifier) {
    val bg: Color
    val textColor: Color
    val border: Color

    when (status) {
        LoadStatus.AVAILABLE -> {
            bg = Color(0xFFEFF6FF)
            textColor = Color(0xFF1D4ED8)
            border = Color(0xFFBFDBFE)
        }
        LoadStatus.ASSIGNED -> {
            bg = Color(0xFFF5F3FF)
            textColor = Color(0xFF6D28D9)
            border = Color(0xFFDDD6FE)
        }
        LoadStatus.IN_TRANSIT -> {
            bg = OrangeContainer
            textColor = OrangeDark
            border = Color(0xFFFFD8BF)
        }
        LoadStatus.DELIVERED -> {
            bg = SuccessGreenContainer
            textColor = Color(0xFF047857)
            border = Color(0xFFA7F3D0)
        }
        LoadStatus.DELAYED -> {
            bg = ErrorRedContainer
            textColor = Color(0xFFB91C1C)
            border = Color(0xFFFECACA)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun TruckStatusBadge(status: TruckStatus, modifier: Modifier = Modifier) {
    val bg: Color
    val textColor: Color

    when (status) {
        TruckStatus.ACTIVE -> {
            bg = SuccessGreenContainer
            textColor = Color(0xFF047857)
        }
        TruckStatus.AVAILABLE -> {
            bg = Color(0xFFEFF6FF)
            textColor = Color(0xFF1D4ED8)
        }
        TruckStatus.MAINTENANCE -> {
            bg = WarningAmberContainer
            textColor = Color(0xFFB45309)
        }
        TruckStatus.OFFLINE -> {
            bg = Color(0xFFF1F5F9)
            textColor = SlateMuted
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun MaintenanceRiskBadge(risk: MaintenanceRisk, score: Int, modifier: Modifier = Modifier) {
    val bg: Color
    val textColor: Color

    when (risk) {
        MaintenanceRisk.LOW -> {
            bg = SuccessGreenContainer
            textColor = Color(0xFF047857)
        }
        MaintenanceRisk.MEDIUM -> {
            bg = WarningAmberContainer
            textColor = Color(0xFFB45309)
        }
        MaintenanceRisk.CRITICAL -> {
            bg = ErrorRedContainer
            textColor = Color(0xFFB91C1C)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "Health $score% • ${risk.label}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun NotificationPriorityBadge(priority: NotificationPriority, modifier: Modifier = Modifier) {
    val bg: Color
    val textColor: Color

    when (priority) {
        NotificationPriority.CRITICAL -> {
            bg = ErrorRedContainer
            textColor = ErrorRed
        }
        NotificationPriority.WARNING -> {
            bg = WarningAmberContainer
            textColor = WarningAmber
        }
        NotificationPriority.INFO -> {
            bg = InfoBlueContainer
            textColor = InfoBlue
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = priority.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
