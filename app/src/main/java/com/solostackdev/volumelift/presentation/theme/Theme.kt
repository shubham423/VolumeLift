package com.solostackdev.volumelift.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.solostackdev.volumelift.domain.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = PrimaryLight,
    secondary = PrimaryLight,
    onSecondary = Background,
    secondaryContainer = PrimaryDark,
    onSecondaryContainer = PrimaryLight,
    tertiary = UnderTarget,
    onTertiary = Background,
    tertiaryContainer = UnderTargetBg,
    background = Background,
    onBackground = TextPrimary,
    surface = Background,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = Background,
    surfaceContainerLow = Background,
    surfaceContainer = Surface,
    surfaceContainerHigh = Surface,
    surfaceContainerHighest = SurfaceVariant,
    outline = Border,
    outlineVariant = SurfaceVariant,
    error = OverTarget,
    onError = TextPrimary,
    errorContainer = OverTargetBg,
    onErrorContainer = OverTarget
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(16.dp)
)

@Composable
fun VolumeLiftTheme(
    themeMode: ThemeMode = ThemeMode.System,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Force dark theme — the design tokens are dark-only
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
