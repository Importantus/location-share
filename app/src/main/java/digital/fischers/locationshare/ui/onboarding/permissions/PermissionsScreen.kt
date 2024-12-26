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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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
        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION -> "Super! Wir haben Zugriff auf deinen Standort, wenn die App geöffnet ist. Aber um deinen Standort auch im Hintergrund erfassen und teilen zu können, benötigen wir auch die Berechtigung, deinen Standort im Hintergrund zu ermitteln."
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "Alles erledigt! Wir haben Zugriff auf deinen Standort im Hintergrund und wenn die App geöffnet ist."
        else -> "Wir brauchen die Berechtigung, deinen genauen Standort zu ermitteln, wenn die App geöffnet ist. Dieser Standort wird nur verwendet, um ihn auf der Karte anzuzeigen."
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
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION -> "Wir brauchen deine Erlaubnis!"
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "Erlaubnis erteilt!"
                    else -> "Wir brauchen deine Erlaubnis!"
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
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            when (highestPermissionState?.permission) {
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                    ButtonRectangle(
                        "Weiter geht's",
                        onClick = onNextNavigation
                    )
                }

                android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    ButtonRectangle(
                        "Hintergrund Zugriff erlauben",
                        onClick = {
                            coroutineScope.launch {
                                accessBackgroundLocationPermission.launchPermissionRequest()
                            }
                        }
                    )
                }

                else -> {
                    ButtonRectangle(
                        "Standortzugriff erlauben",
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