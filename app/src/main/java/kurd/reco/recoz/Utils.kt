package kurd.reco.recoz

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kurd.reco.recoz.plugin.Plugin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


inline fun Modifier.ifTrue(predicate: Boolean, builder: Modifier.() -> Modifier) = then(if (predicate) builder() else Modifier)
inline fun Modifier.ifFalse(predicate: Boolean, builder: Modifier.() -> Modifier) = then(if (!predicate) builder() else Modifier)

@Composable
fun Modifier.isFocused(
    hoverable: Boolean = true,
    focusable: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    builder: Modifier.() -> Modifier
): Modifier = composed {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    this.ifTrue(isHovered || isFocused) {
        builder()
    }.hoverable(
        interactionSource = interactionSource,
        enabled = hoverable
    ).focusable(
        interactionSource = interactionSource,
        enabled = focusable
    )
}

@Composable
fun Modifier.focusScale(
    scale: Float = 1.1F,
    hoverable: Boolean = true,
    focusable: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier = composed {
    isFocused(
        hoverable,
        focusable,
        interactionSource
    ) {
        graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    }
}

val LocalPaddingValues = compositionLocalOf<PaddingValues?> { null }

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(direction) + other.calculateStartPadding(direction),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(direction) + other.calculateEndPadding(direction),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}


@Composable
fun PaddingValues.merge(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = max(this.calculateStartPadding(direction), other.calculateStartPadding(direction)),
        top = max(this.calculateTopPadding(), other.calculateTopPadding()),
        end = max(this.calculateEndPadding(direction), other.calculateEndPadding(direction)),
        bottom = max(this.calculateBottomPadding(), other.calculateBottomPadding())
    )
}

fun Modifier.mergedLocalPadding(other: PaddingValues, additional: PaddingValues = PaddingValues(0.dp)) = composed {
    this.padding((LocalPaddingValues.current?.merge(other) ?: other).plus(additional))
}

fun Modifier.mergedLocalPadding(other: PaddingValues, additional: Dp) = composed {
    this.mergedLocalPadding(other, PaddingValues(additional))
}

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



fun hideSystemBars(window: Window) {
    WindowInsetsControllerCompat(window, window.decorView).let {
        it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        it.hide(WindowInsetsCompat.Type.systemBars())
    }
}


@Composable
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:$seconds"
}

fun showOtherVideoApps(videoUri: Uri, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(videoUri, "video/*")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, "Open video with"))
    } else {
        Toast.makeText(context, "No suitable apps found to play this video", Toast.LENGTH_SHORT).show()
    }
}