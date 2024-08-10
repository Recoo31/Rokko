package kurd.reco.api.model

data class DetailScreenModel(
    val id: Any,
    val title: String,
    val description: String?,
    val image: String,
    val backImage: String?,
    val isSeries: Boolean
)