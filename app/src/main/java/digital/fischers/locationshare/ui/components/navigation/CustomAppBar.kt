package digital.fischers.locationshare.ui.components.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import digital.fischers.locationshare.R
import digital.fischers.locationshare.ui.components.buttons.ButtonRound
import digital.fischers.locationshare.utils.consumePointerInput

@Composable
fun CustomAppBar(
    backNavigation: () -> Unit,
    menu: @Composable (() -> Unit)? = null
) {
    BackHandler { backNavigation() }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.consumePointerInput()
        ) {
            ButtonRound({
                Icon(
                    painterResource(id = R.drawable.icon_arrow),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            }, onClick = backNavigation)
        }
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
                .consumePointerInput()
        ) {
            menu?.invoke()
        }
    }
}
