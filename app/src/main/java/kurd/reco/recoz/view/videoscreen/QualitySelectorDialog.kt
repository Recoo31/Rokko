package kurd.reco.recoz.view.videoscreen

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Format
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector

@OptIn(UnstableApi::class)
@Composable
fun QualitySelectorDialog(
    tracks: Tracks,
    defaultTrackSelector: DefaultTrackSelector,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val mappedTrackInfo = defaultTrackSelector.currentMappedTrackInfo
    val formats = mappedTrackInfo?.getTrackGroups(0)

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = false,
                    onClick = {
                        applySelectedFormat(defaultTrackSelector, format, context)
                        onDismiss()
                    }
                )
                Text(text = "${format.width}x${format.height}")
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
    val rendererIndex = 0

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
