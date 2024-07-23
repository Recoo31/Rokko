package kurd.reco.recoz.view.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val MATERIAL_THEME_ENABLED = booleanPreferencesKey("material_theme_enabled")
        val EXTERNAL_PLAYER = stringPreferencesKey("external_player")
        val SUBTITLE_SIZE = floatPreferencesKey("subtitle_size")
    }

    private fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        runBlocking {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map {
            it[DARK_THEME_ENABLED] ?: true
        }

    fun setDarkTheme(enabled: Boolean) {
        savePreference(DARK_THEME_ENABLED, enabled)
    }

    val materialThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map {
            it[MATERIAL_THEME_ENABLED] ?: false
        }

    fun saveMaterialTheme(enabled: Boolean) {
        savePreference(MATERIAL_THEME_ENABLED, enabled)
    }

    val externalPlayer: Flow<String> = context.dataStore.data
        .map {
            it[EXTERNAL_PLAYER] ?: ""
        }

    fun setExternalPlayer(player: String) {
        savePreference(EXTERNAL_PLAYER, player)
    }

    val subtitleSize: Flow<Float> = context.dataStore.data
        .map {
            it[SUBTITLE_SIZE] ?: 12f
        }

    fun setSubtitleSize(size: Float) {
        savePreference(SUBTITLE_SIZE, size)
    }
}
