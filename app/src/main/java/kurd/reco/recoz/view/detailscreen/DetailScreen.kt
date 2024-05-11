package kurd.reco.recoz.view.detailscreen

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kurd.reco.recoz.PlayerActivity
import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.DetailScreenModel
import kurd.reco.recoz.view.homescreen.LoadingBar
import org.koin.androidx.compose.koinViewModel
import java.io.Serializable

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
    val clickedItem by viewModel.clickedItem.collectAsState()
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (isError) {
        println("Error: $errorText")
        Text(
            text = errorText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }

    when (val resource = clickedItem) {
        is Resource.Success -> {
            val playData = resource.value
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra("url", playData.url)
                if (playData.title != null) {
                    putExtra("title", playData.title)
                }
                if (playData.drm != null) {
                    putExtra("licenseUrl", playData.drm.licenseUrl)
                    if (playData.drm.headers != null) {
                        putExtra("headers", playData.drm.headers as Serializable)
                    }
                }
            }
            context.startActivity(intent)
        }
        is Resource.Failure -> {
            isError = true
            errorText = resource.error
        }
        is Resource.Loading -> {
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            BackImage(item)
        }

        item {
            MovieDetails(item)
        }

        item {
            DescriptionSection(
                item = item,
                expanded = expanded,
                onExpandClick = { expanded = !expanded }
            )
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
                    onClick = {
                        viewModel.getUrl(item.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "Play", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }


}