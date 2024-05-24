package kurd.reco.recoz.plugin

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import dalvik.system.PathClassLoader
import kurd.reco.api.RemoteRepo
import kurd.reco.recoz.view.settings.plugin.extractDexFileFromZip
import org.json.JSONObject
import java.io.File
import java.util.zip.ZipFile

data class Plugin(
    val id: String,
    val name: String,
    val classPath: String,
    val className: String,
    val filePath: String
)

class PluginManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("plugin_prefs", Context.MODE_PRIVATE)
    private var pluginInstance: RemoteRepo? = null

    init {
        val update = checkUpdate()
        if (!update) {
            loadLastSelectedPlugin()
        }
    }


    fun checkUpdate(): Boolean {
        return false
    }

    fun addPlugin(plugin: Plugin) {
        sharedPreferences.edit().putString(plugin.id, plugin.filePath).apply()
    }

    fun selectPlugin(plugin: Plugin) {
        pluginInstance = loadPlugin(plugin)
        sharedPreferences.edit().putString("last_selected_plugin", plugin.id).apply()
    }

    fun getLastSelectedPluginId(): String? = sharedPreferences.getString("last_selected_plugin", null)

    fun getAllPlugins(): List<Plugin> {
        val plugins = mutableListOf<Plugin>()
        val path = context.filesDir.path

        val zipFiles = File(path).listFiles { file ->
            file.isFile && file.name.endsWith(".krd")
        }

        zipFiles?.forEach { file ->
            try {
                val plugin = getPluginFromManifest(file.absolutePath)
                plugin?.let {
                    plugins.add(it)
                    addPlugin(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "${file.nameWithoutExtension} | Error", Toast.LENGTH_SHORT).show()
            }
        }

        return plugins
    }

    private fun getPluginFromManifest(filePath: String): Plugin? {
        val zipFile = File(filePath)
        try {
            val zip = ZipFile(zipFile)
            val json = zip.getEntry("manifest.json")

            zip.getInputStream(json).bufferedReader().use {
                val string = it.readText()
                val jsonObj = JSONObject(string)
                val id = jsonObj.getString("plugin_id")
                val name = jsonObj.getString("plugin_name")
                val classPath = jsonObj.getString("package")
                val className = jsonObj.getString("package_name")

                return Plugin(id, name, classPath, className, filePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error while loading plugin from manifest", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun loadLastSelectedPlugin() {
        val lastSelectedPluginId = sharedPreferences.getString("last_selected_plugin", null)
        lastSelectedPluginId?.let { pluginId ->
            val filePath = sharedPreferences.getString(pluginId, null)
            filePath?.let { pluginFilePath ->
                val plugin = getPluginFromManifest(pluginFilePath)
                plugin?.let {
                    pluginInstance = loadPlugin(it)
                }
            }
        }
    }

    private fun loadPlugin(plugin: Plugin): RemoteRepo? {
        val dexFile = extractDexFileFromZip(context, plugin)
        return dexFile?.let { file ->
            val className = "${plugin.classPath}.${plugin.className}"
            val loader = PathClassLoader(file.absolutePath, context.classLoader)
            try {
                val pluginClass = loader.loadClass(className)
                pluginClass.getDeclaredConstructor().newInstance() as? RemoteRepo
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