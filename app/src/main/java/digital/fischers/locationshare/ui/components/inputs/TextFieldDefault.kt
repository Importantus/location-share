package digital.fischers.locationshare.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldDefault(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    loading: Boolean = false
) {
    OutlinedTextField(
        label = { Text(label) },
        isError = isError,
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedLabelColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
            .fillMaxWidth(),
        suffix = {
            if (loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}