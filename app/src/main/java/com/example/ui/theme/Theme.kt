package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OnOrangeContainer,
    secondary = SlateDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = NavyDark,
    tertiary = InfoBlue,
    onTertiary = Color.White,
    tertiaryContainer = InfoBlueContainer,
    onTertiaryContainer = Color(0xFF1E3A8A),
    background = SurfaceBackground,
    onBackground = NavyDark,
    surface = SurfaceCard,
    onSurface = NavyDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateMedium,
    outline = BorderMedium,
    outlineVariant = BorderSubtle,
    error = ErrorRed,
    errorContainer = ErrorRedContainer,
    onError = Color.White,
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangeLight,
    onPrimary = NavyDark,
    primaryContainer = Color(0xFF5C2300),
    onPrimaryContainer = OrangeContainer,
    secondary = Color(0xFFE2E8F0),
    onSecondary = NavyDark,
    secondaryContainer = SlateDark,
    onSecondaryContainer = Color(0xFFF1F5F9),
    tertiary = Color(0xFF60A5FA),
    onTertiary = NavyDark,
    background = Color(0xFF090D16),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep clean white high-contrast theme primary by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
