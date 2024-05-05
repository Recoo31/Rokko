package kurd.reco.recoz.view.homescreen

import ErrorDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.plugin.PluginManager
import org.koin.compose.koinInject

@Destination<RootGraph>(start = true)
@Composable
fun HomeScreenRoot(navigator: DestinationsNavigator) {
    val viewModel: HomeScreenVM = koinInject()
    val pluginManager: PluginManager = koinInject()


    val movieList by viewModel.moviesList.collectAsState()

    var isClicked by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }


    Button(onClick = { isClicked = !isClicked }) {
        Text(text = "Click")
    }

    if (isClicked) {
        LaunchedEffect(key1 = true) {
            viewModel.getMovies()
        }
    }

    if (isError) {
        ErrorDialog(errorMessage = errorText) {
            isError = !isError
        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val resource = movieList) {
            is Resource.Loading -> {
                LoadingBar()
            }

            is Resource.Success -> {
                HomeScreen(resource.value)
            }

            is Resource.Failure -> {
                isError = !isError
                errorText = resource.error
            }
        }
    }
}


@Composable
fun HomeScreen(movieList: List<HomeScreenModel>) {
    LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(3)) {
        items(movieList) {
            MovieItem(it)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieItem(item: HomeScreenModel) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(2.dp),
    ) {
        GlideImage(
            model = item.poster,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun LoadingBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}
