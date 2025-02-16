package digital.fischers.locationshare.ui

import android.location.Location.distanceBetween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.repositories.LocationRepository
import digital.fischers.locationshare.domain.repositories.UserRepository
import digital.fischers.locationshare.domain.types.FriendUIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.maplibre.android.geometry.LatLng
import org.ramani.compose.CameraPosition
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val hasSeenOnboarding = userRepository.hasSeenOnboardingStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = true
    )

    val isLoggedIn = userRepository.isLoggedInStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = false
    )

    private val zoom = MutableStateFlow(15.0f)

    private val bearing = MutableStateFlow(0.0)

    private val pan = MutableStateFlow(Offset.Zero)


    private val _locationPermissionState = MutableStateFlow<PermissionState?>(null)
    private val locationPermissionState: StateFlow<PermissionState?> = _locationPermissionState


    @OptIn(ExperimentalCoroutinesApi::class)
    val userLocation = locationPermissionState
        .flatMapLatest { permissionState ->
            if (permissionState?.status?.isGranted == true) {
                locationRepository.getCurrentLocation().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000L),
                    initialValue = null
                )
            } else {
                MutableStateFlow(null)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val friends: StateFlow<List<FriendUIState>> = combine(
        friendRepository.getFriendsStream(),
        userLocation
    ) { friendsList, userLocation ->
        friendsList.map { friend ->
            combine(
                flowOf(friend),
                friend.location ?: flowOf(null)
            ) { _, friendLocation ->
                val distance = userLocation?.let { userLoc ->
                    friendLocation?.let { friendLoc ->
                        calculateDistance(
                            userLoc.latitude,
                            userLoc.longitude,
                            friendLoc.latitude,
                            friendLoc.longitude
                        )
                    }
                }
                FriendUIState(
                    id = friend.id,
                    name = friend.name,
                    username = friend.username,
                    theirShareId = friend.theirShareId,
                    userShareId = friend.userShareId,
                    distance = distance,
                    location = friendLocation
                )
            }
        }.let { combinedFlows ->
            combine(combinedFlows) { it.toList() }
        }
    }.flatMapLatest { it }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private var trackTarget by mutableStateOf(
        TrackTarget(
            type = TrackTargetType.USER_LOCATION
        )
    )

    val cameraPosition = combine(
        userLocation,
        friends,
        zoom,
        pan,
        bearing
    ) { location, friendsList, zoom, panOffset, bearing ->
        // Logic to determine the camera position based on user location and friends list
        CameraPosition(
            target = when (trackTarget.type) {
                TrackTargetType.USER_LOCATION -> location?.let { LatLng(it.latitude, it.longitude) }
                    ?: friendsList.firstOrNull()
                        ?.location?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(
                        53.866670,
                        10.684946
                    )

                TrackTargetType.FRIEND -> friendsList.firstOrNull { it.id == trackTarget.value }
                    ?.location?.let { LatLng(it.latitude, it.longitude) }

                TrackTargetType.MANUAL -> null
            },
            zoom = zoom.toDouble(),
            bearing = bearing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CameraPosition(
            target = LatLng(
                userLocation.value?.latitude ?: 53.866670,
                userLocation.value?.longitude ?: 10.684946
            ),
            zoom = 15.0,
            bearing = 0.0,
        )
    )

    fun updateLocationPermissionState(permissionState: PermissionState) {
        _locationPermissionState.value = permissionState
    }

    fun setCameraToTrackFriend(friendId: String) {
        pan.value = Offset.Zero
        trackTarget = TrackTarget(
            type = TrackTargetType.FRIEND,
            value = friendId
        )
    }

    fun setCameraToTrackUserLocation() {
        pan.value = Offset.Zero
        trackTarget = TrackTarget(
            type = TrackTargetType.USER_LOCATION
        )
    }

    fun setCameraToManual() {
        trackTarget = TrackTarget(
            type = TrackTargetType.MANUAL
        )
    }

    fun updateZoom(newZoom: Float) {
        zoom.value *= newZoom
    }

    fun updatePan(newPan: Offset) {
        pan.value += newPan
    }

    fun updateRotation(newAngle: Float) {
        bearing.value += (-newAngle)
    }
}

data class TrackTarget(
    val type: TrackTargetType,
    val value: String? = null
)

enum class TrackTargetType() {
    FRIEND,
    USER_LOCATION,
    MANUAL
}
