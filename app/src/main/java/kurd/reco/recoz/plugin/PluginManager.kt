package kurd.reco.recoz.plugin

import android.content.Context
import dalvik.system.PathClassLoader
import kurd.reco.recoz.data.repo.RemoteRepo
import java.io.File

data class Plugin(
    val id: String,
    val name: String,
    val description: String,
    val remoteRepo: RemoteRepo
)


class PluginManager {
    private val plugins: MutableList<Plugin> = mutableListOf()
    private var selectedPlugin: Plugin? = null

    fun selectPlugin(pluginId: String, context: Context) {
        selectedPlugin = plugins.find { it.id == pluginId }
    }

    fun getSelectedPlugin(): Plugin? {
        return selectedPlugin
    }

    fun addPlugin(plugin: Plugin) {
        plugins.add(plugin)
    }

    fun removePlugin(pluginId: String) {
        plugins.removeAll { it.id == pluginId }
    }

    fun getPlugins(): List<Plugin> {
        return plugins
    }

    fun injectPlugin(context: Context) {
        val pluginPath = context.filesDir.path + "/classes.dex"
        val className = "kurd.reco.tmdbapi.Tmdb"

        val aarFile = File(pluginPath)
        val exists = aarFile.exists()

        println("AAR file path: ${aarFile.path}")
        println("AAR file exists: $exists")

        if (exists) {
            val loader = PathClassLoader(pluginPath, context.classLoader)
            try {
                val pluginClass = loader.loadClass(className)
                addPlugin(Plugin("tmdb", "tmdb", "Tmdb Api", pluginClass.newInstance() as RemoteRepo))
                println(getPlugins())

            } catch (e: ClassNotFoundException) {
                println("Class not found: $className")
            }
        } else {
            println("Not exists")
        }
    }
}