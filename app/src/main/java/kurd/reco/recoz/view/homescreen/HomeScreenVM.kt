package kurd.reco.recoz.view.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kurd.reco.api.Resource
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.settings.logs.AppLog

class HomeScreenVM(private val pluginManager: PluginManager): ViewModel() {
    var moviesList: MutableStateFlow<Resource<List<HomeScreenModel>>> = MutableStateFlow(Resource.Loading)
        private set

    private val _clickedItem: MutableStateFlow<Resource<PlayDataModel>> = MutableStateFlow(Resource.Loading)
    val clickedItem get() = _clickedItem

    private val TAG = "HomeScreenVM"

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().getHomeScreenItems()
                moviesList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                moviesList.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }

    fun getUrl(id: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().getUrl(id)
                _clickedItem.value = response
                AppLog.i(TAG, "getUrl: $response")
            } catch (e: Exception) {
                e.printStackTrace()
                _clickedItem.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }
}