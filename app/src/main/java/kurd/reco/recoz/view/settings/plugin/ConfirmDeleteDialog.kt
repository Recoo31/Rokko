package kurd.reco.recoz.view.settings.plugin

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kurd.reco.recoz.focusScale
import kurd.reco.recoz.plugin.Plugin

@Composable
fun ConfirmDeleteDialog(plugin: Plugin, onDeleteConfirmed: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Plugin") },
        text = { Text(text = "Are you sure you want to delete the plugin \"${plugin.name}\"?") },
        confirmButton = {
            Button(onClick = onDeleteConfirmed, modifier = Modifier.focusScale(1.2f)) { Text("Delete") }
        },
        dismissButton = {
            Button(onClick = onDismiss, modifier = Modifier.focusScale(1.2f)) { Text("Cancel") }
        }
    )
}