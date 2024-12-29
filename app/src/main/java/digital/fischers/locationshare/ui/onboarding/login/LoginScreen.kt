package digital.fischers.locationshare.ui.onboarding.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.inputs.PasswordField
import digital.fischers.locationshare.ui.components.inputs.TextFieldDefault
import digital.fischers.locationshare.ui.components.screens.BaseScreen
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val username = viewModel.username
    val password = viewModel.password
    val loading = viewModel.loading
    val error = viewModel.error

    val coroutineScope = rememberCoroutineScope()

    BackHandler { onNavigateBack() }

    BaseScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Log In.",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
            )
            Text(
                text = "Whoop! Willkommen auf dem weltbesten Server. Logge dich ein, um deinen Standort zu teilen.",
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            if (error != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.error)
                        .widthIn(min = 48.dp, max = 330.dp)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Die Login-Daten sind nicht korrekt. Bitte versuche es erneut.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Box(
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(bottom = 16.dp, top = 16.dp)
            ) {
                TextFieldDefault(
                    value = username,
                    label = "Nutzername",
                    placeholder = "dieter67",
                    onValueChange = viewModel::onUsernameChanged,
                    loading = loading
                )
            }

            Box(
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(bottom = 16.dp)
            ) {
                PasswordField(
                    value = password,
                    label = "Passwort",
                    onValueChange = viewModel::onPasswordChanged,
                )
            }

            Box(modifier = Modifier.widthIn(min = 48.dp, max = 330.dp)) {
                ButtonRectangle(
                    "Log In",
                    onClick = {
                        coroutineScope.launch {
                            val result = viewModel.login(username, password)
                            if (result is APIResult.Error) {
                                // cancel the job as failed
                                coroutineContext.job.cancel()
                            }
                        }.invokeOnCompletion {
                            if (error == null && it == null) {
                                onNavigateNext()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier.widthIn(min = 48.dp, max = 330.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    "ODER",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 5.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Box(modifier = Modifier.widthIn(min = 48.dp, max = 330.dp)) {
                ButtonRectangle(
                    "Registrieren",
                    onClick = onNavigateRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}