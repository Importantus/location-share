package digital.fischers.locationshare.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val deepLinkPath: String? = null) {
    data object Home : Screen("home")
    data object Onboarding : Screen("onboarding")
    data object Welcome : Screen("welcome")
    data object Permissions : Screen("permissions")
    data object SetServer : Screen("setServer")
    data object AddShare : Screen("addShare")
}

@Composable
fun rememberLocationShareAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    LocationShareAppState(navController, context)
}

class LocationShareAppState(
    val navController: NavHostController,
    private val context: Context
) {
    private fun navigateBackstackAware(route: String, screen: Screen) {
        val previousRoute = navController.previousBackStackEntry?.destination?.route
        if (previousRoute != screen.route) {
            navController.navigate(route)
        } else {
            navController.popBackStack()
        }
    }

    fun navigateHome() {
        // Clear all backstack entries and navigate to home
        navController.popBackStack(navController.graph.startDestinationId, false)
    }

    fun navigateToOnboarding() {
        navigateBackstackAware(Screen.Onboarding.route, Screen.Onboarding)
    }

    fun navigateToWelcome() {
        navigateBackstackAware(Screen.Welcome.route, Screen.Welcome)
    }

    fun navigateToPermissions() {
        navigateBackstackAware(Screen.Permissions.route, Screen.Permissions)
    }

    fun navigateToSetServer() {
        navigateBackstackAware(Screen.SetServer.route, Screen.SetServer)
    }

    fun navigateToAddShare() {
        navigateBackstackAware(Screen.AddShare.route, Screen.AddShare)
    }
}