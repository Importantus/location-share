package digital.fischers.locationshare.ui.onboarding.server

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import digital.fischers.locationshare.ui.components.inputs.TextFieldDefault
import digital.fischers.locationshare.ui.components.screens.BaseScreen

@Composable
fun SetServerScreen(
    onBackNavigation: () -> Unit,
    onNextNavigation: () -> Unit,
    viewModel: SetServerViewModel = hiltViewModel()
) {
    BaseScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wähle deinen Server.",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
            )
            Text(
                text = "Du musst einen Server auswählen. Den kannst du selber hosten, oder du benutzt den Server eines Freundes.",
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
            ) {
                TextFieldDefault(
                    value = viewModel.serverUrl,
                    label = "Server URL",
                    placeholder = "https://",
                    onValueChange = viewModel::onServerUrlChanged
                )
            }
        }
    }
}