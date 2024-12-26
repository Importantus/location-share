package digital.fischers.locationshare.ui.onboarding.welcome

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import digital.fischers.locationshare.ui.components.buttons.ButtonRectangle
import digital.fischers.locationshare.ui.components.screens.BaseScreen
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateNext: () -> Unit,
) {
    var text1Visible by remember { mutableStateOf(false) }
    var text2Visible by remember { mutableStateOf(false) }
    var backgroundVisible by remember { mutableStateOf(true) }
    var buttonVisible by remember { mutableStateOf(false) }

    val alphaText1 by animateFloatAsState(
        targetValue = if (text1Visible) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = EaseOut),
        label = "" // Dauer des Einblendens
    )

    val alphaText2 by animateFloatAsState(
        targetValue = if (text2Visible) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOut),
        label = "" // Dauer des Einblendens
    )

    val alphaBackground by animateFloatAsState(
        targetValue = if (backgroundVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOut),
        label = "" // Dauer des Einblendens
    )

    val alphaButton by animateFloatAsState(
        targetValue = if (buttonVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = EaseInOut),
        label = "" // Dauer des Einblendens
    )

    LaunchedEffect(Unit) {
        text1Visible = true // Text 1 sofort einblenden
        delay(1000.toLong()) // Verzögerung für Text 2
        text2Visible = true // Text 2 einblenden
        delay(2000.toLong()) // Verzögerung für den nächsten Bildschirm
        backgroundVisible = false
        buttonVisible = true
    }

    if (alphaBackground != 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alphaBackground))
        )
    }
    BaseScreen(isLoading = false) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Willkommen!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.alpha(alphaText1)
            )
            Text(
                text = "Schön, dass du da bist! Hier kannst du deinen Standort mit deinen Freunden teilen (nicht Sundar Pichai).",
                modifier = Modifier
                    .widthIn(min = 48.dp, max = 330.dp)
                    .padding(top = 8.dp)
                    .alpha(alphaText2),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Box(
                Modifier
                    .alpha(alphaButton)
                    .padding(top = 16.dp)
            ) {
                ButtonRectangle(
                    "Los geht's",
                    onClick = onNavigateNext
                )
            }
        }
    }
}

