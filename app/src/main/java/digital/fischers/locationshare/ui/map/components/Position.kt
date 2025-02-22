package digital.fischers.locationshare.ui.map.components

import androidx.compose.runtime.Composable
import com.google.gson.Gson
import com.google.gson.JsonNull
import digital.fischers.locationshare.ui.map.utils.createCircleFromPolygon
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.Circle
import org.ramani.compose.Polygon

data class FriendId (
    val id: String
)

@Composable
fun Position(
    latLng: LatLng,
    accuracy: Float,
    color: String = "#202020",
    id: FriendId? = null,
    onClick: (String) -> Unit = {}
) {
    Polygon(
        vertices = createCircleFromPolygon(latLng, accuracy),
        isDraggable = false,
        fillColor = color,
        opacity = 0.2F,
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
        borderColor = "#fff",
        data = if(id != null) Gson().toJsonTree(id) else JsonNull.INSTANCE,
        onClick = {
            onClick(Gson().fromJson(it, FriendId::class.java).id)
        }
    )
}