package com.mosque.community.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SoftGold,
    onPrimary = MidnightInk,
    primaryContainer = SoftGoldContainer,
    onPrimaryContainer = OnSoftGoldContainer,
    inversePrimary = SoftGoldDark,

    secondary = SageAccent,
    onSecondary = MidnightInk,
    secondaryContainer = SageContainer,
    onSecondaryContainer = OnSageContainer,

    tertiary = SoftGoldLight,
    onTertiary = MidnightInk,

    background = MidnightInk,
    onBackground = TextPrimary,

    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightCardSurface,
    onSurfaceVariant = TextSecondary,

    outline = MidnightOutline,
    outlineVariant = MidnightElevated
)

@Composable
fun MosqueCommunityTheme(
    darkTheme: Boolean = true, // We default to the rich Midnight Ink theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            try {
                val window = (view.context as? Activity)?.window
                if (window != null) {
                    window.statusBarColor = MidnightInk.toArgb()
                    window.navigationBarColor = MidnightInk.toArgb()
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
                }
            } catch (t: Throwable) {
                // Safeguard against OEM-specific window controller issues
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
