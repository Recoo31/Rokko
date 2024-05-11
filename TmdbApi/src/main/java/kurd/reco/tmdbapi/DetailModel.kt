package kurd.reco.tmdbapi

data class DetailModel(
    val title: String,
    val overview: String,
    val imdb_id: String?,
    val poster_path: String,
    val backdrop_path: String
)

data class DetailSeriesModel(
    val original_name: String,
    val poster_path: String,
    val backdrop_path: String,
    val overview: String,
    val seasons: List<SeasonModel>,
    val id: Int
)

data class SeasonModel(
    val id: Any,
    val name: String,
    val overview: String,
    val poster_path: String
)

data class Episodes(
    val episodes: List<EpisodesModel>
)

data class EpisodesModel(
    val id: Int,
    val name: String,
    val overview: String,
    val still_path: String
)