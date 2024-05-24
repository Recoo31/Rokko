package kurd.reco.recoz.view.settings.plugin

import android.content.Context
import kurd.reco.recoz.plugin.Plugin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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