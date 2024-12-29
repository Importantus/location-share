package digital.fischers.locationshare.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import digital.fischers.locationshare.R

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        label = { Text(label) },
        isError = isError,
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
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
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        suffix = {
            IconToggleButton(
                checked = passwordVisible,
                onCheckedChange = { passwordVisible = it },
                modifier = Modifier.size(20.dp)
            ) {
                if (passwordVisible) {
                    Icon(
                        painterResource(R.drawable.eye_closed),
                        contentDescription = "Hide password"
                    )
                } else {
                    Icon(painterResource(R.drawable.eye_open), contentDescription = "Show password")
                }
            }
        }
    )
}