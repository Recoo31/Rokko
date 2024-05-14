package kurd.reco.recoz.view.homescreen


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurd.reco.api.Resource
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.recoz.plugin.PluginManager

class HomeScreenVM(private val pluginManager: PluginManager): ViewModel() {
    var moviesList: MutableStateFlow<Resource<List<HomeScreenModel>>> = MutableStateFlow(Resource.Loading)
        private set

    var query by mutableStateOf("")

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

    fun search() {

    }
}