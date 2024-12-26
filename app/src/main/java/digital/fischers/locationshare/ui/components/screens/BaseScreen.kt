package digital.fischers.locationshare.ui.components.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import digital.fischers.locationshare.ui.components.loading.BlockingLoading

@Composable
fun BaseScreen(
    isLoading: Boolean = false,
    background: (@Composable () -> Unit)? = null,
    appbar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
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
    }
}