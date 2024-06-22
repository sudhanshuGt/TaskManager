package dev.sudhanshu.taskmanager.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Purple80,
    primaryVariant = Color.Black,
    secondary = PurpleGrey80,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = Purple40,
    primaryVariant = Color.White,
    secondary = PurpleGrey40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

object ThemeManager {
    var isDarkModeEnabled by mutableStateOf(false)
}

@Composable
fun TaskManagerTheme(content: @Composable () -> Unit) {
    val colors = if (ThemeManager.isDarkModeEnabled) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = colors.primaryVariant,
        darkIcons = !ThemeManager.isDarkModeEnabled
    )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}
