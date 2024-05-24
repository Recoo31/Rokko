package kurd.reco.recoz.view.videoscreen

import android.net.Uri
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.TrackGroup
import androidx.media3.common.util.UnstableApi
import kurd.reco.api.model.DrmDataModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.api.model.SubtitleDataModel
import java.util.Locale

fun setSubtitle(item: SubtitleDataModel): MediaItem.SubtitleConfiguration {
    val assetSrtUri = Uri.parse((item.url))
    val subtitle = MediaItem.SubtitleConfiguration.Builder(assetSrtUri)
        .setMimeType(MimeTypes.TEXT_VTT)
        .setLanguage(item.language)
        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
        .build()
    return subtitle
}

fun createMediaItem(item: PlayDataModel): MediaItem {
    return MediaItem.fromUri(item.url).buildUpon().apply {
        if (item.drm != null) {
            setDrmConfiguration(
                createDrmConfiguration(item.drm!!)
            )
        }
        if (item.subtitles != null) {
            val subtitleConfigurations = item.subtitles!!.map { setSubtitle(it) }
            setSubtitleConfigurations(subtitleConfigurations)
        }
    }.build()
}



fun createDrmConfiguration(drm: DrmDataModel): MediaItem.DrmConfiguration {
    return MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
        .setLicenseUri(drm.licenseUrl)
        .apply {
            if (drm.headers != null) {
                setLicenseRequestHeaders(drm.headers!!)
            }
        }
        .build()
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

@Composable
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:$seconds"
}

fun hideSystemBars(window: Window) {
    WindowInsetsControllerCompat(window, window.decorView).let {
        it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        it.hide(WindowInsetsCompat.Type.systemBars())
    }
}