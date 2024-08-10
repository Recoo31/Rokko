package kurd.reco.api.model

data class HomeScreenModel(
    val title: String,
    val contents: List<HomeItemModel>
)

data class HomeItemModel(
    val id: Any,
    val title: String?,
    val poster: String,
    val isSeries: Boolean,
    val isLiveTv: Boolean
)