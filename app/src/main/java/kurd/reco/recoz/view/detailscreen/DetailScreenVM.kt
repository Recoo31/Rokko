package kurd.reco.recoz.view.detailscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurd.reco.api.Resource
import kurd.reco.api.model.DetailScreenModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.recoz.plugin.PluginManager

class DetailScreenVM(private val pluginManager: PluginManager) : ViewModel() {
    private val _item: MutableStateFlow<Resource<DetailScreenModel>> = MutableStateFlow(Resource.Loading)
    val item: StateFlow<Resource<DetailScreenModel>> = _item

    private val _clickedItem: MutableStateFlow<Resource<PlayDataModel>> = MutableStateFlow(Resource.Loading)
    val clickedItem: StateFlow<Resource<PlayDataModel>> = _clickedItem

    val seriesList by lazy {
        pluginManager.getSelectedPlugin().seriesList
    }

    private val TAG = "DetailScreenVM"


    fun getMovie(id: Any, isSeries: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "getMovie: $id")
                val response = pluginManager.getSelectedPlugin().getDetailScreenItems(id, isSeries)
                _item.value = response
           } catch (e: Exception) {
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
            } catch (e: Exception) {
                e.printStackTrace()
                _clickedItem.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }
}