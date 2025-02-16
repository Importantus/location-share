package digital.fischers.locationshare.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.consumePointerInput(): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
//            while (true) {
//                val event = awaitPointerEvent()
//                event.changes.forEach { it.consume() }
//            }
        }
    }
)