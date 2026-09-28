package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MetallicCyanPrimary,
    onPrimary = MetallicCyanOnPrimary,
    primaryContainer = MetallicCyanContainer,
    onPrimaryContainer = MetallicCyanOnContainer,
    secondary = CobaltSecondary,
    onSecondary = Color.White,
    secondaryContainer = CobaltContainer,
    onSecondaryContainer = Color(0xFFDBEAFE),
    tertiary = SteelTertiary,
    onTertiary = Color(0xFF0F172A),
    background = LosslessBackground,
    onBackground = SteelLight,
    surface = LosslessSurface,
    onSurface = SteelLight,
    surfaceVariant = LosslessSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = LosslessCardBorder,
    error = StatusRed
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF2563EB),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = Color(0xFF475569),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    error = StatusRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek lossless dark theme matching the LS branding
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

