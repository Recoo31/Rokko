package kurd.reco.recoz.data.model


data class HomeScreenModel(
    val title: String,
    val contents: List<HomeItemModel>
)

data class HomeItemModel(
    val id: Any,
    val poster: String,
    val isSeries: Boolean,
)