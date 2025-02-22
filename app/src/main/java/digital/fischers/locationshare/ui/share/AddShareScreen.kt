package digital.fischers.locationshare.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.R
import digital.fischers.locationshare.ui.components.buttons.ButtonLayout
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.cards.ContentCard
import digital.fischers.locationshare.ui.components.navigation.CustomAppBar
import digital.fischers.locationshare.ui.components.screens.MapUI
import kotlinx.coroutines.launch

@Composable
fun AddShareScreen(
    onBackNavigation: () -> Unit,
    viewModel: AddShareViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.getAllUsersFromServer()
    }

    val coroutineScope = rememberCoroutineScope()

    MapUI(
        error = viewModel.createShareError ?: viewModel.usersLoadError,
        onErrorConfirm = onBackNavigation,
        isLoading = viewModel.createShareLoading,
        appbar = {
            CustomAppBar(onBackNavigation)
        }
    ) {
        ContentCard {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        stringResource(R.string.share_location),
                        style = TextStyle(
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ButtonSelectHorizontal(
                        viewModel.share.valid_until != null,
                        onClick = { viewModel.setToExpire() }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(
                                    R.string.for_hours,
                                    viewModel.hoursToShare,
                                    if (viewModel.hoursToShare > 1) "n" else ""
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ButtonSmallRectangle(enabled = viewModel.hoursToShare > 1, icon = {
                                    Icon(
                                        painterResource(R.drawable.icon_minus),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(12.dp),
                                        contentDescription = stringResource(R.string.minus)
                                    )
                                }, onClick = { viewModel.decreaseHoursToShare() })
                                ButtonSmallRectangle(icon = {
                                    Icon(
                                        painterResource(R.drawable.icon_plus),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(12.dp),
                                        contentDescription = stringResource(R.string.plus)
                                    )
                                }, onClick = { viewModel.increaseHoursToShare() })
                            }
                        }
                    }
                    ButtonSelectHorizontal(
                        viewModel.share.valid_until == null,
                        onClick = { viewModel.setNeverExpire() }) {
                        Text(
                            stringResource(R.string.until_deactivation),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            ),
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        stringResource(R.string.people_on_your_server),
                        style = TextStyle(
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (viewModel.usersLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(30.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                strokeWidth = 3.dp
                            )
                        }
                    } else if (viewModel.usersLoadError == null) {
                        if (viewModel.users.isEmpty()) {
                            Text(
                                stringResource(R.string.already_shared),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(viewModel.users.size) {
                                UserSelect(
                                    name = viewModel.users[it].name,
                                    username = viewModel.users[it].username,
                                    selected = viewModel.share.shared_with.contains(viewModel.users[it].id),
                                    onClick = {
                                        if (viewModel.share.shared_with.contains(viewModel.users[it].id)) {
                                            viewModel.onDeselectUser(viewModel.users[it].id)
                                        } else {
                                            viewModel.onSelectUser(viewModel.users[it].id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ButtonRectangle(
                            text = stringResource(R.string.cancel),
                            onClick = { onBackNavigation() },
                            modifier = Modifier.weight(1f),
                            layout = ButtonLayout.SMALL
                        )
                        ButtonRectangle(
                            text = stringResource(R.string.create),
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.createShare()
                                }.invokeOnCompletion {
                                    if (viewModel.createShareError == null) {
                                        onBackNavigation()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            layout = ButtonLayout.SMALL,
                            enabled = viewModel.share.shared_with.isNotEmpty()
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ButtonSelectHorizontal(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(10.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RadioKnob(selected)
        content()
    }
}

@Composable
fun UserSelect(
    name: String,
    username: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(70.dp)
            .height(75.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.padding(bottom = 5.dp)
        ) {
            RadioKnob(selected, size = 16)
        }
        Text(
            name,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            username,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ButtonSmallRectangle(
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(enabled) { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
fun RadioKnob(selected: Boolean, size: Int = 20) {
    Box(
        modifier = Modifier
            .height(size.dp)
            .width(size.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface, CircleShape)
            )
        }
    }
}