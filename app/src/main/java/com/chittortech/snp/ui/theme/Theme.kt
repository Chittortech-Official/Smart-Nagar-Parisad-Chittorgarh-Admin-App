package com.chittortech.snp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Decent, elegant modern rounded shape (10.dp) for executive cards & boxes
val SnpCardShape = RoundedCornerShape(10.dp)
val SnpSquareShape = SnpCardShape
val SnpBadgeShape = RoundedCornerShape(6.dp)

val SnpShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = SnpBadgeShape,
    medium = SnpCardShape,
    large = SnpCardShape,
    extraLarge = RoundedCornerShape(16.dp)
)

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
        shapes      = SnpShapes,
        content     = content
    )
}

