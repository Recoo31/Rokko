package kurd.reco.recoz.view.videoscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kurd.reco.api.model.DrmDataModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.api.model.SubtitleDataModel
import java.util.Locale


@OptIn(UnstableApi::class)
fun createHttpDataSourceFactory(headers: Map<String, String>): HttpDataSource.Factory {
    return DefaultHttpDataSource.Factory().apply {
        setDefaultRequestProperties(headers)
    }
}

fun createMediaItem(item: PlayDataModel, url: String): MediaItem {
    val mediaItemBuilder = MediaItem.Builder().apply {
        setUri(Uri.parse(url))
        when {
            url.contains(".mpd", true) -> setMimeType(MimeTypes.APPLICATION_MPD)
            url.contains(".m3u8", true) -> setMimeType(MimeTypes.APPLICATION_M3U8)
        }

        if (item.drm != null) {
            setDrmConfiguration(createDrmConfiguration(item.drm!!))
        }
        if (item.subtitles != null) {
            val subtitleConfigurations = item.subtitles!!.map { setSubtitle(it) }
            setSubtitleConfigurations(subtitleConfigurations)
        }
    }
    return mediaItemBuilder.build()
}

fun createDrmConfiguration(drm: DrmDataModel): MediaItem.DrmConfiguration {
    return MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID).apply {
        setLicenseUri(drm.licenseUrl)
        if (drm.headers != null) {
            setLicenseRequestHeaders(drm.headers!!)
        }
    }.build()
}

fun setSubtitle(item: SubtitleDataModel): MediaItem.SubtitleConfiguration {
    val assetSrtUri = Uri.parse((item.url))
    val subtitle = MediaItem.SubtitleConfiguration.Builder(assetSrtUri)
        .setMimeType(MimeTypes.TEXT_VTT)
        .setLanguage(item.language)
        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
        .build()
    return subtitle
}

@UnstableApi
fun TrackGroup.getName(trackType: @C.TrackType Int, index: Int): String {
    val format = this.getFormat(0)
    val language = format.language
    val label = format.label
    return buildString {
        if (label != null) {
            append(label)
        }
        if (isEmpty()) {
            if (trackType == C.TRACK_TYPE_TEXT) {
                append("Subtitle Track #${index + 1}")
            } else {
                append("Audio Track #${index + 1}")
            }
        }
        if (language != null && language != "und") {
            append(" - ")
            append(Locale(language).displayLanguage)
        }
    }
}

fun formatTime(ms: Long, locale: Locale = Locale.getDefault()): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(locale, "%02d:%02d", minutes, seconds)
}

fun hideSystemBars(window: Window) {
    WindowInsetsControllerCompat(window, window.decorView).let {
        it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        it.hide(WindowInsetsCompat.Type.systemBars())
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

@OptIn(UnstableApi::class)
fun applySelectedTrack(trackSelector: DefaultTrackSelector, trackGroup: TrackGroup, trackIndex: Int, trackType: Int) {
    val parametersBuilder = trackSelector.parameters.buildUpon()
    val trackSelectionOverride = TrackSelectionOverride(trackGroup, trackIndex)

    parametersBuilder.clearOverridesOfType(trackType)
    parametersBuilder.addOverride(trackSelectionOverride)

    trackSelector.setParameters(parametersBuilder)
}

fun adjustBrightness(context: Context, change: Float) {
    val layoutParams = (context as Activity).window.attributes
    layoutParams.screenBrightness = (layoutParams.screenBrightness + change).coerceIn(0f, 1f)
    context.window.attributes = layoutParams
}

fun adjustVolume(context: Context, change: Float) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    val newVolume = (currentVolume + change * maxVolume).toInt().coerceIn(0, maxVolume)
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
}

fun openVideoWithSelectedPlayer(context: Context, videoUri: String, playerPackageName: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(videoUri), "video/*")
        setPackage(playerPackageName)
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Selected player is not installed", Toast.LENGTH_SHORT).show()
    }
}
