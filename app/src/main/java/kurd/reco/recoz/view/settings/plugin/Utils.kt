package kurd.reco.recoz.view.settings.plugin

import android.content.Context
import kurd.reco.api.app
import kurd.reco.recoz.plugin.Plugin
import kurd.reco.recoz.plugin.PluginDao
import kurd.reco.recoz.plugin.PluginResponseRoot
import kurd.reco.recoz.plugin.downloadPlugin
import kurd.reco.recoz.plugin.getPluginFromManifest
import kurd.reco.recoz.view.settings.logs.AppLog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

private const val TAG = "PluginDownloader"

fun extractDexFileFromZip(context: Context, plugin: Plugin): File? {
    val pluginFile = File(plugin.filePath)
    if (!pluginFile.exists()) {
        return null
    }

    val outputDir = context.getDir("dex", Context.MODE_PRIVATE)
    val outputFile = File(outputDir, "classes.dex")

    ZipInputStream(FileInputStream(pluginFile)).use { zipInputStream ->
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            if (entry.name == "classes.dex") {
                FileOutputStream(outputFile).use { outputStream ->
                    zipInputStream.copyTo(outputStream)
                }
                return outputFile
            }
            entry = zipInputStream.nextEntry
        }
    }
    return null
}

suspend fun downloadPlugins(url: String, pluginDao: PluginDao, outputDir: String) {
    val response = app.get(url).parsed<PluginResponseRoot>()
    val plugins = pluginDao.getAllPlugins()

    response.plugins.forEach {
        val uri = it.url
        val filename = uri.substring(uri.lastIndexOf('/') + 1)
        val filePath = "$outputDir/$filename"
        val result = downloadPlugin(uri, filePath)
        if (result) {
            val plugin = getPluginFromManifest(filePath, url, it.version)
            plugin?.let { pl ->
                if (plugins.any { p -> p.id == pl.id }) {
                    AppLog.d(TAG,"Plugin ${pl.id} already exists, deleting...")
                    pluginDao.deletePlugin(pl.id)
                }
                pluginDao.insertPlugin(pl)
            }
        }
    }
}