package kurd.reco.recoz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainVM: ViewModel() {
    var actionButton by mutableStateOf(false)
}