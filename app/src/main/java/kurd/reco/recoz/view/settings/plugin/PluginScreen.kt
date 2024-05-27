package kurd.reco.recoz.view.settings.plugin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kurd.reco.recoz.plugin.Plugin
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.settings.SettingsVM
import kurd.reco.recoz.view.settings.logs.AppLog

private val TAG = "PluginBottomSheet"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginBottomSheet(viewModel: SettingsVM, pluginManager: PluginManager) {
    val sheetState = rememberModalBottomSheetState()
    var showAddDialog by remember { mutableStateOf(false) }
    var isDeleteMode by remember { mutableStateOf(false) }
    var pluginToDelete by remember { mutableStateOf<Plugin?>(null) }
    val textTitle = if (isDeleteMode) "Delete" else "Select"
    var plugins = pluginManager.getAllPlugins()


    if (showAddDialog) {
        DownloadDialog(viewModel) { showAddDialog = false }
    }

    pluginToDelete?.let { plugin ->
        ConfirmDeleteDialog(plugin,
            onDeleteConfirmed = {
                viewModel.deletePlugin(plugin)
                pluginToDelete = null
                plugins = pluginManager.getAllPlugins()
            },
            onDismiss = { pluginToDelete = null }
        )
    }

    ModalBottomSheet(
        onDismissRequest = { viewModel.showBottomSheet = false },
        sheetState = sheetState
    ) {
        val lastPlugin = pluginManager.getLastSelectedPluginId()

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$textTitle plugin",
                    modifier = Modifier.align(Alignment.TopCenter),
                    style = MaterialTheme.typography.titleMedium
                )
                Row(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                ) {
                    IconButton(onClick = { isDeleteMode = !isDeleteMode }) {
                        Icon(
                            imageVector = if (isDeleteMode) Icons.Default.Check else Icons.Default.Delete,
                            contentDescription = if (isDeleteMode) "Exit Delete Mode" else "Enter Delete Mode"
                        )
                    }
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Plugin")
                    }
                }
            }

            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(plugins) { plugin ->
                    val isSelected = plugin.id == lastPlugin
                    OutlinedCard(
                        onClick = {
                            if (isDeleteMode) {
                                pluginToDelete = plugin
                            } else {
                                viewModel.showBottomSheet = false
                                pluginManager.selectPlugin(plugin)
                                AppLog.d(TAG, "Selected plugin: ${plugin.name}")
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(50.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = if (isSelected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            CardDefaults.outlinedCardBorder()
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = plugin.name, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}