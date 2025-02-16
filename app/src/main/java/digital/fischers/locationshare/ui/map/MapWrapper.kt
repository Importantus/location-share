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
    screen: @Composable () -> Unit,
) {

    val context = LocalContext.current

    val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                Log.d("Gesture", "onScroll: $distanceX, $distanceY")
                onPan(Offset(-distanceX, -distanceY))
                return false
            }
        })

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (showMap) {
            Log.d("Map", "Rebuilding map")
            Map(
                userLocation = userLocation,
                friends = friends,
                cameraPosition = cameraPosition,
                onRotate = {
                    onRotate(it.toFloat())
                },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { motionEvent ->
                    false
                }
                .zIndex(1f)
        ) {
            screen()
        }
    }
}