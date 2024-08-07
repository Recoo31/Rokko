package kurd.reco.recoz.view.videoscreen.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kurd.reco.recoz.R

@Composable
fun VideoSeekControls(exoPlayer: ExoPlayer) {
    var isRewinding by remember { mutableStateOf(false) }
    var isForwarding by remember { mutableStateOf(false) }

    val pauseIcon = if (exoPlayer.isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24

    val rewindScale by animateFloatAsState(
        targetValue = if (isRewinding) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300), label = "rewind"
    )

    val forwardScale by animateFloatAsState(
        targetValue = if (isForwarding) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300), label = "forward"
    )

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
                isRewinding = true
            },
            modifier = Modifier.scale(rewindScale)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.netflix_skip_rewind),
                contentDescription = "Rewind 10 Seconds",
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 50.dp))

        IconButton(
            onClick = { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() },
            modifier = Modifier.size(50.dp)
        ) {
            Icon(painter = painterResource(id = pauseIcon), contentDescription = "Pause/Play")
        }

        Spacer(modifier = Modifier.padding(horizontal = 50.dp))

        IconButton(
            onClick = {
                exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
                isForwarding = true
            },
            modifier = Modifier.scale(forwardScale)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.netflix_skip_forward),
                contentDescription = "Forward 10 Seconds",
            )
        }
    }

    LaunchedEffect(isForwarding, isRewinding) {
        delay(300)
        isForwarding = false
    }
}