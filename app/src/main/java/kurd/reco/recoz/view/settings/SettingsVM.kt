package kurd.reco.recoz.view.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsVM : ViewModel() {
    var showBottomSheet by mutableStateOf(false)
    var selectedItem by mutableStateOf("")
}
