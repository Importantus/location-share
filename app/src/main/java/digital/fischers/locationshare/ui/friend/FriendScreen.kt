package digital.fischers.locationshare.ui.friend

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.R
import digital.fischers.locationshare.domain.types.FriendUIState
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.cards.ContentCard
import digital.fischers.locationshare.ui.components.dialogs.WarningDialog
import digital.fischers.locationshare.ui.components.navigation.CustomAppBar
import digital.fischers.locationshare.ui.components.screens.MapUI
import digital.fischers.locationshare.ui.home.LocationVisibleBox
import digital.fischers.locationshare.utils.getTimeDifference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FriendScreen(
    onBackNavigation: () -> Unit,
    onAddShareNavigation: () -> Unit = {},
    friendFlow: Flow<FriendUIState>,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val friend by friendFlow.collectAsState(initial = null)
    val refreshLoading = viewModel.refreshLoading
    val removeShareLoading = viewModel.removeShareLoading

    val coroutineScope = rememberCoroutineScope()

    var showWarningDialog by remember { mutableStateOf(false) }

    MapUI(
        isLoading = removeShareLoading,
        error = viewModel.refreshLoadingError ?: viewModel.removeShareLoadingError,
        onErrorConfirm = { viewModel.clearErrors() },
        appbar = {
            CustomAppBar(onBackNavigation)
        }
    ) {
        ContentCard(
            modifier = Modifier.heightIn(40.dp, 200.dp)
        ) {
            Column {
                if (friend == null) {
                    Text("Lädt...")
                } else {
                    Column {
                        FriendCard(friend = friend!!, onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.refreshFriend(friend!!.id)
                            }
                        }, refreshLoading)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 12.dp)
                        ) {
                            if (friend!!.userShareId != null) {
                                ButtonRectangle("Nicht mehr teilen", onClick = {
                                    showWarningDialog = true
                                }, modifier = Modifier.fillMaxWidth())
                            } else {
                                ButtonRectangle("Standort teilen", onClick = {
                                    onAddShareNavigation()
                                }, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }

    if (showWarningDialog) {
        WarningDialog(
            title = "Möchtest du die Freigabe löschen?",
            description = "Möchtest du aufhören, deinen Standort mit ${friend?.name} zu teilen? Du kannst die Freigabe jederzeit wieder erneuern.",
            confirmText = "Bestätigen",
            onConfirm = {
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.stopSharing(friend!!)
                }.invokeOnCompletion {
                    showWarningDialog = false
                }
            },
            onCancel = { showWarningDialog = false }
        )
    }
}

@Composable
fun FriendCard(
    friend: FriendUIState,
    onClick: () -> Unit = {},
    refreshLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(friend.name, color = MaterialTheme.colorScheme.onSurface)
                LocationVisibleBox(friend.userShareId != null)
            }
            if (friend.theirShareId == null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.icon_location_off),
                        contentDescription = "Location off",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Standort nicht mit dir geteilt",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        if (friend.distance != null) String.format(
                            Locale.getDefault(),
                            "%.2f",
                            friend.distance / 1000
                        ) + " km entfernt" else "lädt...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Text(
                        "•", color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Text(
                        if (friend.location?.battery != null) String.format(
                            Locale.getDefault(),
                            "%.2f",
                            friend.location.battery
                        ) + " %" else "lädt...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    friend.location?.let {
                        Text(
                            "•", color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Text(
                            getTimeDifference(friend.location.timestamp, LocalContext.current),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(2.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick() }
        ) {
            val rotation by animateFloatAsState(
                targetValue = if (refreshLoading) -360f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Icon(
                painterResource(R.drawable.icon_refresh),
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .graphicsLayer { this.rotationZ = if (refreshLoading) rotation else 0f }
            )
        }
    }
}