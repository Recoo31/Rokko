package kurd.reco.blutv


data class LiveItemModel(
    val media: LiveMediaItems
)

data class LiveMediaItems(
    val title: String,
    val source: String,
    val poster: String,
    val drm: Boolean,
)