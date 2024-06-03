package kurd.reco.recoz.view.detailscreen.composables

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.PlayerActivity

@Composable
fun MultiSourceDialog(
    playDataModel: PlayDataModel?,
    context: Context,
    updatePlayDataModel: (PlayDataModel) -> Unit
) {
    val urls = playDataModel?.urls ?: return

    AnimatedVisibility(visible = true) {
        ElevatedCard {
            LazyColumn {
                item {
                    Text(
                        text = "Select a source",
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                items(urls) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = false, onClick = {
                            val updatedList = mutableListOf<Pair<String, String>>().apply {
                                add(item)
                                playDataModel.urls.forEach { if (it != item) add(it) }
                            }
                            updatePlayDataModel(playDataModel.copy(urls = updatedList))
                            context.startActivity(Intent(context, PlayerActivity::class.java))
                        })
                        Text(text = item.first, modifier = Modifier.padding(4.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
