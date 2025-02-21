package digital.fischers.locationshare.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import digital.fischers.locationshare.ui.friend.FriendScreen
import digital.fischers.locationshare.ui.home.HomeScreen
import digital.fischers.locationshare.ui.map.MapWrapper
import digital.fischers.locationshare.ui.onboarding.OnboardingScreen
import digital.fischers.locationshare.ui.share.AddShareScreen
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationShareApp(
    intent: Intent?,
    appState: LocationShareAppState = rememberLocationShareAppState(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsState()

    val accessFineLocationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val accessCoarseLocationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val accessBackgroundLocationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    LaunchedEffect(
        accessFineLocationPermission,
        accessCoarseLocationPermission,
        accessBackgroundLocationPermission
    ) {
        viewModel.updateLocationPermissionState(accessFineLocationPermission)
        viewModel.updateLocationPermissionState(accessCoarseLocationPermission)
        viewModel.updateLocationPermissionState(accessBackgroundLocationPermission)
    }



    Box(modifier = Modifier.fillMaxSize()) {
        MapWrapper(
            showMap = hasSeenOnboarding,
            userLocation = viewModel.userLocation,
            friends = viewModel.friends,
            cameraPosition = viewModel.cameraPosition
        ) {
            NavHost(
                navController = appState.navController,
                startDestination = hasSeenOnboarding.let {
                    if (it) Screen.Home.route else Screen.Onboarding.route
                }) {
                composable(Screen.Home.route) {
                    LaunchedEffect(Unit) {
                        viewModel.setCameraToTrackUserLocation()
                    }
                    HomeScreen(
                        friends = viewModel.friends,
                        onFriendAddNavigation = {
                            appState.navigateToAddShare()
                        },
                        onSearchNavigation = {},
                        onFriendNavigation = { friendId ->
                            viewModel.setCameraToTrackFriend(friendId)
                            appState.navigateToFriend(friendId)
                        }
                    )
                }
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(onNavigateNext = {
                        appState.navigateHome()
                    })
                }
                composable(Screen.AddShare.route) {
                    AddShareScreen(
                        onBackNavigation = { appState.navigateBack() }
                    )
                }
                composable(Screen.Friend.route) {
                    val userId = it.arguments?.getString(Screen.ARG_USER_ID)
                    if (userId != null) {
                        FriendScreen(
                            onBackNavigation = {
                                viewModel.setCameraToTrackUserLocation()
                                appState.navigateHome()
                            },
                            onAddShareNavigation = {
                                appState.navigateToAddShare()
                            },
                            friendFlow = viewModel.friends.map { friends ->
                                friends.first { friend -> friend.id == userId }
                            }
                        )
                    }
                }
            }
        }
    }
}