package kurd.reco.recoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.BottomBar
import org.koin.compose.koinInject

private val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainVM = koinInject()
            val pluginManager: PluginManager = koinInject()
            val context = LocalContext.current

//            pluginManager.injectPlugin(context)
            val navController = rememberNavController()

            RecozTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
//                    floatingActionButton = {
//                        FloatingActionButton(
//                            onClick = {
//                                viewModel.actionButton = !viewModel.actionButton
//                            }
//                        ) {
//                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Icon")
//                        }
//                    },
                    bottomBar = {
                        BottomBar(navController)
                    },
                ) { innerPadding ->

//                    if (viewModel.actionButton) {
//                        ActionBarDialog(viewModel, pluginManager)
//                    }

                    Box(modifier = Modifier.padding(innerPadding)) {
                        DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
                    }
                }
            }
        }
    }
}

//
//@Composable
//fun ActionBarDialog(viewModel: MainVM, pluginManager: PluginManager) {
//    val context = LocalContext.current
//    Dialog(onDismissRequest = { viewModel.actionButton = false }) {
//        Card(
//            modifier = Modifier
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//        ) {
//            LazyColumn(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                itemsIndexed(pluginManager.getPlugins()) { index, plugin ->
//                    Text(
//                        text = plugin.name,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                            .clickable {
//                                viewModel.actionButton = false
//                                pluginManager.selectPlugin(plugin.id, context)
//                            },
//                        textAlign = TextAlign.Center
//                    )
//                    if (index < pluginManager.getPlugins().lastIndex) {
//                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
//                    }
//                }
//            }
//        }
//    }
//}
