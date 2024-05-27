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
import androidx.compose.runtime.LaunchedEffect
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
import kurd.reco.api.Resource
import kurd.reco.api.model.HomeItemModel
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.recoz.focusScale
import kurd.reco.recoz.view.settings.logs.AppLog
import org.koin.androidx.compose.koinViewModel


@Destination<RootGraph>(start = true)
@Composable
fun HomeScreenRoot(navigator: DestinationsNavigator) {
    val viewModel: HomeScreenVM = koinViewModel()
    val movieList by viewModel.moviesList.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val resource = movieList) {
            is Resource.Loading -> {
                LoadingBar()
            }
            is Resource.Success -> {
                HomeScreen(resource.value, navigator) }
            is Resource.Failure -> {
                LaunchedEffect(resource) {
                    isError = true
                    errorText = resource.error
                    AppLog.e("HomeScreenRoot", "Error loading movie list: $errorText")
                }
            }
        }

        if (isError) {
            ErrorMessage(errorText)
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
                items(item.contents) { movie ->
                    MovieItem(movie) {
                        navigator.navigate(
                            DetailScreenRootDestination(
                                movie.id.toString(),
                                movie.isSeries
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(item: HomeItemModel, onItemClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(width = 133.dp, height = 182.dp)
            .focusScale(1.04F)
            .clickable { onItemClick() },
    ) {
        GlideImage(
            imageModel = { item.poster },
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingBar()
                }
            }
        )
    }
}

@Composable
fun LoadingBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun ErrorMessage(errorText: String) {
    Text(
        text = errorText,
        color = MaterialTheme.colorScheme.error,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp)
    )
}
