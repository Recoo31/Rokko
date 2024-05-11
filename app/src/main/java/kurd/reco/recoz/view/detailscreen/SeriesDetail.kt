package kurd.reco.recoz.view.detailscreen

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.unit.dp
import kurd.reco.recoz.data.model.SeriesDataModel

@Composable
fun SeasonsSelector(season: List<SeriesDataModel>, selectedSeason: Int, onSeasonSelected: (Int) -> Unit) {
    LazyRow {
        items(season.size) { item ->
            val selected = item == selectedSeason
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                    .clickable { onSeasonSelected(item) }
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        CircleShape
                    )
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item + 1}",
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}