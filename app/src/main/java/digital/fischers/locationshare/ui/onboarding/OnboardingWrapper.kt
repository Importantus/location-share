package digital.fischers.locationshare.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import digital.fischers.locationshare.R
import digital.fischers.locationshare.ui.components.buttons.ButtonRound

@Composable
fun OnboardingWrapper(
    step: Int,
    steps: Int,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    screen: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    onNavigateBack?.let {
                        ButtonRound({
                            Icon(
                                painterResource(id = R.drawable.icon_arrow),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }, onClick = onNavigateBack)
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    onNavigateNext?.let {
                        ButtonRound({
                            Icon(
                                painterResource(id = R.drawable.icon_arrow),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.rotate(180F)
                            )
                        }, onClick = onNavigateNext)
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DotIndicator(steps, step)
            }
        }
        screen()
    }
}

@Composable
fun DotIndicator(steps: Int, step: Int) {
    val offsetX by animateDpAsState(
        targetValue = (step * 8).dp + ((step) * 10).dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(steps) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(5.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offsetX.roundToPx(),
                        0
                    )
                }
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.onSurface)
                .padding(5.dp)
        )
    }
}