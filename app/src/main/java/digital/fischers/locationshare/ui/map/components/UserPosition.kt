package digital.fischers.locationshare.ui.map.components

import androidx.compose.runtime.Composable
import org.maplibre.android.geometry.LatLng

@Composable
fun UserLocation(
    name: String,
    latLng: LatLng,
    accuracy: Float,
    id: FriendId? = null,
    onClick: (String) -> Unit = {}
) {
    val color = "#202020"

    Position(
        latLng = latLng,
        accuracy = accuracy,
        color = color,
        id = id,
        onClick = onClick
    )
    TextWithBackground(
        text = name,
        zIndex = 3,
        backgroundColor = color,
        textColor = "#fff",
        borderColor = "transparent",
        center = latLng,
        offset = arrayOf(0F, 90F)
    )
}