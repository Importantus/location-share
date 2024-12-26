package digital.fischers.locationshare.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE46D6D),
    background = Color(0xffffffff),
    surfaceContainerLowest = Color(0xff202020),
    surfaceContainerLow = Color(0xff313131),
    surfaceContainerHigh = Color(0xff434343),
    onSurface = Color(0xffd7d7d7),
    onSurfaceVariant = Color(0xffa5a5a5),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE46D6D),
    background = Color(0xffffffff),
    surfaceContainerLowest = Color(0xff202020),
    surfaceContainerLow = Color(0xff313131),
    surfaceContainerHigh = Color(0xff434343),
    onSurface = Color(0xffd7d7d7),
    onSurfaceVariant = Color(0xffa5a5a5),
)

@Composable
fun LocationShareTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (view.context is Activity) {
        val window = (view.context as Activity).window
        SideEffect {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}