package kurd.reco.recoz

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kurd.reco.recoz.data.model.DrmDataModel
import kurd.reco.recoz.data.model.PlayDataModel
import kurd.reco.recoz.ui.theme.RecozTheme

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecozTheme {
                Surface {
                    val url = intent.getStringExtra("url")
                    val title = intent.getStringExtra("title")
                    val licenseUrl = intent.getStringExtra("licenseUrl")
                    val headers = intent.getSerializableExtra("headers") as? Map<String, String>


                    val playData = PlayDataModel(
                        url = url ?: "",
                        title = title ?: "",
                        drm = if (licenseUrl != null) {
                            DrmDataModel(licenseUrl, headers)
                        } else {
                            null
                        }
                    )

                    VideoPlayer(playData)
                }
            }
        }
    }


    @OptIn(UnstableApi::class)
    @Composable
    fun VideoPlayer(item: PlayDataModel) {
        val context = LocalContext.current
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(
                buildUponParameters().setMinVideoSize(1920, 1080)
                    .setForceHighestSupportedBitrate(true)
            )
        }

        val exoPlayer = remember {
            ExoPlayer.Builder(context).setTrackSelector(trackSelector).build().apply {
                val mediaItem = MediaItem.fromUri(item.url).buildUpon().run {
                    if (item.drm != null) {
                        setDrmConfiguration(
                            MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                                .setLicenseUri(item.drm.licenseUrl)
                                .run { if (item.drm.headers != null) setLicenseRequestHeaders(item.drm.headers) else this }
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

        hideSystemBars(LocalView.current)

//        LaunchedEffect(Unit) {
//            (context as Activity).apply {
//                oldOrientation = requestedOrientation
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            }
//        }

        DisposableEffect(
            (context as Activity).apply {
                oldOrientation = requestedOrientation
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        ) {
            onDispose {
                println("onDispose")
                exoPlayer.release()
                (context).requestedOrientation = oldOrientation
            }
        }

        AndroidView(factory = {
            PlayerView(it).apply {
                hideController()
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        })
    }

    private fun hideSystemBars(view: View) {
        WindowInsetsControllerCompat(window, view).let {
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
