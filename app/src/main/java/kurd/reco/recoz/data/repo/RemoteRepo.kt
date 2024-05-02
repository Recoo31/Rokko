package kurd.reco.recoz.data.repo




interface RemoteRepo {
    suspend fun getHomeScreenItems()
}