package kurd.reco.recoz.view.settings.plugin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.settings.SettingsVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginBottomSheet(viewModel: SettingsVM, pluginManager: PluginManager) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = { viewModel.showBottomSheet = false },
        sheetState = sheetState
    ) {
        val list = pluginManager.getAllPlugins()
        val lastPlugin = pluginManager.getLastSelectedPluginId()

        LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2)) {
            items(list) {
                val isSelected = it.id == lastPlugin
                OutlinedCard(
                    onClick = {
                        viewModel.showBottomSheet = false
                        pluginManager.selectPlugin(it)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(60.dp),
                    colors = CardColors(
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = if (isSelected) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        CardDefaults.outlinedCardBorder()
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = it.name, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}