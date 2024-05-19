package kurd.reco.recoz.view.videoscreen

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.showOtherVideoApps

@OptIn(UnstableApi::class)
@Composable
fun VideoControlBar(exoPlayer: ExoPlayer, item: PlayDataModel) {
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var currentTime by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(exoPlayer.duration) }
    var showControls by remember { mutableStateOf(false) }
    var videoSize by remember { mutableStateOf(exoPlayer.videoSize) }
    val context = LocalContext.current

    LaunchedEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(_isPlaying: Boolean) {
                isPlaying = _isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = exoPlayer.duration
                }
            }

            override fun onVideoSizeChanged(_videoSize: VideoSize) {
                videoSize = _videoSize
            }

            override fun onPlayerError(error: PlaybackException) {
                error.printStackTrace()
                exoPlayer.release()
                showOtherVideoApps(Uri.parse(item.url), context)

            }
        }
        exoPlayer.addListener(listener)
        while (true) {
            currentTime = exoPlayer.currentPosition
            delay(1000L)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        })

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showControls = !showControls },
        ) {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            if (!item.title.isNullOrEmpty()) {
                                Text(
                                    text = item.title!!,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                            Text(
                                text = "${videoSize.height}p",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }


                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                        VideoPlayerBottom(exoPlayer, currentTime, duration, isPlaying)
                    }
                }
            }
        }
    }
}
