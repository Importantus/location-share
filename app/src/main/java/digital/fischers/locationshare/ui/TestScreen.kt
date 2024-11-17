package digital.fischers.locationshare.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.utils.getDeviceName

@Composable
fun Testscreen(
    viewModel: TestScreenViewModel = hiltViewModel()
) {
    val isLocationSharingEnabled by viewModel.isLocationSharingEnabled.collectAsState()
    val isDataSyncEnabled by viewModel.isDataSyncEnabled.collectAsState()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Your device name is: " + getDeviceName(context.contentResolver),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable {
                    // Copy device name to clipboard
                    val clipboard = getSystemService(context, ClipboardManager::class.java)
                    clipboard?.setPrimaryClip(ClipData.newPlainText("Device Name", getDeviceName(context.contentResolver)))
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                        Toast
                            .makeText(context, "Device name copied", Toast.LENGTH_SHORT)
                            .show()
                }
            )
            Button(
                onClick = { viewModel.toggleLocationSharing() }
            ) {
                Text(if (isDataSyncEnabled) "Disable Location Sharing" else "Enable Location Sharing")
            }
        }

    }
}