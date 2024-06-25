package kurd.reco.recoz

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.AppUpdateDialog
import kurd.reco.recoz.view.BottomBar
import kurd.reco.recoz.view.settings.SettingsDataStore
import kurd.reco.recoz.view.settings.plugin.PluginDialog
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainVM = koinInject()
            val pluginManager: PluginManager = koinInject()
            val settingsDataStore: SettingsDataStore = koinInject()
            val navController = rememberNavController()
            val context = LocalContext.current

            val lastPlugin by pluginManager.getSelectedPluginFlow().collectAsState()
            val pluginList = pluginManager.getAllPlugins()
            var showFab by remember { mutableStateOf(false) }
            var showPluginDialog by remember { mutableStateOf(false) }
            val isDarkMode by settingsDataStore.darkThemeEnabled.collectAsState(true)
            val isMaterialThemeEnabled by settingsDataStore.materialThemeEnabled.collectAsState(false)

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect {
                    showFab = it.destination.route == "home_screen_root"
                }
            }

            LaunchedEffect(Unit) {
                if (pluginList.isEmpty()) {
                    Toast.makeText(context, "Downloading Main Plugins...", Toast.LENGTH_SHORT).show()
                    downloadMainPlugins(viewModel, context)
                }
            }

            RecozTheme(
                darkTheme = isDarkMode,
                dynamicColor = isMaterialThemeEnabled
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(navController)
                    },
                    floatingActionButton = {
                        if (showFab) {
                            ExtendedFloatingActionButton(
                                text = { Text(text = lastPlugin?.name ?: "None") },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_filter_list_24),
                                        contentDescription = null,
                                    )
                                },
                                onClick = { showPluginDialog = !showPluginDialog },
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.focusScale()
                            )
                        }
                    }
                ) { innerPadding ->
                    viewModel.checkAppUpdate(context)

                    AppUpdateDialog(viewModel) {
                        viewModel.showUpdateDialog = false
                    }

                    if (showPluginDialog) {
                        PluginDialog {
                            showPluginDialog = false
                        }
                    }

                    Box(modifier = Modifier.padding(innerPadding)) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun downloadMainPlugins(viewModel: MainVM, context: Context) {
        val url = "https://raw.githubusercontent.com/Recoo31/RoxioPlugins/main/version.json"
        viewModel.download(url, context)
    }
}