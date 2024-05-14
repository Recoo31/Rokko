package kurd.reco.recoz.view

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kurd.reco.recoz.mergedLocalPadding
import kurd.reco.recoz.view.homescreen.HomeScreenVM

@Composable
fun BoxScope.SearchFAB(viewModel: HomeScreenVM) {
    FloatingSearchButton(
        modifier = Modifier.align(Alignment.BottomEnd).mergedLocalPadding(WindowInsets.ime.asPaddingValues(), 16.dp),
        onTextChange = {
            viewModel.query = it
        },
        icon = Icons.Default.Search,
        onClick = {
            viewModel.search()
        }
    )
}