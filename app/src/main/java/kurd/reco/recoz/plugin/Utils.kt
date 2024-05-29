package kurd.reco.recoz.plugin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kurd.reco.api.app
import kurd.reco.recoz.view.settings.logs.AppLog
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

data class PluginResponseRoot(val plugins: List<PluginResponse>)
data class PluginResponse(val id: String, val name: String, val url: String, val version: String)

private val TAG = "PluginUtils"

fun getPluginFromManifest(filePath: String, url: String, version: String): Plugin? {
    return try {
        val zip = ZipFile(File(filePath))
        zip.getInputStream(zip.getEntry("manifest.json")).bufferedReader().use {
            val jsonObj = JSONObject(it.readText())
            Plugin(
                jsonObj.getString("plugin_id"),
                jsonObj.getString("plugin_name"),
                jsonObj.getString("package"),
                jsonObj.getString("package_name"),
                filePath, version, url
            )
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        AppLog.e(TAG, e.localizedMessage ?: e.message ?: "Unknown error")
        null
    }
}

suspend fun checkUpdate(plugin: Plugin, pluginDao: PluginDao) {
    val response = app.get(plugin.downloadUrl).parsed<PluginResponseRoot>()
    response.plugins.firstOrNull { it.id == plugin.id }?.let { outdated ->
        if (outdated.version != plugin.version) {
            AppLog.i(TAG, "Outdated plugin: ${plugin.name}")
            updatePlugin(plugin.copy(version = outdated.version), pluginDao, outdated.url)
        }
    }
}

suspend fun updatePlugin(plugin: Plugin, pluginDao: PluginDao, url: String) {
    val directory = File(plugin.filePath)
    if (directory.exists() && directory.delete()) {
        AppLog.i("PluginUtils", "Deleting plugin: ${plugin.name}")
        pluginDao.deletePlugin(plugin.id)
        if (downloadPlugin(url, plugin.filePath)) {
            pluginDao.insertPlugin(plugin)
        }
    } else {
        AppLog.i("PluginUtils", "Failed to delete plugin: ${plugin.name}")
    }
}

suspend fun downloadPlugin(uri: String, outputFilePath: String): Boolean {
    return try {
        app.get(uri).body.byteStream().use { input ->
            withContext(Dispatchers.IO) {
                FileOutputStream(File(outputFilePath)).use { output ->
                    input.copyTo(output)
                }
            }
        }
        AppLog.i(TAG, "Downloaded plugin: $uri")
        true
    } catch (t: Throwable) {
        AppLog.e(TAG, "Failed to download plugin: $uri")
        t.printStackTrace()
        AppLog.e(TAG, t.localizedMessage ?: t.message ?: "Unknown error")
        false
    }
}