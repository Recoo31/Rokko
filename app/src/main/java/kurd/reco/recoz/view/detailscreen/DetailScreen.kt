package kurd.reco.recoz.view.detailscreen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kurd.reco.api.Resource
import kurd.reco.api.model.DetailScreenModel
import kurd.reco.recoz.MainVM
import kurd.reco.recoz.PlayerActivity
import kurd.reco.recoz.db.favorite.Favorite
import kurd.reco.recoz.db.favorite.FavoriteDao
import kurd.reco.recoz.db.plugin.PluginDao
import kurd.reco.recoz.focusScale
import kurd.reco.recoz.view.detailscreen.composables.BackImage
import kurd.reco.recoz.view.detailscreen.composables.CustomIconButton
import kurd.reco.recoz.view.detailscreen.composables.DescriptionSection
import kurd.reco.recoz.view.detailscreen.composables.MovieDetails
import kurd.reco.recoz.view.detailscreen.composables.MultiSourceDialog
import kurd.reco.recoz.view.detailscreen.composables.SeasonItem
import kurd.reco.recoz.view.detailscreen.composables.SeasonsSelector
import kurd.reco.recoz.view.homescreen.ErrorMessage
import kurd.reco.recoz.view.homescreen.LoadingBar
import kurd.reco.recoz.view.settings.logs.AppLog
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private val TAG = "DetailScreenRoot"

@Destination<RootGraph>
@Composable
fun DetailScreenRoot(
    id: String,
    isSeries: Boolean,
    navigator: DestinationsNavigator
) {
    val viewModel: DetailScreenVM = koinViewModel()
    val item by viewModel.item.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        AppLog.d(TAG, "LaunchedEffect: $id")
        viewModel.getMovie(id, isSeries)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val resource = item) {
            is Resource.Loading -> LoadingBar()
            is Resource.Success -> DetailScreen(id, resource.value, viewModel, navigator)
            is Resource.Failure -> {
                LaunchedEffect(resource) {
                    isError = true
                    errorText = resource.error
                }
            }
        }

        if (isError) {
            ErrorMessage(errorText) {
                isError = false
            }
        }
    }
}

@Composable
fun DetailScreen(
    homeID: String,
    item: DetailScreenModel,
    viewModel: DetailScreenVM,
    navigator: DestinationsNavigator,
    pluginDao: PluginDao = koinInject(),
    favoriteDao: FavoriteDao = koinInject()
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSeason by rememberSaveable { mutableIntStateOf(0) }
    val clickedItem by viewModel.clickedItem.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var showMultiSelect by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val mainVM: MainVM = koinInject()

    if (isError) {
        ErrorMessage(errorText) {
            isError = false
        }
    }

    if (showMultiSelect) {
        Dialog(onDismissRequest = { showMultiSelect = false }) {
            MultiSourceDialog(mainVM.playDataModel, context) { mainVM.playDataModel = it }
        }
    }

    when (val resource = clickedItem) {
        is Resource.Success -> {
            val playData = resource.value
            mainVM.playDataModel = playData
            LaunchedEffect(resource) {
                if (playData.urls.size > 1) {
                    showMultiSelect = true
                } else {
                    val intent = Intent(context, PlayerActivity::class.java)
                    context.startActivity(intent)
                    viewModel.clearClickedItem()
                }
            }
        }

        is Resource.Failure -> {
            LaunchedEffect(resource) {
                isError = true
                errorText = resource.error
            }
        }

        is Resource.Loading -> Unit
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item.backImage?.let {
                item {
                    BackImage(it)
                }
            }

            item {
                MovieDetails(item)
            }

            item.description?.let {
                item {
                    DescriptionSection(
                        item = it,
                        expanded = expanded,
                        onExpandClick = { expanded = !expanded }
                    )
                }
            }

            if (item.isSeries) {
                viewModel.seriesList?.let { season ->
                    item {
                        SeasonsSelector(season, selectedSeason) {
                            selectedSeason = it
                        }
                    }

                    items(season[selectedSeason].episodes) { episode ->
                        SeasonItem(item = episode) {
                            Toast.makeText(context, "Loading Video...", Toast.LENGTH_SHORT).show()
                            viewModel.getUrl(it)
                        }
                    }
                }
            } else {
                item {
                    Button(
                        onClick = { viewModel.getUrl(item.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Play",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.focusScale()
                        )
                    }
                }
            }
        }
        CustomIconButton(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) { navigator.navigateUp() }

        val favoriteIcon = favoriteDao.getFavoriteById(homeID) != null
        var isFavorite by remember { mutableStateOf(favoriteIcon) }

        CustomIconButton(
            icon = {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            if (isFavorite) {
                favoriteDao.deleteFavoriteById(homeID)
            } else {
                favoriteDao.insertFavorite(
                    Favorite(
                        id = homeID,
                        title = item.title,
                        image = item.image,
                        isSeries = item.isSeries,
                        pluginID = pluginDao.getSelectedPlugin()!!.id
                    )
                )
            }
            isFavorite = !isFavorite
        }
    }
}