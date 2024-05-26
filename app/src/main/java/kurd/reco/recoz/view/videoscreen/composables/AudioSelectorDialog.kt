package kurd.reco.recoz.view.videoscreen.composables

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import kurd.reco.recoz.view.videoscreen.getName

@OptIn(UnstableApi::class)
@Composable
fun AudioSelectorDialog(tracks: Tracks, defaultTracks: DefaultTrackSelector, onDismiss: () -> Unit) {
    val trackName = C.TRACK_TYPE_AUDIO
    val audioTracks = tracks.groups
        .filter { it.type == trackName && it.isSupported }

    LazyColumn {
        itemsIndexed(audioTracks) { index, format ->
            val selected = format.isSelected
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selected,
                    onClick = {
                        applySelectedTrack(defaultTracks, format.mediaTrackGroup, 0, C.TRACK_TYPE_AUDIO)
                        onDismiss()
                    }
                )
                Text(text = format.mediaTrackGroup.getName(trackName, index), modifier = Modifier.padding(4.dp))
            }
        }
    }
}
