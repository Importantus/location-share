package digital.fischers.locationshare.ui.components.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.ui.components.error.CustomErrorAlert
import digital.fischers.locationshare.ui.components.loading.BlockingLoading

@Composable
fun BaseScreen(
    isLoading: Boolean = false,
    error: APIError? = null,
    onErrorConfirm: () -> Unit = {},
    contentPadding: Boolean = true,
    background: (@Composable () -> Unit)? = null,
    appbar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        background?.invoke()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            appbar?.invoke()
            content()
        }
        if (isLoading) {
            BlockingLoading()
        }
        if (error != null) {
            CustomErrorAlert(error, onConfirm = onErrorConfirm)
        }
    }
}