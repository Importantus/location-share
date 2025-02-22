package digital.fischers.locationshare.ui.map

import android.location.Location
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import digital.fischers.locationshare.domain.types.FriendUIState
import kotlinx.coroutines.flow.StateFlow
import org.ramani.compose.CameraPosition

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapWrapper(
    userLocation: StateFlow<Location?>,
    friends: StateFlow<List<FriendUIState>>,
    cameraPosition: StateFlow<CameraPosition>,
    showMap: Boolean = true,
    onZoom: (Float) -> Unit = {},
    onPan: (Offset) -> Unit = {},
    onRotate: (Float) -> Unit = {},
    onFriendClick: (String) -> Unit = {},
    screen: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (showMap) {
            Map(
                userLocation = userLocation,
                friends = friends,
                cameraPosition = cameraPosition,
                onRotate = {
                    onRotate(it.toFloat())
                },
                onFriendClick = onFriendClick
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { _ ->
                    false
                }
                .zIndex(1f)
        ) {
            screen()
        }
    }
}