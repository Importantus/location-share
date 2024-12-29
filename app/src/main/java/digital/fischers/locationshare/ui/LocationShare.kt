package digital.fischers.locationshare.ui

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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import digital.fischers.locationshare.ui.onboarding.OnboardingScreen

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
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onNavigateNext = {
                    appState.navigateHome()
                })
            }
        }
    }
}