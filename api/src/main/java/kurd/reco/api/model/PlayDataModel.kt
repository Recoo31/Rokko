package kurd.reco.api.model

data class PlayDataModel(
    val url: String,
    val title: String?,
    val drm: DrmDataModel?,
    val subtitles: List<String>?
)

data class DrmDataModel(
    val licenseUrl: String,
    val headers: Map<String, String>?
)