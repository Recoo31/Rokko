package kurd.reco.recoz.view.videoscreen.composables

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kurd.reco.recoz.view.videoscreen.applySelectedTrack

@OptIn(UnstableApi::class)
@Composable
fun QualitySelectorDialog(tracks: Tracks, defaultTrackSelector: DefaultTrackSelector, onDismiss: () -> Unit) {
    val videoTracks = tracks.groups
        .filter { it.type == C.TRACK_TYPE_VIDEO && it.isSupported}

    LazyColumn {
        items(videoTracks) { trackGroup ->
            trackGroup.mediaTrackGroup.let { mediaTrackGroup ->
                for (index in (0 until mediaTrackGroup.length).reversed()) {
                    val format = mediaTrackGroup.getFormat(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = {
                                applySelectedTrack(defaultTrackSelector, mediaTrackGroup, index, C.TRACK_TYPE_VIDEO)
                                onDismiss()
                            }
                        )
                        Text(text = "${format.width}x${format.height}", modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}
