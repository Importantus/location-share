package digital.fischers.locationshare.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toHexString(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}