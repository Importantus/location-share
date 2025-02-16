package digital.fischers.locationshare.ui.map

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import digital.fischers.locationshare.domain.types.FriendUIState
import digital.fischers.locationshare.ui.map.components.Position
import digital.fischers.locationshare.ui.map.components.UserLocation
import digital.fischers.locationshare.utils.toHexString
import kotlinx.coroutines.flow.StateFlow
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre
import org.ramani.compose.MapObserver
import org.ramani.compose.Margins
import org.ramani.compose.UiSettings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Map(
    userLocation: StateFlow<Location?>,
    friends: StateFlow<List<FriendUIState>>,
    cameraPosition: StateFlow<CameraPosition>,
    onZoom: () -> Unit = {},
    onRotate: (Double) -> Unit = {},
) {
    val userLocationState by userLocation.collectAsState()
    val friendsState by friends.collectAsState()
    val cameraPositionState by cameraPosition.collectAsState()

    MapLibre(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(0f),
        cameraPosition = cameraPositionState,
        styleBuilder = Style.Builder()
            .fromUri("asset://map/styles/graybeard.json"),
        uiSettings = UiSettings(
            compassMargins = Margins(0, 100, 16, 0)
        )
    ) {
        MapObserver(
            onMapScaled = onZoom,
            onMapRotated = onRotate
        )

        userLocationState?.let {
            val isOlderThan1Minute = it.time < System.currentTimeMillis() - 60 * 1000
            Position(
                latLng = LatLng(it.latitude, it.longitude),
                accuracy = it.accuracy,
                color = if (isOlderThan1Minute) MaterialTheme.colorScheme.onBackground.toHexString() else MaterialTheme.colorScheme.primary.toHexString()
            )
        }

        friendsState.filter { it.location != null }.forEach { friend ->
            friend.location?.let {
                UserLocation(
                    name = friend.name,
                    latLng = LatLng(it.latitude, it.longitude),
                    accuracy = it.accuracy
                )
            }
        }
    }
}

