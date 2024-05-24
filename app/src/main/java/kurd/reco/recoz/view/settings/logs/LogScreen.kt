package kurd.reco.recoz.view.settings.logs

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kurd.reco.recoz.view.settings.SettingsVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(viewModel: SettingsVM) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { viewModel.showBottomSheet = false },
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .padding(16.dp)
        ) {
            items(AppLog.logs) { log ->
                LogItemView(log)
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))

            }
        }
    }

}

@Composable
fun LogItemView(log: LogItem) {
    val color = when (log.type) {
        LogType.INFO -> MaterialTheme.colorScheme.primary
        LogType.DEBUG -> MaterialTheme.colorScheme.secondary
        LogType.WARNING -> MaterialTheme.colorScheme.onBackground
        LogType.ERROR -> MaterialTheme.colorScheme.error
    }

    Text(
        text = log.message,
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}