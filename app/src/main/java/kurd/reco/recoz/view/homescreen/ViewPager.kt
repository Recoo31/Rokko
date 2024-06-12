package kurd.reco.recoz.view.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kurd.reco.api.model.HomeItemModel

@Composable
fun ViewPager(
    items: List<HomeItemModel>,
    onItemClicked: (HomeItemModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState {
        items.size
    }
    val animationScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            animationScope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            Box(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                ImageWithShadow(items[page].poster)
                ElevatedButton(
                    onClick = { onItemClicked(items[page]) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(48.dp),
                ) {
                    Text(text = "Watch Now")
                }
            }
        }

        ElevatedCard {
            Row(
                Modifier
                    .padding(8.dp)
                    .wrapContentHeight(),
            ) {
                repeat(pagerState.pageCount) { pageIndex ->
                    val color =
                        if (pagerState.currentPage == pageIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.3f
                        )
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageWithShadow(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 20.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                clip = true,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        GlideImage(
            imageModel = { imageUrl },
            modifier = modifier
                .size(width = 600.dp, height = 450.dp)
                .align(Alignment.Center),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop
            ),
            requestOptions = { RequestOptions().timeout(5000) }
        )
    }
}
