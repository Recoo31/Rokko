package kurd.reco.api.model

data class SeriesDataModel(
    val id: Any,
    val name: String,
    val episodes: List<SeriesItem>
)

data class SeriesItem(
    val id: Any,
    val title: String,
    val poster: String,
    val description: String?,
)