package kurd.reco.api

import kurd.reco.api.model.DetailScreenModel
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.api.model.SeriesDataModel

interface RemoteRepo {
    suspend fun getHomeScreenItems(): Resource<List<HomeScreenModel>>
    suspend fun getDetailScreenItems(id: Any, isSeries: Boolean): Resource<DetailScreenModel>
    suspend fun getUrl(id: Any): Resource<PlayDataModel>
    var seriesList: List<SeriesDataModel>?
}