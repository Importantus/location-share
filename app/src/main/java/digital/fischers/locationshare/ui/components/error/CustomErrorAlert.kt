package digital.fischers.locationshare.ui.components.error

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import digital.fischers.locationshare.R
import digital.fischers.locationshare.data.remote.APIError

@Composable
fun CustomErrorAlert(
    error: APIError,
    customFallbackTitle: String? = null,
    customFallbackMessage: String? = null,
    onConfirm: () -> Unit
) {
    var title = stringResource(id = R.string.network_error)
    val desc = when (error) {
        is APIError.CustomError -> {
            when (error.code) {
                400 -> stringResource(id = R.string.error_400)
                401 -> stringResource(id = R.string.error_401)
                403 -> stringResource(id = R.string.error_403)
                404 -> stringResource(id = R.string.error_404)
                500 -> stringResource(id = R.string.error_500)
                else -> {
                    title = customFallbackTitle ?: stringResource(id = R.string.network_error)
                    customFallbackMessage ?: stringResource(id = R.string.error_generic)
                }
            }
        }

        APIError.NetworkError -> {
            stringResource(id = R.string.network_error_desc)
        }

        APIError.UnknownError -> {
            title = customFallbackTitle ?: stringResource(id = R.string.network_error)
            customFallbackMessage ?: stringResource(id = R.string.unknown_error_desc)
        }
    }

    AlertDialog(
        onDismissRequest = {},
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(id = R.string.ok),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(title)
        },
        text = {
            Text(desc)
        }
    )
}