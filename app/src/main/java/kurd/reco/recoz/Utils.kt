package kurd.reco.recoz

import android.content.Context
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lagradost.nicehttp.Requests
import com.lagradost.nicehttp.ResponseParser
import kurd.reco.recoz.plugin.Plugin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.reflect.KClass

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(val error: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

var app = Requests(responseParser = object : ResponseParser {
    val mapper: ObjectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION)
    }

    override fun <T : Any> parse(text: String, kClass: KClass<T>): T {
        return mapper.readValue(text, kClass.java)
    }

    override fun <T : Any> parseSafe(text: String, kClass: KClass<T>): T? {
        return try {
            mapper.readValue(text, kClass.java)
        } catch (e: Exception) {
            null
        }
    }

    override fun writeValueAsString(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }
})

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
fun LocalPadding(additional: PaddingValues = PaddingValues(0.dp)): PaddingValues {
    return LocalPaddingValues.current?.plus(additional) ?: additional
}

@Composable
fun LocalPadding(additional: Dp): PaddingValues {
    return LocalPaddingValues.current?.plus(PaddingValues(additional)) ?: PaddingValues(additional)
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