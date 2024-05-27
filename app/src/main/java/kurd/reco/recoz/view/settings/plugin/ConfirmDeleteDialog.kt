package kurd.reco.recoz.view.settings.plugin

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kurd.reco.recoz.plugin.Plugin

@Composable
fun ConfirmDeleteDialog(plugin: Plugin, onDeleteConfirmed: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Plugin") },
        text = { Text(text = "Are you sure you want to delete the plugin \"${plugin.name}\"?") },
        confirmButton = {
            Button(onClick = onDeleteConfirmed) { Text("Delete") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}