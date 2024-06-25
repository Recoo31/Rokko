package kurd.reco.recoz

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kurd.reco.api.app
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.plugin.DeletedPlugin
import kurd.reco.recoz.plugin.DeletedPluginDao
import kurd.reco.recoz.plugin.Plugin
import kurd.reco.recoz.plugin.PluginDao
import kurd.reco.recoz.view.settings.plugin.downloadPlugins
import java.io.File
import java.io.FileOutputStream

data class VersionData(
    val version: Double,
    val downloadUrl: String,
    val changeLog: List<String>,
)


class MainVM(private val pluginDao: PluginDao, private val deletedPluginDao: DeletedPluginDao): ViewModel() {
    var playDataModel: PlayDataModel? = null
    var showUpdateDialog by mutableStateOf(false)
    var downloadProgress by mutableFloatStateOf(0f)
    private val versionLink = "https://raw.githubusercontent.com/Recoo31/Rokko/master/version.json"
    var changeLog: List<String> = emptyList()
    private var appLink: String? = null


    fun checkAppUpdate(context: Context) {
        val currentVersion = getCurrentAppVersion(context)
        viewModelScope.launch {
            val response = app.get(versionLink).parsed<VersionData>()
            changeLog = response.changeLog
            appLink = response.downloadUrl

            if (response.version > currentVersion.toDouble()) {
                showUpdateDialog = true
            }
        }
    }

    private fun getCurrentAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Unknown"
        }
    }

    fun downloadApk(outputFilePath: File) {
        viewModelScope.launch(Dispatchers.IO) {
            appLink?.let { link ->
                app.get(link).body.use { response ->
                    FileOutputStream(outputFilePath).use { output ->
                        val totalBytes = response.contentLength()
                        var bytesCopied: Long = 0
                        val input = response.byteStream()

                        val buffer = ByteArray(8 * 1024)
                        var bytes = input.read(buffer)
                        while (bytes >= 0) {
                            output.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            downloadProgress = (bytesCopied / totalBytes.toFloat()) * 100
                            bytes = input.read(buffer)
                        }
                    }
                }
            }
        }
    }

    fun installUpdate(apkFile: File, context: Context) {
        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
        val installIntent = Intent(Intent.ACTION_VIEW)
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(installIntent)
    }

    fun download(url: String, context: Context) {
        val outputDir = context.filesDir.path
        viewModelScope.launch(Dispatchers.IO) {
            downloadPlugins(url, pluginDao, outputDir)
        }
        Toast.makeText(context, "Plugins Downloading...", Toast.LENGTH_SHORT).show()
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
