package kurd.reco.recoz.view.settings.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import kurd.reco.recoz.R
import kurd.reco.recoz.view.settings.SettingItem
import kurd.reco.recoz.view.settings.SettingSliderItem
import kurd.reco.recoz.view.settings.SettingsDataStore

@Composable
fun PlayerSettings(settingsDataStore: SettingsDataStore, modifier: Modifier = Modifier) {
    var openPlayerSelection by remember { mutableStateOf(false) }
    val currentPlayer by settingsDataStore.externalPlayer.collectAsState(initial = "")
    val savedSubtitleSize by settingsDataStore.subtitleSize.collectAsState(initial = 16f)
    val context = LocalContext.current


    if (openPlayerSelection) {
        PlayerSelectionDialog(
            context = context,
            selectedPlayer = currentPlayer,
            onDismiss = { openPlayerSelection = false },
            onSelectPlayer = { packageName ->
                settingsDataStore.setExternalPlayer(packageName)
                openPlayerSelection = false
            }
        )
    }

    SettingItem(
        title = "External Player",
        description = currentPlayer.ifEmpty { "Most players don't support DRM" },
        icon = ImageVector.vectorResource(id = R.drawable.round_play_arrow_24),
        onClick = { openPlayerSelection = true }
    )

    SettingSliderItem(
        icon = ImageVector.vectorResource(id = R.drawable.baseline_subtitles_24),
        title = "Subtitle Size",
        description = "Adjust the subtitle size",
        value = savedSubtitleSize,
        valueRange = 8f..36f,
        steps = 12,
        onValueChange = { newSize ->
            settingsDataStore.setSubtitleSize(newSize)
        }
    )
}