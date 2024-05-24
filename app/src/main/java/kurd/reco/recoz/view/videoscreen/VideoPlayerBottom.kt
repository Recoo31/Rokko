package kurd.reco.recoz.view.videoscreen

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kurd.reco.recoz.R

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerBottom(
    exoPlayer: ExoPlayer,
    currentTime: Long,
    duration: Long,
    isPlaying: Boolean,
    onResizeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        IconButton(onClick = { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() }) {
            Icon(
                painter = painterResource(id = if (isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24),
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "${formatTime(currentTime)} / ${formatTime(duration)}",
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        if (duration > 0) {
            Slider(
                value = currentTime.toFloat(),
                onValueChange = { exoPlayer.seekTo(it.toLong()) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                )
            )
        } else {
            Slider(
                value = 0f,
                onValueChange = {},
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                )
            )
        }

        IconButton(onClick = { onResizeClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_aspect_ratio_24),
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(onClick = { onSettingsClick() }) {
            Icon(
                painter = painterResource(id = R.drawable.rounded_video_settings_24),
                contentDescription = null,
                tint = Color.White
            )
        }
    }


}
