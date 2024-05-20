package kurd.reco.recoz.view.videoscreen

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

@OptIn(UnstableApi::class)
@Composable
fun AudioSelectorDialog(trackSelector: DefaultTrackSelector, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val mappedTrackInfo = trackSelector.currentMappedTrackInfo
    val formats = mappedTrackInfo?.getTrackGroups(C.TRACK_TYPE_AUDIO)

    val options = formats?.let { groups ->
        (0 until groups.length).flatMap { groupIndex ->
            (0 until groups[groupIndex].length).map { trackIndex ->
                groups[groupIndex].getFormat(trackIndex)
            }
        }.reversed()
    } ?: emptyList()

    LazyColumn {
        items(options) { format ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = false,
                    onClick = {
                        applySelectedFormat(trackSelector, format, context)
                        onDismiss()
                    }
                )
                Text(text = "${format.language}", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@OptIn(UnstableApi::class)
private fun applySelectedFormat(
    trackSelector: DefaultTrackSelector,
    selectedFormat: Format,
    context: Context
) {
    val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return
    val parametersBuilder = TrackSelectionParameters.Builder(context)
    val rendererIndex = C.TRACK_TYPE_AUDIO

    for (groupIndex in 0 until mappedTrackInfo.getTrackGroups(rendererIndex).length) {
        val group = mappedTrackInfo.getTrackGroups(rendererIndex)[groupIndex]
        for (trackIndex in 0 until group.length) {
            if (group.getFormat(trackIndex) == selectedFormat) {
                val trackSelectionOverride = TrackSelectionOverride(group, listOf(trackIndex))
                parametersBuilder.addOverride(trackSelectionOverride)
                break
            }
        }
    }

    trackSelector.setParameters(parametersBuilder.build())
}
