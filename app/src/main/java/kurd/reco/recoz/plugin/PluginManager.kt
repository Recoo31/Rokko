package kurd.reco.recoz.plugin

import android.content.Context
import androidx.lifecycle.ViewModel
import dalvik.system.PathClassLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kurd.reco.api.RemoteRepo
import kurd.reco.api.app
import kurd.reco.recoz.view.settings.logs.AppLog
import kurd.reco.recoz.view.settings.plugin.extractDexFileFromZip

class PluginManager(private val pluginDao: PluginDao, private val deletedPlugin: DeletedPluginDao,appContext: Context) : ViewModel() {
    private val context = appContext.applicationContext
    private var pluginInstance: RemoteRepo? = null
    private val outputDir = context.filesDir.path

    init {
        try {
            runBlocking {
                checkPluginUpdate()
            }
            loadLastSelectedPlugin()
        } catch (t: Throwable) {
            t.printStackTrace()
            AppLog.e("PluginManager", "Error loading plugins: ${t.localizedMessage ?: "Unknown error"}")
        }
//        val plugin = Plugin(
//            id = "tvplus",
//            name = "TvPlus",
//            classPath = "kurd.reco.tvplus",
//            className = "TvPlus",
//            version = "1.0",
//            downloadUrl = "https://raw.githubusercontent.com/Recoo31/RoxioPlugins/main/version.json",
//            filePath = "/data/user/0/kurd.reco.recoz/files/TvPlus.krd"
//        )
//        pluginDao.insertPlugin(plugin)
    }

    private suspend fun checkPluginUpdate() {
        coroutineScope {
            val pluginGroups = pluginDao.getAllPlugins().groupBy { it.downloadUrl }

            pluginGroups.entries.map { (url, plugins) ->
                launch(Dispatchers.IO) {
                    val response = app.get(url).parsed<PluginResponseRoot>()
                    val remotePlugins = response.plugins

                    plugins.map { plugin ->
                        checkUpdate(plugin, pluginDao, remotePlugins)
                    }

                    checkAndDownloadNewPlugins(url, remotePlugins, pluginDao, deletedPlugin, outputDir)
                }
            }
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
//            AppLog.d("PluginManager", "Selected plugin: ${it.name}")
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
