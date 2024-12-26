package digital.fischers.locationshare.ui

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import digital.fischers.locationshare.ui.onboarding.OnboardingWrapper
import digital.fischers.locationshare.ui.onboarding.permissions.PermissionsScreen
import digital.fischers.locationshare.ui.onboarding.server.SetServerScreen
import digital.fischers.locationshare.ui.onboarding.welcome.WelcomeScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationShareApp(
    intent: Intent?,
    appState: LocationShareAppState = rememberLocationShareAppState(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = appState.navController, startDestination = hasSeenOnboarding.let {
            if (it) Screen.Home.route else Screen.Onboarding.route
        }) {
            composable(Screen.Home.route) {
                Map()
            }
            navigation(Screen.Welcome.route, Screen.Onboarding.route) {
                val numberOfSteps = 4

                composable(Screen.Welcome.route) {
                    WelcomeScreen(onNavigateNext = {
                        appState.navigateToPermissions()
                    })
                }
                composable(Screen.Permissions.route) {
                    val rememberBackgroundPermission = rememberPermissionState(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )

                    OnboardingWrapper(
                        step = 1,
                        steps = numberOfSteps,
                        onNavigateNext = if (rememberBackgroundPermission.status.isGranted) {
                            { appState.navigateToSetServer() }
                        } else null
                    ) {
                        PermissionsScreen(
                            onBackNavigation = { appState.navigateToWelcome() },
                            onNextNavigation = { appState.navigateToSetServer() }
                        )
                    }
                }
                composable(Screen.SetServer.route) {
                    OnboardingWrapper(
                        step = 2,
                        steps = numberOfSteps,
                        onNavigateNext = {
                        }
                    ) {
                        SetServerScreen(
                            onBackNavigation = { appState.navigateToPermissions() },
                            onNextNavigation = {}
                        )
                    }
                }
            }
        }
    }
}