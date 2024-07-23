package kurd.reco.recoz.view.settings.plugin

import android.content.Context
import kurd.reco.api.app
import kurd.reco.recoz.db.plugin.Plugin
import kurd.reco.recoz.db.plugin.PluginDao
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
        println("Plugin file does not exist: ${plugin.filePath}")
        return null
    }

    val outputDir = context.getDir("dex", Context.MODE_PRIVATE)
    val outputFile = File(outputDir, "classes.dex")

    // Check if the file already exists and is read-only, then make it writable and delete it
    if (outputFile.exists()) {
        if (!outputFile.setWritable(true)) {
            AppLog.d(TAG,"Failed to make file writable: ${outputFile.absolutePath}")
            return null
        }
        if (!outputFile.delete()) {
            AppLog.d(TAG, "Failed to delete existing file: ${outputFile.absolutePath}")
            return null
        }
    }

    ZipInputStream(FileInputStream(pluginFile)).use { zipInputStream ->
        var entry: ZipEntry? = zipInputStream.nextEntry
        while (entry != null) {
            if (entry.name == "classes.dex") {
                FileOutputStream(outputFile).use { outputStream ->
                    try {
                        zipInputStream.copyTo(outputStream)
                        // Only set the file to read-only after successful write
                        if (outputFile.setReadOnly()) {
                            AppLog.d(TAG,"File successfully set to read-only: ${outputFile.absolutePath}")
                        } else {
                            AppLog.d(TAG,"Failed to set file to read-only: ${outputFile.absolutePath}")
                        }
                        return outputFile
                    } catch (t: Throwable) {
                        println("Error writing to file: ${t.message}")
                    }
                }
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