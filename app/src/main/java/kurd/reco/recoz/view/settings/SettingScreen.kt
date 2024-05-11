package kurd.reco.recoz.view.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlinx.coroutines.delay
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.settings.plugin.PluginBottomSheet
import org.koin.compose.koinInject

@Composable
private fun rotationAnimation(): Float {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 360f,
            animationSpec = tween(1600, easing = LinearEasing),
        )
        delay(1500)
        animatable.stop()
    }

    return animatable.value
}


@Destination<RootGraph>
@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    val viewModel: SettingsVM = koinInject()
    val pluginManager: PluginManager = koinInject()

    val settingsItems = listOf(
        SettingsItemClass(title = "Plugins"),
        SettingsItemClass(title = "Player"),
    )

    Column {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp)
                    .rotate(rotationAnimation())
            )
        }

        LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2)) {
            items(settingsItems) {
                SettingsItem(it) {
                    viewModel.showBottomSheet = !viewModel.showBottomSheet
                    viewModel.selectedItem = it.title
                }
            }
        }
    }
    if (viewModel.showBottomSheet) {
        when (viewModel.selectedItem) {
            "Plugins" -> {
                PluginBottomSheet(viewModel, pluginManager)
            }

            "Player" -> {
                Unit
            }
        }
    }
}

@Composable
fun SettingsItem(settingsItem: SettingsItemClass, onItemClick: () -> Unit) {
    OutlinedCard(
        onClick = onItemClick,
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = settingsItem.title, textAlign = TextAlign.Center)
        }
    }
}

data class SettingsItemClass(
    val title: String
)


@Preview
@Composable
private fun SettingScreenPrev() {
    RecozTheme {
        SettingScreen()
    }
}