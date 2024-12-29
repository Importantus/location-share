package digital.fischers.locationshare.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.ui.onboarding.login.LoginScreen
import digital.fischers.locationshare.ui.onboarding.permissions.PermissionsScreen
import digital.fischers.locationshare.ui.onboarding.registration.RegistrationScreen
import digital.fischers.locationshare.ui.onboarding.server.SetServerScreen
import digital.fischers.locationshare.ui.onboarding.welcome.WelcomeScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateNext: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingStep by viewModel.onboardingStep.collectAsState()
    val onBoardingSteps: Int by remember {
        derivedStateOf { onboardingStep.coerceAtLeast(3) + 1 }
    }

    val coroutineScope = rememberCoroutineScope()

    OnboardingWrapper(
        step = onboardingStep,
        steps = onBoardingSteps,
        onNavigateNext = null,
        onNavigateBack = when (onboardingStep) {
            3, 4 -> {
                {
                    coroutineScope.launch {
                        viewModel.updateOnboardingStep(onboardingStep - 1)
                    }
                }
            }

            else -> null
        },
    ) {
        AnimatedContent(
            targetState = onboardingStep,
            transitionSpec = {
                // Define your desired enter/exit transitions here
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut())
                }
            }, label = ""
        ) { targetStep ->
            when (targetStep) {
                0 -> WelcomeScreen(
                    onNavigateNext = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(1)
                        }
                    }
                )

                1 -> PermissionsScreen(
                    onBackNavigation = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(0)
                        }
                    },
                    onNextNavigation = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(2)
                        }
                    }
                )

                2 -> SetServerScreen(
                    onBackNavigation = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(1)
                        }
                    },
                    onNextNavigation = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(3)
                        }
                    }
                )

                3 -> LoginScreen(
                    onNavigateNext = {
                        coroutineScope.launch {
                            // We update the step to 5 so that we know that the user has seen and completed the onboarding
                            viewModel.updateOnboardingStep(5)
                        }.invokeOnCompletion {
                            onNavigateNext()
                        }
                    },
                    onNavigateBack = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(2)
                        }
                    },
                    onNavigateRegister = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(4)
                        }
                    }
                )

                4 -> RegistrationScreen(
                    onNavigateNext = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(5)
                        }.invokeOnCompletion {
                            onNavigateNext()
                        }
                    },
                    onNavigateBack = {
                        coroutineScope.launch {
                            viewModel.updateOnboardingStep(3)
                        }
                    }
                )
            }
        }
    }
}