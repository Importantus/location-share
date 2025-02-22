package digital.fischers.locationshare.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import digital.fischers.locationshare.R

@Composable
fun WarningDialog(
    title: String,
    description: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = confirmText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    color = MaterialTheme.colorScheme.onSurface,
                    text = stringResource(R.string.cancel)
                )
            }
        },
        title = {
            Text(title)
        },
        text = {
            Text(description)
        }
    )
}