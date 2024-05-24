package kurd.reco.recoz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kurd.reco.api.model.PlayDataModel

class MainVM: ViewModel() {
    var actionButton by mutableStateOf(false)
    var playDataModel: PlayDataModel? = null
}