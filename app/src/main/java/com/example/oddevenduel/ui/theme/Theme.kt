package com.example.oddevenduel.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.oddevenduel.domain.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = PlayerPrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = PlayerContainerDark,
    onPrimaryContainer = Color(0xFFDBEAFE),

    secondary = CpuSecondaryDark,
    onSecondary = Color(0xFF1E293B),
    secondaryContainer = CpuContainerDark,
    onSecondaryContainer = Color(0xFFFEF3C7),

    tertiary = ChallengeTertiaryDark,
    onTertiary = Color(0xFF1E293B),
    tertiaryContainer = ChallengeContainerDark,
    onTertiaryContainer = Color(0xFFEDE9FE),

    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = PlayerPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = PlayerContainerLight,
    onPrimaryContainer = Color(0xFF1E3A8A),

    secondary = CpuSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = CpuContainerLight,
    onSecondaryContainer = Color(0xFF78350F),

    tertiary = ChallengeTertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = ChallengeContainerLight,
    onTertiaryContainer = Color(0xFF4C1D95),

    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant
)

@Composable
fun OddEvenDuelTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}