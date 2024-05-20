package kurd.reco.api.model

data class PlayDataModel(
    val url: String,
    val title: String?,
    val drm: DrmDataModel?,
    val subtitles: List<SubtitleDataModel>?
)

data class DrmDataModel(
    val licenseUrl: String,
    val headers: Map<String, String>?
)

data class SubtitleDataModel(
    val url: String,
    val language: String,
    val id: Any?
)