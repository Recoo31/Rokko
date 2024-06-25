package kurd.reco.recoz.view.settings.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import kurd.reco.recoz.R
import kurd.reco.recoz.view.settings.SettingItem
import kurd.reco.recoz.view.settings.SettingsDataStore


@Composable
fun GeneralSettings(settingsDataStore: SettingsDataStore, modifier: Modifier = Modifier) {
    val isDarkThemeEnabled by settingsDataStore.darkThemeEnabled.collectAsState(false)
    val isMaterialThemeEnabled by settingsDataStore.materialThemeEnabled.collectAsState(false)

    SettingItem(
        title = "Dark Theme",
        description = "Use dark theme for the app",
        icon = ImageVector.vectorResource(id = R.drawable.rounded_dark_mode_24),
        isChecked = isDarkThemeEnabled,
        onCheckedChange = { settingsDataStore.setDarkTheme(it) },
        onClick = { settingsDataStore.setDarkTheme(!isDarkThemeEnabled) }
    )

    SettingItem(
        title = "Material Theme",
        description = "Use material theme for the app",
        icon = ImageVector.vectorResource(id = R.drawable.rounded_palette_24),
        isChecked = isMaterialThemeEnabled,
        onCheckedChange = { settingsDataStore.saveMaterialTheme(it) },
        onClick = { settingsDataStore.saveMaterialTheme(!isMaterialThemeEnabled) }
    )
}