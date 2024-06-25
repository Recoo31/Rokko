package kurd.reco.recoz.view.settings.composables

import androidx.compose.runtime.Composable
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
import kurd.reco.recoz.view.settings.SettingsDataStore
import kurd.reco.recoz.view.settings.logs.LogScreen

@Composable
fun DeveloperSettings(settingsDataStore: SettingsDataStore,  modifier: Modifier = Modifier) {
    var showLogs by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showLogs) {
        LogScreen {
            showLogs = false
        }
    }

    SettingItem(
        icon = ImageVector.vectorResource(id = R.drawable.logs_icon),
        title = "Logs",
        description = "App logs and errors",
        onClick = {
            showLogs = !showLogs
        }
    )
}