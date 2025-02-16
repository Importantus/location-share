package digital.fischers.locationshare.ui.map.components

import androidx.compose.runtime.Composable
import digital.fischers.locationshare.ui.map.utils.createCircleFromPolygon
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.Circle
import org.ramani.compose.Polygon

@Composable
fun Position(
    latLng: LatLng,
    accuracy: Float,
    color: String = "#202020"
) {
    Polygon(
        vertices = createCircleFromPolygon(latLng, accuracy),
        isDraggable = false,
        fillColor = color,
        opacity = 0.4F,
        borderColor = "transparent",
        zIndex = 1,
    )
    Circle(
        zIndex = 2,
        center = latLng,
        isDraggable = false,
        radius = 6.5F,
        color = color,
        borderWidth = 3.2F,
        borderColor = "#fff"
    )
}