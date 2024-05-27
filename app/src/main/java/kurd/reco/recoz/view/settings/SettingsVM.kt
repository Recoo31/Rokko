package kurd.reco.recoz.view.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kurd.reco.api.app
import kurd.reco.recoz.plugin.Plugin
import kurd.reco.recoz.plugin.PluginDao
import kurd.reco.recoz.plugin.PluginResponseRoot
import kurd.reco.recoz.plugin.downloadPlugin
import kurd.reco.recoz.plugin.getPluginFromManifest
import java.io.File

class SettingsVM(private val pluginDao: PluginDao) : ViewModel() {
    var showBottomSheet by mutableStateOf(false)
    var showDeleteMode by mutableStateOf(false)
    var selectedItem by mutableStateOf("")

    fun download(url: String, context: Context) {
        val outputDir = context.filesDir.path
        viewModelScope.launch(Dispatchers.IO) {
            val response = app.get(url).parsed<PluginResponseRoot>()

            response.plugins.forEach {
                val uri = it.url
                val filename = uri.substring(uri.lastIndexOf('/') + 1)
                val filePath = "$outputDir/$filename"
                val result = downloadPlugin(uri, filePath)
                if (result) {
                    val plugin = getPluginFromManifest(filePath, url, it.version)
                    plugin?.let { pl ->
                        pluginDao.insertPlugin(pl)
                    }
                }
            }

        }
        Toast.makeText(context, "Plugins Downloaded", Toast.LENGTH_SHORT).show()
    }

    fun deletePlugin(plugin: Plugin) {
        viewModelScope.launch(Dispatchers.IO) {
            pluginDao.deletePlugin(plugin.id)
            val file = File(plugin.filePath)
            if (file.exists() ) {
                file.delete()
            }
        }
    }
}
