package com.prox.deals.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = ProxGreen,
    onPrimary = Color.White,
    primaryContainer = ProxGreenLight,
    onPrimaryContainer = ProxGreenDark,
    secondary = ProxAmber,
    secondaryContainer = ProxAmberLight,
    background = ProxBackground,
    onBackground = ProxText,
    surface = ProxSurface,
    onSurface = ProxText,
    error = ProxError
)

private val DarkColors = darkColorScheme(
    primary = ProxGreenLight,
    onPrimary = ProxGreenDark,
    primaryContainer = ProxGreenDark,
    secondary = ProxAmber,
    background = Color(0xFF121512),
    surface = Color(0xFF1B1F1B)
)

// A small typography scale with intentional weights. Bold headlines for prices
// and product names, lighter body text for supporting info.
private val ProxTypography = Typography(
    headlineSmall = Typography().headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
    bodyMedium = Typography().bodyMedium.copy(lineHeight = 20.sp)
)

@Composable
fun ProxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = ProxTypography,
        content = content
    )
}
