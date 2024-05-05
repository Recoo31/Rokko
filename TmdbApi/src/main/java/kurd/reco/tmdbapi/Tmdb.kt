package kurd.reco.tmdbapi

import kurd.reco.recoz.Resource
import kurd.reco.recoz.app
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.data.repo.RemoteRepo

data class TmdbDataClass(
    val page: Int,
    val results: List<TmdbMovieDataClass>,
    val total_pages: Int,
    val total_results: Int
)

data class TmdbMovieDataClass(
    val poster_path: String,
    val title: String,
    val id: Int
)

class Tmdb : RemoteRepo {
    override suspend fun getHomeScreenItems(): Resource<List<HomeScreenModel>> {
        val movieList = mutableListOf<HomeScreenModel>()
        try {
            val url = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc&api_key=86ab4a1729081b6557bf1d959a3d4ec9"
            val response = app.get(url = url).parsed<TmdbDataClass>()

            response.results.forEach {
                movieList.add(HomeScreenModel(it.id, "https://image.tmdb.org/t/p/w500"+it.poster_path))
            }
            return Resource.Success(movieList)
        } catch (e: Exception) {
            return Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
        }
    }

}