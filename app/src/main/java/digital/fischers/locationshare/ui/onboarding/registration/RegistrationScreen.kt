package digital.fischers.locationshare.ui.onboarding.registration

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.R
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.inputs.PasswordField
import digital.fischers.locationshare.ui.components.inputs.TextFieldDefault
import digital.fischers.locationshare.ui.components.screens.BaseScreen
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val error = viewModel.error
    val loading = viewModel.loading

    val serverInfo = viewModel.serverInfo

    val username = viewModel.username
    val password = viewModel.password
    val name = viewModel.name
    val email = viewModel.email
    val registrationSecret = viewModel.registrationSecret

    val coroutineScope = rememberCoroutineScope()

    BackHandler { onNavigateBack() }

    BaseScreen(
        isLoading = loading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(min = 48.dp, max = 330.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.register_here),
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if (error != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .widthIn(min = 48.dp, max = 330.dp)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.registration_failed),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier.widthIn(min = 48.dp, max = 330.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (serverInfo?.public_registration == false) {
                    TextFieldDefault(
                        value = registrationSecret,
                        label = stringResource(R.string.registration_code),
                        placeholder = "123456",
                        onValueChange = viewModel::onRegistrationSecretChanged
                    )
                }
                TextFieldDefault(
                    value = name,
                    label = stringResource(R.string.name),
                    placeholder = "Max",
                    onValueChange = viewModel::onNameChanged
                )
                TextFieldDefault(
                    value = username,
                    label = stringResource(R.string.username),
                    placeholder = "dieter67",
                    onValueChange = viewModel::onUsernameChanged
                )
                TextFieldDefault(
                    value = email,
                    label = stringResource(R.string.email),
                    placeholder = "dieter67@example.com",
                    onValueChange = viewModel::onEmailChanged
                )
                PasswordField(
                    value = password,
                    label = stringResource(R.string.password),
                    onValueChange = viewModel::onPasswordChanged,
                )
                ButtonRectangle(
                    stringResource(R.string.register),
                    onClick = {
                        coroutineScope.launch {
                            val result = viewModel.register()
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
                        .padding(top = 8.dp)
                )
            }
        }
    }
}