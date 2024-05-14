package kurd.reco.recoz.view.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenRootDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.api.model.HomeItemModel
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.api.Resource
import kurd.reco.recoz.focusScale
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.SearchFAB
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
                SearchFAB(viewModel)
            }

            is Resource.Failure -> {
                isError = true
                errorText = resource.error
            }
        }

        if (isError) {
            println("Error: $errorText")
            Text(text = errorText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun HomeScreen(movieList: List<HomeScreenModel>, navigator: DestinationsNavigator) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movieList) { item ->
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 14.dp)
            )
            LazyRow {
                items(item.contents) {
                    MovieItem(item = it, onItemClick = {
                        navigator.navigate(DetailScreenRootDestination(it.id.toString(), it.isSeries))
                    })
                }
            }
        }
    }
}

@Composable
fun MovieItem(item: HomeItemModel, onItemClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .size(width = 133.dp, height = 182.dp)
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

        )
    }
}

@Composable
fun LoadingBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}
