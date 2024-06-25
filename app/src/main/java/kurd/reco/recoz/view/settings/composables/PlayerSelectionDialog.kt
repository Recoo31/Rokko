package kurd.reco.recoz.view.settings.composables

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.recoz.focusScale
import kurd.reco.recoz.view.settings.getVideoPlayers

@Composable
fun PlayerSelectionDialog(
    context: Context,
    selectedPlayer: String,
    onDismiss: () -> Unit,
    onSelectPlayer: (String) -> Unit
) {
    val players = getVideoPlayers(context)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Select External Player", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        },
        text = {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectPlayer("") }
                            .padding(16.dp)
                            .focusScale()
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "None",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedPlayer.isEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,

                        )
                    }

                }
                items(players) { player ->
                    val packageName = player.activityInfo.packageName
                    val appName = player.loadLabel(context.packageManager).toString()
                    val selected = packageName == selectedPlayer

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectPlayer(packageName) }
                            .padding(16.dp)
                            .focusScale(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            imageModel = { player.loadIcon(context.packageManager) },
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.focusScale()) {
                Text(text = "Cancel")
            }
        }
    )
}
