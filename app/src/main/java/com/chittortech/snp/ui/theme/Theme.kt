package com.chittortech.snp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary             = SnpPrimary,
    onPrimary           = Color.White,
    primaryContainer    = SnpPrimaryLight,
    onPrimaryContainer  = SnpPrimaryDark,
    secondary           = SnpGold,
    onSecondary         = Color.White,
    secondaryContainer  = SnpGoldLight,
    onSecondaryContainer = SnpGold,
    tertiary            = SnpAmber,
    onTertiary          = Color.White,
    tertiaryContainer   = SnpAmberLight,
    error               = SnpRed,
    onError             = Color.White,
    errorContainer      = SnpRedLight,
    background          = SnpBackground,
    onBackground        = TextPrimary,
    surface             = SnpCardWhite,
    onSurface           = TextPrimary,
    surfaceVariant      = SnpSurfaceVariant,
    onSurfaceVariant    = TextSecondary,
    outline             = SnpBorder,
    outlineVariant      = SnpBorder
)

@Composable
fun SmartNagarParishadTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = SnpTypography,
        content     = content
    )
}

