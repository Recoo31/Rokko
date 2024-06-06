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

    var clickedItem: MutableStateFlow<Resource<PlayDataModel>> = MutableStateFlow(Resource.Loading)
        private set

    private val TAG = "HomeScreenVM"

    val pagerList by lazy {
        pluginManager.getSelectedPlugin().pagerList
    }

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().getHomeScreenItems()
                moviesList.value = response
            } catch (t: Throwable) {
                t.printStackTrace()
                moviesList.value = Resource.Failure(t.localizedMessage ?: t.message ?: "Unknown Error")
            }
        }
    }

    fun getUrl(id: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().getUrl(id)
                clickedItem.value = response
                AppLog.i(TAG, "getUrl: $response")
            } catch (e: Throwable) {
                e.printStackTrace()
                clickedItem.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }
}