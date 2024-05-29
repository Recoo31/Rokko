package kurd.reco.recoz.view.detailscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kurd.reco.api.Resource
import kurd.reco.api.model.DetailScreenModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.plugin.PluginManager
import kurd.reco.recoz.view.settings.logs.AppLog

class DetailScreenVM(private val pluginManager: PluginManager) : ViewModel() {
    private val _item: MutableStateFlow<Resource<DetailScreenModel>> = MutableStateFlow(Resource.Loading)
    val item get() = _item

    private val _clickedItem: MutableStateFlow<Resource<PlayDataModel>> = MutableStateFlow(Resource.Loading)
    val clickedItem get() = _clickedItem

    val seriesList by lazy {
        pluginManager.getSelectedPlugin().seriesList
    }

    private val TAG = "DetailScreenVM"


    fun getMovie(id: Any, isSeries: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppLog.d(TAG, "getMovie: ID: $id | IsSeries: $isSeries")
                val response = pluginManager.getSelectedPlugin().getDetailScreenItems(id, isSeries)
                _item.value = response
           } catch (e: Throwable) {
                e.printStackTrace()
                _item.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }

    fun getUrl(id: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().getUrl(id)
                _clickedItem.value = response
                AppLog.i(TAG, "getUrl: $response")
            } catch (e: Throwable) {
                e.printStackTrace()
                _clickedItem.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }
}