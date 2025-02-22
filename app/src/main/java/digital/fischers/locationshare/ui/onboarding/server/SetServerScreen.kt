package digital.fischers.locationshare.ui.onboarding.server

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.R
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.inputs.TextFieldDefault
import digital.fischers.locationshare.ui.components.screens.BaseScreen
import kotlinx.coroutines.launch

@Composable
fun SetServerScreen(
    onBackNavigation: () -> Unit,
    onNextNavigation: () -> Unit,
    viewModel: SetServerViewModel = hiltViewModel()
) {
    val validServerUrl = viewModel.validServerUrl
    val loading = viewModel.loading

    val coroutineScope = rememberCoroutineScope()

    BaseScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.choose_server),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
            )
            Text(
                text = stringResource(R.string.choose_server_desc),
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(bottom = 16.dp)
            ) {
                TextFieldDefault(
                    value = viewModel.serverUrl,
                    label = stringResource(R.string.server_url),
                    placeholder = "https://",
                    onValueChange = viewModel::onServerUrlChanged,
                    loading = loading
                )
            }

            if (validServerUrl) {
                Box(modifier = Modifier.widthIn(min = 48.dp, max = 330.dp)) {
                    ButtonRectangle(
                        stringResource(R.string.next_step),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveServerUrl(viewModel.serverUrl)

                            }.invokeOnCompletion { onNextNavigation() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}