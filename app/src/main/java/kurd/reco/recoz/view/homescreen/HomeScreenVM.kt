package kurd.reco.recoz.view.homescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.plugin.PluginManager

class HomeScreenVM(private val pluginManager: PluginManager): ViewModel() {
    private val _moviesList: MutableStateFlow<Resource<List<HomeScreenModel>>> = MutableStateFlow(Resource.Loading)
    val moviesList: StateFlow<Resource<List<HomeScreenModel>>> = _moviesList

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = pluginManager.getSelectedPlugin().getHomeScreenItems()
                _moviesList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _moviesList.value = Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
            }
        }
    }
}