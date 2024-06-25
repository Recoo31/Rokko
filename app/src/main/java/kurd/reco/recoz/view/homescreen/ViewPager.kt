package kurd.reco.recoz.view.homescreen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kurd.reco.api.model.HomeItemModel
import kurd.reco.recoz.focusScale

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
    val context = LocalContext.current

    // Preload images and cache them
    LaunchedEffect(items) {
        coroutineScope {
            items.forEach {
                async {
                    Glide.with(context).load(it.poster).preload()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            animationScope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage, animationSpec = tween(durationMillis = 1000))
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
                    .wrapContentHeight()
                    .focusScale()
                    .graphicsLayer {
                        val pageOffset =
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                ImageWithShadow(items[page].poster)
                ElevatedButton(
                    onClick = { onItemClicked(items[page]) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(48.dp),
                ) {
                    Text(text = "Watch Now", modifier = Modifier.focusScale())
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
                .align(Alignment.Center),
            requestOptions = { RequestOptions().timeout(5000) }
        )
    }
}
