package kurd.reco.recoz.view.settings.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kurd.reco.recoz.view.settings.SettingsVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(viewModel: SettingsVM) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = { viewModel.showBottomSheet = false },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .padding(16.dp)
        ) {
            SelectionContainer {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp)
                ) {
                    items(AppLog.logs) { log ->
                        LogItemView(log)
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp)
            ) {
                ShareButton(onClick = { shareLogs(context) }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                ClearButton(onClick = { AppLog.logs = emptyList() }, modifier = Modifier.weight(1f))
            }

        }
    }
}

@Composable
fun ShareButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share Logs",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Share Logs", fontSize = 16.sp, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ClearButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Clear Logs",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Clear Logs", fontSize = 16.sp, style = MaterialTheme.typography.bodyMedium)
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