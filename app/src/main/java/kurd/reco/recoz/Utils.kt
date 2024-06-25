package kurd.reco.recoz

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer


inline fun Modifier.ifTrue(predicate: Boolean, builder: Modifier.() -> Modifier) = then(if (predicate) builder() else Modifier)

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
    scale: Float = 1.08F,
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

fun Context.getAppVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}
