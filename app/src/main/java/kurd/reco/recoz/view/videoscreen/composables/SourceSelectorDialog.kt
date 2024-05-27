package kurd.reco.recoz.view.videoscreen.composables

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
import androidx.media3.exoplayer.ExoPlayer
import kurd.reco.recoz.MainVM
import kurd.reco.recoz.view.videoscreen.createMediaItem
import org.koin.compose.koinInject

@Composable
fun SourceSelectorDialog(exoPlayer: ExoPlayer, onDismiss: () -> Unit) {
    val viewModel: MainVM = koinInject()

    val urls = viewModel.playDataModel?.urls
    if (urls.isNullOrEmpty()) return

    LazyColumn {
        items(urls) {
            val selected = exoPlayer.currentMediaItem?.localConfiguration?.uri.toString() == it.second
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selected,
                    onClick = {
                        val mediaItem = createMediaItem(viewModel.playDataModel!!, it.second)
                        exoPlayer.setMediaItem(mediaItem)
                        onDismiss()
                    }
                )
                Text(text = it.first, modifier = Modifier.padding(4.dp))
            }
        }
    }
}

