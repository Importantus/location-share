package digital.fischers.locationshare.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.R
import digital.fischers.locationshare.domain.types.FriendUIState
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.cards.ContentCard
import digital.fischers.locationshare.ui.components.screens.MapUI
import digital.fischers.locationshare.utils.getTimeDifference
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

@Composable
fun HomeScreen(
    friends: StateFlow<List<FriendUIState>>,
    onFriendNavigation: (id: String) -> Unit,
    onFriendAddNavigation: () -> Unit,
    onSearchNavigation: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.syncFriends()
        viewModel.syncFriendLocations()
    }

    MapUI() {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonRectangle(
                    "Freund hinzufügen",
                    onFriendAddNavigation,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            ContentCard(
                modifier = Modifier.heightIn(40.dp, 200.dp)
            ) {
                FriendsList(friends, onFriendNavigation)
            }
        }
    }
}

@Composable
fun FriendsList(
    friends: StateFlow<List<FriendUIState>>,
    onFriendNavigation: (id: String) -> Unit = {}
) {
    val friendsState by friends.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(friendsState.size) { index ->
            FriendCard(
                friend = friendsState[index],
                onClick = { onFriendNavigation(friendsState[index].id) })
            if (index < friendsState.size - 1) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun FriendCard(
    friend: FriendUIState,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(16.dp, 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
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
                Text(
                    if (friend.distance != null) String.format(
                        Locale.getDefault(),
                        "%.2f",
                        friend.distance / 1000
                    ) + " km" else "lädt...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            friend.location?.let {
                Text(
                    getTimeDifference(friend.location.timestamp, LocalContext.current),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            Icon(
                painterResource(R.drawable.icon_chevron),
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LocationVisibleBox(
    visible: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .height(20.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(end = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(if (visible) R.drawable.eye_open else R.drawable.eye_closed),
                contentDescription = "Location on",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            if (visible) "Du teilst deinen Standort" else "Sieht deinen Standort nicht",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}