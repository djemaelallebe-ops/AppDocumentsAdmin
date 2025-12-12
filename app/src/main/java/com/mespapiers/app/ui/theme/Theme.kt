package com.mespapiers.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =============================================================================
// THÈME SOBRE "PAPER WHITE" - Interface épurée et élégante
// =============================================================================

private val SobreLightColorScheme = lightColorScheme(
    // Couleurs primaires - Anthracite élégant
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = SurfaceVariant,
    onPrimaryContainer = Primary,

    // Couleurs secondaires - Gris neutres
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = Secondary,

    // Accent - Violet sobre pour les actions importantes
    tertiary = Accent,
    onTertiary = OnPrimary,
    tertiaryContainer = AccentLight.copy(alpha = 0.15f),
    onTertiaryContainer = Accent,

    // Surfaces - Blanc pur et propre
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    // États
    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,

    // Contours et bordures subtiles
    outline = Border,
    outlineVariant = Divider,

    // Surfaces inversées pour snackbars
    inverseSurface = Primary,
    inverseOnSurface = OnPrimary,
    inversePrimary = AccentLight
)

@Composable
fun MesPapiersTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = SobreLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar blanche avec icônes sombres pour un look sobre
            window.statusBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
