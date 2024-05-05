package kurd.reco.recoz.data.repo

import kurd.reco.recoz.Resource
import kurd.reco.recoz.data.model.HomeScreenModel


interface RemoteRepo {
    suspend fun getHomeScreenItems(): Resource<List<HomeScreenModel>>
}