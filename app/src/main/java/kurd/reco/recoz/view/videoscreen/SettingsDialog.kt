package kurd.reco.recoz.view.videoscreen

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kurd.reco.recoz.ui.theme.RecozTheme
import org.jsoup.internal.StringUtil.padding

@OptIn(UnstableApi::class)
@Composable
fun SettingsDialog(
    trackSelector: DefaultTrackSelector,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = listOf("Quality", "Audio", "Subtitle")
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var selectedSetting by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth(0.5f)
                .padding(16.dp)
                .clickable(false) {} // Prevent click-through
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    itemsIndexed(settings) { index, setting ->
                        val isSelected = index == selectedIndex
                        val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        val dividerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedIndex = index
                                    selectedSetting = setting
                                }
                        ) {
                            Text(
                                text = setting,
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 24.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth().height(1.dp).width(48.dp),
                                color = dividerColor
                            )
                        }
                    }
                }

                when (selectedSetting) {
                    "Quality" -> {
                        QualitySelectorDialog(trackSelector, onDismiss)
                    }
                    "Audio" -> {
                        AudioSelectorDialog(trackSelector, onDismiss)
                    }
                    "Subtitle" -> {
                        SubtitleSelectorDialog(trackSelector, onDismiss)
                    }
                }
            }
        }
    }
}

