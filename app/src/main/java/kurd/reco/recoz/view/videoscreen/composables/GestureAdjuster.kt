package kurd.reco.recoz.view.videoscreen.composables

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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