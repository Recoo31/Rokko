package kurd.reco.recoz.view.detailscreen.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kurd.reco.recoz.focusScale

@Composable
fun DescriptionSection(
    item: String,
    expanded: Boolean,
    onExpandClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.padding(16.dp),
        onClick = { onExpandClick() }
    ) {
        Text(
            text = item,
            modifier = Modifier
                .padding(8.dp)
                .focusScale()
                .animateContentSize(animationSpec = tween(200)),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
        )
    }
}