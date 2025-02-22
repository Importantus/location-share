package digital.fischers.locationshare.ui.onboarding.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import digital.fischers.locationshare.R
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.screens.BaseScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    onBackNavigation: () -> Unit,
    onNextNavigation: () -> Unit
) {

    val accessFineLocationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val accessCoarseLocationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val accessBackgroundLocationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    val highestPermissionState: PermissionState? =
        if (accessBackgroundLocationPermission.status.isGranted) {
            accessBackgroundLocationPermission
        } else if (accessFineLocationPermission.status.isGranted) {
            accessFineLocationPermission
        } else if (accessCoarseLocationPermission.status.isGranted) {
            accessCoarseLocationPermission
        } else {
            null
        }

    val descriptionText = when (highestPermissionState?.permission) {
        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION -> stringResource(
            R.string.background_permission_desc
        )

        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> stringResource(R.string.all_set)
        else -> stringResource(R.string.permission_desc)
    }

    val coroutineScope = rememberCoroutineScope()

    BaseScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (highestPermissionState?.permission) {
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION -> stringResource(
                        R.string.we_need_permission
                    )

                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> stringResource(R.string.permission_granted)
                    else -> stringResource(R.string.we_need_permission)
                },
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
            )
            Text(
                text = descriptionText,
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            when (highestPermissionState?.permission) {
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                    ButtonRectangle(
                        stringResource(R.string.onboarding_continue),
                        onClick = onNextNavigation
                    )
                }

                android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    ButtonRectangle(
                        stringResource(R.string.allow_background_access),
                        onClick = {
                            coroutineScope.launch {
                                accessBackgroundLocationPermission.launchPermissionRequest()
                            }
                        }
                    )
                }

                else -> {
                    ButtonRectangle(
                        stringResource(R.string.allow_location_access),
                        onClick = {
                            coroutineScope.launch {
                                accessFineLocationPermission.launchPermissionRequest()
                            }
                        }
                    )
                }
            }

        }
    }
}