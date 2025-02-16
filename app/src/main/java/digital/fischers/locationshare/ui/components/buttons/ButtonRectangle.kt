package digital.fischers.locationshare.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonRectangle(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    layout: ButtonLayout = ButtonLayout.MEDIUM,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    minWidth: Int = 160,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(enabled) { onClick() }
            .padding(horizontal = layout.paddingX.dp, vertical = layout.paddingY.dp)
            .widthIn(min = minWidth.dp)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = TextStyle(
                fontSize = layout.textSize.sp
            )
        )
    }
}

enum class ButtonLayout(val paddingX: Int, val paddingY: Int, val textSize: Int) {
    SMALL(5, 8, 14),
    MEDIUM(16, 10, 16),
    LARGE(24, 14, 18)
}