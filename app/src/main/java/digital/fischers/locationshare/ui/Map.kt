package digital.fischers.locationshare.ui

import android.graphics.Color
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.LocationPriority
import org.ramani.compose.LocationRequestProperties
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import org.ramani.compose.Margins
import org.ramani.compose.Polygon
import org.ramani.compose.UiSettings
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Map() {
    val cameraPosition = rememberSaveable {
        mutableStateOf(
            CameraPosition(
                target = LatLng(51.9282459, 10.5954777),
                zoom = 15.0
            )
        )
    }

    val locationProperties: MutableState<LocationRequestProperties> =
        rememberSaveable {
            mutableStateOf(
                LocationRequestProperties(
                    priority = LocationPriority.PRIORITY_HIGH_ACCURACY,
                    interval = 1000,
                    fastestInterval = 500,
                )
            )
        }

    val userLocation = rememberSaveable { mutableStateOf(Location(null)) }

    MapLibre(
        modifier = Modifier.fillMaxSize(),
        cameraPosition = cameraPosition.value,
        styleBuilder = Style.Builder()
            .fromUri("asset://map/styles/graybeard.json"),
        uiSettings = UiSettings(
            compassMargins = Margins(0, 100, 16, 0)
        ),
        locationStyling = LocationStyling(
            foregroundTintColor = Color.parseColor("#E46D6D"),
            accuracyColor = Color.parseColor("#E46D6D"),
            accuracyAlpha = 0.4F,
        ),
        locationRequestProperties = locationProperties.value,
        userLocation = userLocation,
    ) {
        UserLocation(
            name = "Malte",
            latLng = LatLng(51.9282459, 10.5954777),
            accuracy = 60.0
        )
    }
    Button(
        onClick = {
            cameraPosition.value = CameraPosition(cameraPosition.value).apply {
                this.target = LatLng(
                    userLocation.value.latitude,
                    userLocation.value.longitude
                )
            }
        },
    ) {
        Text(text = "Center on device location")
    }
}

@Composable
fun UserLocation(
    name: String,
    latLng: LatLng,
    accuracy: Double,
) {
    Polygon(
        vertices = createCircleFromPolygon(latLng, accuracy),
        isDraggable = false,
        fillColor = "#202020",
        opacity = 0.4F,
        borderColor = "transparent",
        zIndex = 1
    )
    Circle(
        zIndex = 2,
        center = latLng,
        isDraggable = false,
        radius = 6.5F,
        color = "#202020",
        borderWidth = 3.2F,
        borderColor = "#fff"
    )
    TextWithBackground(
        text = name,
        zIndex = 3,
        backgroundColor = "#202020",
        textColor = "#fff",
        borderColor = "transparent",
        center = latLng,
        offset = arrayOf(0F, 90F),
    )
}

fun createCircleFromPolygon(center: LatLng, radiusInMeter: Double): List<LatLng> {
    val numberOfSides = 100
    val vertices = mutableListOf<LatLng>()

    for (i in 0 until numberOfSides) {
        val angle = Math.toRadians(i.toDouble() * 360 / numberOfSides)
        val dx = radiusInMeter * cos(angle)
        val dy = radiusInMeter * sin(angle)
        val lat = center.latitude + (180 / Math.PI) * (dy / 6378137)
        val lon =
            center.longitude + (180 / Math.PI) * (dx / 6378137) / cos(center.latitude * Math.PI / 180)
        vertices.add(LatLng(lat, lon))
    }

    return vertices
}

