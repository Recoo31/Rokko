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
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
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
            val navController = rememberNavController()


            RecozTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(navController)
                    },
                ) { innerPadding ->
//                    val context = LocalContext.current
//
//                    val urls = listOf(
//                        "BitMovin" to "https://bitmovin-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
//                        "AfCdn" to "https://assets.afcdn.com/video49/20210722/v_645516.m3u8",
//                        "MuxDev" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
//                    )
//                    val intent = Intent(context, PlayerActivity::class.java)
//
//                    viewModel.playDataModel = PlayDataModel(urls, null, null, null)
//
//                    context.startActivity(intent)

                    Box(modifier = Modifier.padding(innerPadding)) {
                        DestinationsNavHost(navGraph = NavGraphs.root, navController = navController)
                    }
                }
            }
        }
    }
}