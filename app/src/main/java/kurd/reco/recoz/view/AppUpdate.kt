package kurd.reco.recoz.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kurd.reco.recoz.MainVM
import java.io.File

@Composable
fun AppUpdateDialog(viewModel: MainVM, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val outputFilePath = File(context.filesDir, "apk-update.apk")

    if (viewModel.showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                when(viewModel.downloadProgress) {
                    0f -> {
                        TextButton(onClick = {
                            viewModel.downloadApk(outputFilePath)
                        }) {
                            Text(text = "Update")
                        }
                    }
                    100f -> {
                        TextButton(onClick = {
                            viewModel.installUpdate(outputFilePath, context)
                        }) {
                            Text(text = "Install")
                        }
                    }
                    else -> Text(text = "Downloading: ${viewModel.downloadProgress.toInt()}%")
                }
            },
            title = { Text(text = "App Update", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(text = "New version available", style = MaterialTheme.typography.bodyLarge)
                    if (viewModel.changeLog.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Change Log:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        viewModel.changeLog.forEach { log ->
                            Text(text = "- $log", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
        )
    }
}