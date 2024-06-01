package kurd.reco.recoz.view.videoscreen

import android.content.Intent
import android.graphics.Typeface
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.CaptionStyleCompat
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.view.videoscreen.composables.GestureAdjuster
import kurd.reco.recoz.view.videoscreen.composables.SettingsDialog
import kurd.reco.recoz.view.videoscreen.composables.VideoPlayerBottom
import kurd.reco.recoz.view.videoscreen.composables.VideoSeekControls

@OptIn(UnstableApi::class)
@Composable
fun VideoControlBar(
    exoPlayer: ExoPlayer,
    item: PlayDataModel,
    trackSelector: DefaultTrackSelector
) {
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var currentTime by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(exoPlayer.duration) }
    var showControls by remember { mutableStateOf(false) }
    var videoSize by remember { mutableStateOf(exoPlayer.videoSize) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var _resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_ZOOM) }


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

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(item.urls.first().second), "video/*")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                startActivity(context, intent, null)

            }
        }
        exoPlayer.addListener(listener)
        while (true) {
            currentTime = exoPlayer.currentPosition
            delay(1000L)
        }
    }

    var bottomPadding by remember {
        mutableIntStateOf(180)
    }

    // TODO: Update subtitle position according to Resize mode
    // It's like this for now

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    useController = false
                    resizeMode = _resizeMode
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    subtitleView?.apply {
                        setPadding(0, 0, 0, bottomPadding)
                        this.setStyle(
                            CaptionStyleCompat(
                                Color.White.toArgb(),
                                0x000000FF,
                                0x00000000,
                                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                                Color.Black.toArgb(),
                                Typeface.DEFAULT,
                            )
                        )
                    }
                }
            },
            update = {
                it.resizeMode = _resizeMode
                it.subtitleView?.apply {
                    setPadding(0, 0, 0, bottomPadding)
                    this.setStyle(
                        CaptionStyleCompat(
                            Color.White.toArgb(),
                            0x000000FF,
                            0x00000000,
                            CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                            Color.Black.toArgb(),
                            Typeface.DEFAULT,
                        )
                    )
                }
            }
        )

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    GestureAdjuster(modifier = Modifier.fillMaxSize(), context = context) {
                        exoPlayer.setPlaybackSpeed(2f)
                    }

                    VideoSeekControls(exoPlayer)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (!item.title.isNullOrEmpty()) {
                                Text(
                                    text = item.title!!,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                            Text(
                                text = "${videoSize.width}x${videoSize.height}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }



//                    VideoSeekControls(exoPlayer)

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        VideoPlayerBottom(
                            exoPlayer,
                            currentTime,
                            duration,
                            isPlaying,
                            onResizeClick = {
                                println(_resizeMode)
                                _resizeMode = when (_resizeMode) {
                                    AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                                        bottomPadding = 140
                                        AspectRatioFrameLayout.RESIZE_MODE_FILL
                                    }
                                    AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                                        bottomPadding = 160
                                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                    }
                                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                                        bottomPadding = 50
                                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                                    }
                                    else -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                                }
                            },
                            onSettingsClick = { showSettingsDialog = !showSettingsDialog })
                    }
                }
                if (showSettingsDialog) {
                    SettingsDialog(
                        exoPlayer,
                        trackSelector,
                        onDismiss = { showSettingsDialog = false }
                    )
                }
            }
        }
    }
}

