package kurd.reco.recoz.view.searchscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kurd.reco.api.model.SearchModel
import kurd.reco.recoz.plugin.PluginManager

class SearchScreenVM(private val pluginManager: PluginManager): ViewModel() {
    var searchList by mutableStateOf(emptyList<SearchModel>())


    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().search(query)
                searchList = response
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}