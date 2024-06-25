package kurd.reco.recoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.settings.SettingsDataStore
import kurd.reco.recoz.view.videoscreen.VideoPlayerCompose
import kurd.reco.recoz.view.videoscreen.openVideoWithSelectedPlayer
import org.koin.compose.koinInject


class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecozTheme {
                Surface {
                    val viewModel: MainVM = koinInject()

                    viewModel.playDataModel?.let {
                        val settingsDataStore: SettingsDataStore = koinInject()
                        val context = LocalContext.current

                        val externalPlayer by settingsDataStore.externalPlayer.collectAsState("")
                        if (externalPlayer.isNotEmpty() && it.drm == null) {
                            openVideoWithSelectedPlayer(
                                context = context,
                                videoUri = it.urls[0].second,
                                playerPackageName = externalPlayer
                            )
                            finish()
                        } else VideoPlayerCompose(it)
                    }
                }
            }
        }
    }
}
