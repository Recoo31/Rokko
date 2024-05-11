package kurd.reco.recoz.data.repo

import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.DetailScreenModel
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.data.model.PlayDataModel
import kurd.reco.recoz.data.model.SeriesDataModel


interface RemoteRepo {
    suspend fun getHomeScreenItems(): Resource<List<HomeScreenModel>>
    suspend fun getDetailScreenItems(id: Any, isSeries: Boolean): Resource<DetailScreenModel>
    suspend fun getUrl(id: Any): Resource<PlayDataModel>
    var seriesList: List<SeriesDataModel>?
}