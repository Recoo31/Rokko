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
import kurd.reco.recoz.plugin.DeletedPlugin
import kurd.reco.recoz.plugin.DeletedPluginDao
import kurd.reco.recoz.plugin.Plugin
import kurd.reco.recoz.plugin.PluginDao
import kurd.reco.recoz.view.settings.plugin.downloadPlugins
import java.io.File

class SettingsVM(private val pluginDao: PluginDao, private val deletedPluginDao: DeletedPluginDao) : ViewModel() {
    var showBottomSheet by mutableStateOf(false)
    var selectedItem by mutableStateOf("")
    private val TAG = "SettingsVM"

    fun download(url: String, context: Context) {
        val outputDir = context.filesDir.path
        viewModelScope.launch(Dispatchers.IO) {
            downloadPlugins(url, pluginDao, outputDir)
        }
        Toast.makeText(context, "Plugins Downloaded", Toast.LENGTH_SHORT).show()
    }

    fun deletePlugin(plugin: Plugin) {
        viewModelScope.launch(Dispatchers.IO) {
            pluginDao.deletePlugin(plugin.id)
            deletedPluginDao.insertDeletedPlugin(DeletedPlugin(plugin.id))
            val file = File(plugin.filePath)
            if (file.exists() ) {
                file.delete()
            }
        }
    }
}
