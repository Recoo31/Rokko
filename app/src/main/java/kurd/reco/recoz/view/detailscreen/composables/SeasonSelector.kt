package kurd.reco.recoz.view.detailscreen.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kurd.reco.api.model.SeriesDataModel
import kurd.reco.recoz.focusScale

@Composable
fun SeasonsSelector(
    season: List<SeriesDataModel>,
    selectedSeason: Int,
    onSeasonSelected: (Int) -> Unit
) {
    LazyRow {
        items(season.size) { item ->
            val selected = item == selectedSeason
            SeasonSelectorItem(item, selected, onSeasonSelected)
        }
    }
}

@Composable
fun SeasonSelectorItem(item: Int, selected: Boolean, onSeasonSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            .size(width = 100.dp, height = 50.dp)
            .focusScale()
            .clip(CircleShape)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
            )
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                CircleShape
            )
            .clickable { onSeasonSelected(item) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Season ${item + 1}",
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}