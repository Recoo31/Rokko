package kurd.reco.recoz.view.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kurd.reco.api.Resource
import kurd.reco.api.model.HomeItemModel
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

//    val pagerList by lazy {
//        pluginManager.getSelectedPlugin().pagerList
//    }

    init {
        getMovies()
    }

    fun getMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                moviesList.emit(Resource.Loading)
                val response = pluginManager.getSelectedPlugin().getHomeScreenItems()
                moviesList.emit(response)
            } catch (t: Throwable) {
                t.printStackTrace()
                moviesList.emit(Resource.Failure(t.localizedMessage ?: t.message ?: "Unknown Error"))
            }
        }
    }

    fun getPagerList(): List<HomeItemModel>? {
        return pluginManager.getSelectedPlugin().pagerList
    }

    fun getUrl(id: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clickedItem.emit(Resource.Loading)
                val response = pluginManager.getSelectedPlugin().getUrl(id)
                clickedItem.emit(response)
                AppLog.i(TAG, "getUrl: $response")
            } catch (t: Throwable) {
                t.printStackTrace()
                clickedItem.emit(Resource.Failure(t.localizedMessage ?: t.message ?: "Unknown Error"))
            }
        }
    }
}