package kurd.reco.recoz.view.videoscreen.composables

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import kurd.reco.recoz.view.videoscreen.adjustBrightness
import kurd.reco.recoz.view.videoscreen.adjustVolume

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GestureAdjuster(modifier: Modifier = Modifier, context: Context, onPlayFast: () -> Unit) {
    var initialY by remember { mutableFloatStateOf(0f) }

    Row(modifier = modifier.fillMaxSize()) {
        // Left side for brightness adjustment
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            initialY = it.y
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val brightnessChange = dragAmount / 1000f
                            adjustBrightness(context, -brightnessChange)
                        }
                    )
                }
        )

        // Right side for volume adjustment
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            initialY = it.y
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val volumeChange = dragAmount / 500f
                            adjustVolume(context, -volumeChange)
                        }
                    )
                }
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_BUTTON_PRESS -> {
                            onPlayFast()
                            true
                        }
                        else -> false
                    }
                }
        )
    }
}