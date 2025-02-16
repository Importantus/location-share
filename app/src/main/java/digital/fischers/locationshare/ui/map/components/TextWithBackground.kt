package digital.fischers.locationshare.ui.map.components

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.plus
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.MapApplier
import org.ramani.compose.MapObserver
import org.ramani.compose.Polygon
import org.ramani.compose.Symbol
import kotlin.math.PI

@Composable
fun TextWithBackground(
    center: LatLng,
    text: String,
    textSize: Float = 16f,
    textColor: String = "Black",
    backgroundColor: String = "White",
    borderColor: String = "Black",
    borderWidth: Float = 1.0f,
    cornerRadius: Float = 5.0f,
    padding: Float = 4.0f,
    offset: Array<Float> = arrayOf(0f, 0f),
    zIndex: Int = 1
) {
    val mapApplier = currentComposer.applier as MapApplier
    val proj = mapApplier.map.projection
    val centerLocal = proj.toScreenLocation(center) + PointF(offset[0], offset[1])
    val dpToPixel = with(LocalDensity.current) { 1.dp.toPx() }

    val recomposeState = remember { mutableStateOf(true) }
    MapObserver(onMapScaled = { recomposeState.value = !recomposeState.value })

    val textWidth = text.length * textSize * 0.6f * dpToPixel
    val textHeight = textSize * dpToPixel

    val halfWidth = textWidth / 2 + padding * dpToPixel
    val halfHeight = textHeight / 2 + padding * dpToPixel
    val radius = cornerRadius * dpToPixel

    key(recomposeState.value) {
        val rectPoints = mutableListOf<PointF>()

        // Top-left corner
        for (i in 0..20) {
            val angle = PI / 2 * (i / 20.0)
            rectPoints.add(
                PointF(
                    -halfWidth + radius * (1 - Math.cos(angle)).toFloat(),
                    -halfHeight + radius * (1 - Math.sin(angle)).toFloat()
                )
            )
        }

        // Top-right corner
        for (i in 0..20) {
            val angle = PI / 2 * ((20 - i) / 20.0)
            rectPoints.add(
                PointF(
                    halfWidth - radius * (1 - Math.cos(angle)).toFloat(),
                    -halfHeight + radius * (1 - Math.sin(angle)).toFloat()
                )
            )
        }

        // Bottom-right corner
        for (i in 0..20) {
            val angle = PI / 2 * (i / 20.0)
            rectPoints.add(
                PointF(
                    halfWidth - radius * (1 - Math.cos(angle)).toFloat(),
                    halfHeight - radius * (1 - Math.sin(angle)).toFloat()
                )
            )
        }

        // Bottom-left corner
        for (i in 0..20) {
            val angle = PI / 2 * ((20 - i) / 20.0)
            rectPoints.add(
                PointF(
                    -halfWidth + radius * (1 - Math.cos(angle)).toFloat(),
                    halfHeight - radius * (1 - Math.sin(angle)).toFloat()
                )
            )
        }

        val screenPoints = rectPoints.map { centerLocal + it }.map { proj.fromScreenLocation(it) }


        Polygon(
            zIndex = zIndex,
            vertices = screenPoints,
            fillColor = backgroundColor,
            borderColor = borderColor,
            borderWidth = borderWidth,
        )

        Symbol(
            zIndex = zIndex + 1,
            center = proj.fromScreenLocation(centerLocal),
            color = textColor,
            textColor = textColor,
            isDraggable = false,
            text = text,
            size = textSize,
            textOffset = arrayOf(0f, 0f),
            imageId = null
        )
    }
}