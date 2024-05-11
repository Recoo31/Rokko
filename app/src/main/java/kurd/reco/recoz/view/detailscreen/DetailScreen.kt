package kurd.reco.recoz.view.detailscreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.recoz.LocalPadding
import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.DetailScreenModel
import kurd.reco.recoz.data.model.SeriesItem
import kurd.reco.recoz.view.homescreen.LoadingBar
import org.koin.androidx.compose.koinViewModel

private val TAG = "DetailScreenRoot"

@Destination<RootGraph>
@Composable
fun DetailScreenRoot(id: String, isSeries: Boolean, navigator: DestinationsNavigator) {
    val viewModel: DetailScreenVM = koinViewModel()

    val item by viewModel.item.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        Log.d(TAG, "LaunchedEffect: $id")
        viewModel.getMovie(id, isSeries)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val resource = item) {
            is Resource.Loading -> {
                LoadingBar()
            }

            is Resource.Success -> {
                DetailScreen(resource.value, viewModel)
            }

            is Resource.Failure -> {
                isError = true
                errorText = resource.error
            }
        }

        if (isError) {
            println("Error: $errorText")
            Text(
                text = errorText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DetailScreen(item: DetailScreenModel, viewModel: DetailScreenVM) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSeason by rememberSaveable { mutableIntStateOf(0) }


    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box {
                GlideImage(
                    imageModel = { item.backImage },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit
                    ),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.background
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    GlideImage(imageModel = { item.image }, modifier = Modifier.fillMaxSize())
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.title,
                        maxLines = 2,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        item {
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp),
                onClick = {
                    expanded = !expanded
                }
            ) {
                Text(
                    text = item.description,
                    modifier = Modifier
                        .padding(8.dp)
                        .animateContentSize(animationSpec = tween(200)),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (item.isSeries) {
            viewModel.seriesList?.let { season ->
                item {
                    LazyRow {
                        items(season.size) { item ->
                            val selected = item == selectedSeason
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                                    .clickable { selectedSeason = item }
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

                items(season[selectedSeason].episodes) { episode ->
                    SeasonItem(item = episode) {
                        println(it)
                    }
                }

            }
        } else {
            item {
                Button(
                    onClick = {
                        println(item.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "Play")
                }
            }
        }
    }
}


@Composable
fun SeasonItem(item: SeriesItem, onClick: (Any) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),

        ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                text = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { onClick(item.id) },
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onClick(item.id) },
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
                    modifier = Modifier.fillMaxWidth()
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
                color = MaterialTheme.colorScheme.primary,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}