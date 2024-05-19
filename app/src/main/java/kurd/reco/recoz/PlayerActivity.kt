package kurd.reco.recoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import kurd.reco.api.model.DrmDataModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.ui.theme.RecozTheme
import kurd.reco.recoz.view.videoscreen.VideoPlayerCompose


class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecozTheme {
                Surface {
                    val url = intent.getStringExtra("url")
                    val title = intent.getStringExtra("title")
                    val licenseUrl = intent.getStringExtra("licenseUrl")
                    val headers = intent.getSerializableExtra("headers") as? Map<String, String>


                    val playData = PlayDataModel(
                        url = url ?: "",
                        title = title ?: "",
                        drm = if (licenseUrl != null) {
                            DrmDataModel(licenseUrl, headers)
                        } else {
                            null
                        }
                    )

                    VideoPlayerCompose(playData)
                }
            }
        }
    }
}
