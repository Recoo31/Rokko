package kurd.reco.blutv

class CategoryModel(
    val title: String,
    val type: String,
    val contents: List<CategoryContents>
)

data class CategoryContents(
    val channel: String,
    val contentType: String,
    val description: String,
    val id: String,
    val images: List<ImageModel>,
    val imdbRating: Double,
    val madeYear: Int,
    val path: String,
    val title: String,
    val url: String,
)

data class ImageModel(
    val contentType: String?,
    val id: String,
    val name: String,
    val type: String
)