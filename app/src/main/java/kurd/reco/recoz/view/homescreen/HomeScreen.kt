package kurd.reco.recoz.view.homescreen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenRootDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.api.Resource
import kurd.reco.api.model.HomeItemModel
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.recoz.MainVM
import kurd.reco.recoz.PlayerActivity
import kurd.reco.recoz.view.settings.logs.AppLog
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Destination<RootGraph>(start = true)
@Composable
fun HomeScreenRoot(navigator: DestinationsNavigator) {
    val viewModel: HomeScreenVM = koinViewModel()
    val mainVM: MainVM = koinInject()
    val context = LocalContext.current
    val movieList by viewModel.moviesList.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val resource = movieList) {
            is Resource.Loading -> {
                LoadingBar()
            }
            is Resource.Success -> {
                HomeScreen(resource.value, viewModel, navigator) }
            is Resource.Failure -> {
                LaunchedEffect(resource) {
                    isError = true
                    errorText = resource.error
                    AppLog.e("HomeScreenRoot", "Error loading movie list: $errorText")
                }
            }
        }

        if (isError) {
            ErrorMessage(errorText) {
                isError = !isError
            }
        }
    }
}

@Composable
fun HomeScreen(movieList: List<HomeScreenModel>, viewModel: HomeScreenVM, navigator: DestinationsNavigator) {
    val clickedItem by viewModel.clickedItem.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val mainVM: MainVM = koinInject()

    if (isError) {
        ErrorMessage(errorText) {
            isError = !isError
        }
    }

    when (val resource = clickedItem) {
        is Resource.Success -> {
            val playData = resource.value
            mainVM.playDataModel = playData
            val intent = Intent(context, PlayerActivity::class.java)
            context.startActivity(intent)
        }
        is Resource.Failure -> {
            LaunchedEffect(resource) {
                isError = true
                errorText = resource.error
            }
        }
        is Resource.Loading -> Unit
    }

    LazyColumn {

        viewModel.pagerList?.let {
            item {
                ViewPager(it, onItemClicked = {
                    if (it.isLiveTv) {
                        viewModel.getUrl(it.id)
                    } else {
                        navigator.navigate(
                            DetailScreenRootDestination(
                                it.id.toString(),
                                it.isSeries
                            )
                        )
                    }
                })
            }
        }

        items(movieList) { movie ->
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyRow {
                items(movie.contents) { homeItem ->
                    MovieItem(homeItem) {
                        if (homeItem.isLiveTv) {
                            viewModel.getUrl(homeItem.id)
                        } else {
                            navigator.navigate(
                                DetailScreenRootDestination(
                                    homeItem.id.toString(),
                                    homeItem.isSeries
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MovieItem(item: HomeItemModel, onItemClick: () -> Unit) {
    GlideImage(
        imageModel = { item.poster },
        loading = {
            Box(Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
                LoadingBar()
            }
        },
        imageOptions = ImageOptions(
            contentScale = ContentScale.Fit
        ),
        requestOptions = { RequestOptions().timeout(5000) },
        modifier = Modifier
            .heightIn(180.dp, 215.dp)
            .widthIn(130.dp, 165.dp)
            .padding(horizontal = 10.dp)
            .clip(
                RoundedCornerShape(18.dp)
            )
            .clickable { onItemClick() }
    )
}

@Composable
fun LoadingBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
fun ErrorMessage(errorText: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Text(
                text = "Ok",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDismiss() }
            )
        },
        title = { Text(text = "Error") },
        text = { Text(text = errorText) },
        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) }
    )
}
