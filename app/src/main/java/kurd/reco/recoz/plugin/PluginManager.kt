package kurd.reco.recoz.plugin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dalvik.system.PathClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kurd.reco.api.RemoteRepo
import kurd.reco.recoz.view.settings.logs.AppLog
import kurd.reco.recoz.view.settings.plugin.extractDexFileFromZip

class PluginManager(private val pluginDao: PluginDao, appContext: Context) : ViewModel() {
    private val context = appContext.applicationContext
    private var pluginInstance: RemoteRepo? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            checkPluginUpdate()
        }
        loadLastSelectedPlugin()
//        val plugin = Plugin(
//            id = "digiturk",
//            name = "Digiturk",
//            classPath = "kurd.reco.digiturk",
//            className = "Digiturk",
//            version = "1",
//            downloadUrl = "https://raw.githubusercontent.com/Recoo31/RoxioPlugins/builds/plugins.json",
//            filePath = "/data/user/0/kurd.reco.recoz/files/Digiturk.krd"
//        )
//        pluginDao.insertPlugin(plugin)
    }

    private suspend fun checkPluginUpdate() {
        getAllPlugins().forEach { plugin ->
            checkUpdate(plugin, pluginDao)
        }
    }

    fun selectPlugin(plugin: Plugin) {
        pluginDao.clearSelectedPlugin()
        AppLog.d("PluginManager", "selectPlugin: ${plugin.id}")
        pluginDao.selectPlugin(plugin.id)
        pluginInstance = loadPlugin(plugin)
    }

    fun getLastSelectedPluginId(): String? {
        return pluginDao.getSelectedPlugin()?.id
    }

    fun getAllPlugins(): List<Plugin> = pluginDao.getAllPlugins()

    private fun loadLastSelectedPlugin() {
        pluginDao.getSelectedPlugin()?.let {
            AppLog.d("PluginManager", "Selected plugin: ${it.name}")
            pluginInstance = loadPlugin(it)
        }
    }

    private fun loadPlugin(plugin: Plugin): RemoteRepo? {
        val dexFile = extractDexFileFromZip(context, plugin)
        return dexFile?.let { file ->
            val className = "${plugin.classPath}.${plugin.className}"
            val loader = PathClassLoader(file.absolutePath, context.classLoader)
            try {
                loader.loadClass(className).getDeclaredConstructor().newInstance() as? RemoteRepo
            } catch (e: ClassNotFoundException) {
                println("Class not found: $className")
                null
            }
        }
    }

    fun getSelectedPlugin(): RemoteRepo {
        return pluginInstance ?: throw IllegalStateException("Plugin instance not initialized")
    }
}
