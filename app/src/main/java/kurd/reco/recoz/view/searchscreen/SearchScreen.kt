package kurd.reco.recoz.view.searchscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenRootDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kurd.reco.api.model.SearchModel
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.homescreen.LoadingBar
import kurd.reco.recoz.view.settings.logs.AppLog
import org.koin.androidx.compose.koinViewModel

private val TAG: String = "SearchScreen"

@Destination<RootGraph>
@Composable
fun SearchScreen(navigator: DestinationsNavigator) {
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val viewModel: SearchScreenVM = koinViewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                placeholder = { Text(text = "Search") },
                shape = RoundedCornerShape(size = 32.dp),
                value = query,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                },
                onValueChange = {
                    query = it
                    if (query.length >= 3) {
                        AppLog.i(TAG, "Query: $query")
                        viewModel.searchList = emptyList()
                        viewModel.search(query)
                        isLoading = true
                    }
                }
            )

            val dividerColor = if (query.isNotEmpty()) MaterialTheme.colorScheme.primary else DividerDefaults.color

            HorizontalDivider(Modifier.padding(8.dp), thickness = 3.dp, color = dividerColor)

            if (viewModel.searchList.isNotEmpty()) {
                isLoading = false
            }

            if (isLoading) {
                LoadingBar()
            }


            LazyColumn {
                items(viewModel.searchList) {
                    SearchItem(it) {
                        navigator.navigate(
                            DetailScreenRootDestination(
                                it.id.toString(),
                                it.isSeries
                            )
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun SearchItem(
    item: SearchModel,
    onCLick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier.padding(8.dp),
        onClick = {
            onCLick()
        }
    ) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            Text(
                item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            GlideImage(
                imageModel = { item.image },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                )
            )

        }
    }
}

@Preview
@Composable
private fun SearchScreenPrev() {
    RecozTheme {
        val item = SearchModel("1", "Breaking Bad", "htt", true)
        SearchItem(item)
    }
}