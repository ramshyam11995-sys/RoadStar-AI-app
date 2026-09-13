package com.example.roadstar.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse and entrance animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Progress bar simulation
    var progress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        val steps = 25
        for (i in 1..steps) {
            delay(80)
            progress = i / steps.toFloat()
        }
        delay(300)
        onDismiss()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen_root"),
        color = NavyDark
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            NavyDark,
                            Color(0xFF020617)
                        )
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag: Enterprise Logistics Cloud
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI-POWERED LOGISTICS PLATFORM",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Center Content: Modern Logo & Typography
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Modern RoadStar AI Logo Badge
                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .size(170.dp)
                            .shadow(
                                elevation = 24.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = OrangePrimary.copy(alpha = 0.4f)
                            )
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White)
                            .border(2.dp, OrangePrimary.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                            .testTag("splash_logo_container"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_roadstar_ai_logo),
                            contentDescription = "RoadStar AI Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Brand Typography
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RoadStar",
                                fontWeight = FontWeight.Black,
                                fontSize = 36.sp,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = " AI",
                                fontWeight = FontWeight.Black,
                                fontSize = 36.sp,
                                color = OrangePrimary,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        Text(
                            text = "Next-Generation Trucking & Freight Operations",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Active Highlights Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FeaturePill(label = "Dynamic Dispatch")
                        FeaturePill(label = "Eco Routing")
                        FeaturePill(label = "Smart HOS")
                    }
                }

                // Bottom: Progress Loader and Quick Skip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Animated Linear Progress Bar
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Initializing Telemetry & AI Models...",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = OrangePrimary,
                            trackColor = Color.White.copy(alpha = 0.12f)
                        )
                    }

                    // Direct Enter Button
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("splash_enter_button")
                    ) {
                        Text(
                            text = "Enter RoadStar AI",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Version 2.4.0 • Enterprise Cloud Edition",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturePill(label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
