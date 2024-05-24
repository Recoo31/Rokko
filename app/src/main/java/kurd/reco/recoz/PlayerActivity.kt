package kurd.reco.recoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.videoscreen.VideoPlayerCompose
import org.koin.compose.koinInject


class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecozTheme {
                Surface {
                    val viewModel: MainVM = koinInject()
                    viewModel.playDataModel?.let { VideoPlayerCompose(it) }
                }
            }
        }
    }
}
