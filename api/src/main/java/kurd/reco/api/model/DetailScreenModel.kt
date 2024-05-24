package kurd.reco.api.model

data class DetailScreenModel(
    val title: String,
    val description: String?,
    val id: Any,
    val image: String,
    val backImage: String?,
    val isSeries: Boolean
)