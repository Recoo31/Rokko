package kurd.reco.recoz.view.videoscreen

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.hideSystemBars

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerCompose(item: PlayDataModel) {
    val context = LocalContext.current
    val windows = (context as Activity).window

    val trackSelector = DefaultTrackSelector(context).apply {
        setParameters(
            buildUponParameters().setMinVideoSize(1920, 1080)
                .setForceHighestSupportedBitrate(true)
        )
    }

    hideSystemBars(windows)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).setTrackSelector(trackSelector).build().apply {
            val mediaItem = MediaItem.fromUri(item.url).buildUpon().run {
                if (item.drm != null) {
                    setDrmConfiguration(
                        MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                            .setLicenseUri(item.drm!!.licenseUrl)
                            .run { if (item.drm!!.headers != null) setLicenseRequestHeaders(item.drm!!.headers!!) else this }
                            .build()
                    )
                }
                build()
            }
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    var oldOrientation by rememberSaveable { mutableIntStateOf(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) }

    DisposableEffect(
        context.apply {
            oldOrientation = requestedOrientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    ) {
        onDispose {
            exoPlayer.release()
            context.requestedOrientation = oldOrientation
        }
    }

    VideoControlBar(exoPlayer = exoPlayer, item, trackSelector)
}

