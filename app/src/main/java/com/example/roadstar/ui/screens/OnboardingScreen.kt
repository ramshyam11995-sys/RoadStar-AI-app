package com.example.roadstar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.roadstar.data.auth.AuthUserState
import com.example.roadstar.data.model.UserRole
import com.example.ui.theme.*

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val highlight: String
)

@Composable
fun OnboardingScreen(
    authUserState: AuthUserState? = null,
    onSignInWithGoogle: () -> Unit = {},
    onComplete: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlide by remember { mutableStateOf(0) }
    var selectedRole by remember { mutableStateOf(UserRole.FLEET_MANAGER) }

    val slides = listOf(
        OnboardingSlide(
            title = "Manage Your Fleet",
            subtitle = "Real-time GPS telemetry, driver duty status, and instant asset visibility across North America.",
            highlight = "Live Telemetry"
        ),
        OnboardingSlide(
            title = "Optimize Every Load",
            subtitle = "AI-powered freight matching pairs cargo weight, truck capacity, and driver HOS hours for maximum profit.",
            highlight = "96% Match Accuracy"
        ),
        OnboardingSlide(
            title = "Drive Smarter With AI",
            subtitle = "Intelligent eco-routing saves up to 18% fuel while predictive maintenance prevents catastrophic roadside downtime.",
            highlight = "-18% Fuel Savings"
        )
    )

    Surface(
        color = Color.White,
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Logo & Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_roadstar_ai_logo),
                            contentDescription = "RoadStar AI Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "RoadStar AI",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = NavyDark
                    )
                }

                TextButton(
                    onClick = { onComplete(selectedRole) },
                    modifier = Modifier.testTag("skip_onboarding")
                ) {
                    Text(
                        text = "Skip",
                        color = SlateMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Hero Graphic
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_trucking),
                        contentDescription = "RoadStar Fleet Illustration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Slide Content & Indicators
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Feature badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(OrangeContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = slides[currentSlide].highlight,
                        color = OrangePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = slides[currentSlide].title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = NavyDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = slides[currentSlide].subtitle,
                    fontSize = 14.sp,
                    color = SlateMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dots indicator
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    slides.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (currentSlide == index) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (currentSlide == index) OrangePrimary else Color(0xFFCBD5E1))
                                .clickable { currentSlide = index }
                        )
                    }
                }
            }

            // Role Selector & Action Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select your operational role:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = selectedRole == role
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) OrangePrimary else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) OrangePrimary else BorderSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedRole = role }
                                .testTag("role_${role.name.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = role.label.split(" ").first(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else NavyDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Google Sign In Integration
                if (authUserState?.isAuthenticated == true) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Connected: ${authUserState.displayName ?: authUserState.email}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onSignInWithGoogle,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("onboarding_google_signin")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4285F4),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("G", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In with Google", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Primary Large Action Button (Min 48dp target)
                Button(
                    onClick = {
                        if (currentSlide < slides.size - 1) {
                            currentSlide++
                        } else {
                            onComplete(selectedRole)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_primary_button")
                ) {
                    Text(
                        text = if (currentSlide < slides.size - 1) "Next →" else "Enter RoadStar AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
