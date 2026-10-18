package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TheSkullsColorScheme = darkColorScheme(
    primary = SkullWhite,
    onPrimary = SkullBgDark,
    primaryContainer = SkullSurfaceCardElevated,
    onPrimaryContainer = SkullWhite,
    secondary = SkullSilver,
    onSecondary = SkullBgDark,
    secondaryContainer = SkullSurfaceCard,
    onSecondaryContainer = SkullSilver,
    tertiary = SkullChrome,
    onTertiary = SkullBgDark,
    background = SkullBgDark,
    onBackground = SkullTextPrimary,
    surface = SkullSurfaceDark,
    onSurface = SkullTextPrimary,
    surfaceVariant = SkullSurfaceCard,
    onSurfaceVariant = SkullTextSecondary,
    outline = SkullBorderDark,
    outlineVariant = SkullDividerDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = TheSkullsColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SkullBgDark.toArgb()
                window.navigationBarColor = SkullBgDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
