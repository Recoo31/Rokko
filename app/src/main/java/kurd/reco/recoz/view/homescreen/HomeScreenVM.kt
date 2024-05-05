package kurd.reco.recoz.view.homescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.plugin.PluginManager

class HomeScreenVM(val pluginManager: PluginManager): ViewModel() {
    private val _moviesList: MutableStateFlow<Resource<List<HomeScreenModel>>> = MutableStateFlow(Resource.Loading())
    val moviesList: StateFlow<Resource<List<HomeScreenModel>>> = _moviesList

    fun getMovies() {
        viewModelScope.launch {
            val plugin = pluginManager.getSelectedPlugin()
            plugin?.let {
                val response = it.remoteRepo.getHomeScreenItems()
                _moviesList.value = response
            }
        }
    }
}