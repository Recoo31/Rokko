package kurd.reco.tmdbapi

import kurd.reco.recoz.Resource
import kurd.reco.recoz.app
import kurd.reco.recoz.data.model.DetailScreenModel
import kurd.reco.recoz.data.model.HomeItemModel
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.data.model.PlayDataModel
import kurd.reco.recoz.data.model.SeriesDataModel
import kurd.reco.recoz.data.model.SeriesItem
import kurd.reco.recoz.data.repo.RemoteRepo

data class TmdbDataClass(
    val page: Int,
    val results: List<TmdbMovieDataClass>,
    val total_pages: Int,
    val total_results: Int
)

data class TmdbMovieDataClass(
    val poster_path: String,
    val id: Int
)

class Tmdb() : RemoteRepo {
    override var seriesList: List<SeriesDataModel>? = null

    val mainUrl = "https://api.themoviedb.org/3"
    val apiKey = "86ab4a1729081b6557bf1d959a3d4ec9"

    val mainPage = mapOf(
        "/movie/popular?api_key=$apiKey" to "Popular Movies",
        "/tv/popular?api_key=$apiKey" to "Popular Series",
        "/movie/top_rated?api_key=$apiKey" to "Top Rated Movies",
        "/tv/top_rated?api_key=$apiKey" to "Top Rated Series",
        "/movie/upcoming?api_key=$apiKey" to "Upcoming Movies",
        "/tv/on_the_air?api_key=$apiKey" to "On The Air Series",
        "/discover/movie?api_key=$apiKey" to "Discover Movies",
        "/discover/tv?api_key=$apiKey" to "Discover Series"
    )

    override suspend fun getHomeScreenItems(): Resource<List<HomeScreenModel>> {
        val movieList = mutableListOf<HomeScreenModel>()
        try {
            for ((url, title) in mainPage) {
                val response = app.get(url = mainUrl + url).parsed<TmdbDataClass>()
                val contents = mutableListOf<HomeItemModel>()
                val isSeries = url.contains("tv")
                for (item in response.results) {
                    contents.add(HomeItemModel(item.id, "https://image.tmdb.org/t/p/w500"+item.poster_path, isSeries))
                }
                movieList.add(HomeScreenModel(title, contents))
            }

            return Resource.Success(movieList)
        } catch (e: Throwable) {
            return Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
        }
    }

    override suspend fun getDetailScreenItems(id: Any, isSeries: Boolean): Resource<DetailScreenModel> {
        try {
            if (isSeries) {
                val url = "https://api.themoviedb.org/3/tv/$id?language=en-US&api_key=86ab4a1729081b6557bf1d959a3d4ec9"
                val response = app.get(url = url).parsed<DetailSeriesModel>()

                val title = response.original_name
                val description = response.overview
                val imdbID = response.id
                val image = "https://image.tmdb.org/t/p/w500"+response.poster_path
                val backImage = "https://image.tmdb.org/t/p/w780"+response.backdrop_path
                val filteredSeasonsList = response.seasons.filter { it.name != "Specials" }

                parseSeasons(filteredSeasonsList, imdbID)

                return Resource.Success(DetailScreenModel(title, description, imdbID, image, backImage, true))
            } else {
                val url = "https://api.themoviedb.org/3/movie/$id?language=en-US&api_key=86ab4a1729081b6557bf1d959a3d4ec9"
                val response = app.get(url = url).parsed<DetailModel>()

                val title = response.title
                val description = response.overview
                val imdbID = response.imdb_id
                val image = "https://image.tmdb.org/t/p/w500"+response.poster_path
                val backImage = "https://image.tmdb.org/t/p/w780"+response.backdrop_path

                return Resource.Success(DetailScreenModel(title, description, imdbID, image, backImage, false))
            }

        } catch (e: Throwable) {
            return Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
        }
    }

    override suspend fun getUrl(id: Any): Resource<PlayDataModel> {
        TODO("Not yet implemented")
    }

    private suspend fun parseSeasons(seasons: List<SeasonModel>, imdbID: Int) {
        val seriesList = mutableListOf<SeriesDataModel>()

        for ((index, season) in seasons.withIndex()) {
            val episodes = getEpisodes(imdbID.toString(), index + 1)
            val seriesDataModel = SeriesDataModel(
                id = season.id,
                name = season.name,
                episodes = episodes.map {
                    SeriesItem(
                        it.id,
                        it.name,
                        "https://image.tmdb.org/t/p/w500"+it.still_path,
                        it.overview)
                }
            )
            seriesList.add(seriesDataModel)
        }
        this.seriesList = seriesList
    }


    private suspend fun getEpisodes(seasonNumber: String, episodeNumber: Int): List<EpisodesModel> {
        val url = "https://api.themoviedb.org/3/tv/$seasonNumber/season/$episodeNumber?api_key=86ab4a1729081b6557bf1d959a3d4ec9"
        val response = app.get(url = url).parsed<Episodes>()
        return response.episodes
    }

}