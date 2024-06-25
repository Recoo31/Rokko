package kurd.reco.recoz.view.detailscreen.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.api.model.SeriesItem
import kurd.reco.recoz.focusScale

@Composable
fun SeasonItem(item: SeriesItem, onClick: (Any) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .focusScale()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { onClick(item.id) },
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onClick(item.id) }
            ) {
                GlideImage(
                    imageModel = { item.poster },
                    loading = {
                        Box(modifier = Modifier.matchParentSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    modifier = Modifier.heightIn(max = 350.dp),
                    imageOptions = ImageOptions(contentScale = ContentScale.FillWidth),
                    requestOptions = { RequestOptions().timeout(5000) }
                )
            }
            Text(
                text = item.description ?: "No description",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable { expanded = !expanded }
                    .animateContentSize(animationSpec = tween(200)),
                textAlign = TextAlign.Center,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}