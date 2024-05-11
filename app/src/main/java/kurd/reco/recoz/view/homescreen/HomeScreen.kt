package kurd.reco.recoz.view.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenRootDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.focusScale
import kurd.reco.recoz.plugin.PluginManager
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Destination<RootGraph>(start = true)
@Composable
fun HomeScreenRoot(navigator: DestinationsNavigator) {
    val viewModel: HomeScreenVM = koinViewModel()
    val pluginManager: PluginManager = koinInject()

    val movieList by viewModel.moviesList.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val resource = movieList) {
            is Resource.Loading -> {
                LoadingBar()
            }

            is Resource.Success -> {
                HomeScreen(resource.value, navigator)
            }

            is Resource.Failure -> {
                isError = true
                errorText = resource.error
            }
        }

        if (isError) {
            println("Error: $errorText")
            Text(text = errorText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun HomeScreen(movieList: List<HomeScreenModel>, navigator: DestinationsNavigator) {
    LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(3)) {
        items(movieList) {
            MovieItem(it) {
                navigator.navigate(DetailScreenRootDestination(it.id.toString(), it.isSeries))
            }
        }
    }
}

@Composable
fun LazyGridItemScope.MovieItem(item: HomeScreenModel, onItemClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .animateItem()
            .focusScale(1.04F)
            .clickable {
                onItemClick()
            },
    ) {
        GlideImage(
            imageModel = {
                item.poster
            },
            loading = {
                LoadingBar()
            },
            imageOptions = ImageOptions(
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
            ),
        )
    }
}

@Composable
fun LoadingBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}
