package com.phone_box_app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Created by Ram Mandal on 09/10/2025
 * @System: Apple M1 Pro
 */

private val DarkColorScheme = darkColorScheme(
    primary = AppThemeColor.primary,
    secondary = AppThemeColor.secondary,
    tertiary = AppThemeColor.statusBar
)

private val LightColorScheme = lightColorScheme(
    primary = AppThemeColor.primary,
    secondary = AppThemeColor.secondary,
    tertiary = AppThemeColor.statusBar,
    onPrimary = AppThemeColor.white,
    surface = AppThemeColor.white,
    background = AppThemeColor.white,
    onBackground = AppThemeColor.white,
)

@Composable
fun ArchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppThemeColor.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}