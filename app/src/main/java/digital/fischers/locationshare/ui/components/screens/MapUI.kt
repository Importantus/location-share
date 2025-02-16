package digital.fischers.locationshare.ui.components.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.utils.consumePointerInput

@Composable
fun MapUI(
    isLoading: Boolean = false,
    error: APIError? = null,
    onErrorConfirm: () -> Unit = {},
    appbar: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    BaseScreen(
        isLoading = isLoading,
        appbar = appbar,
        error = error,
        onErrorConfirm = onErrorConfirm
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier.consumePointerInput()
            ) {
                content()
            }
        }
    }
}