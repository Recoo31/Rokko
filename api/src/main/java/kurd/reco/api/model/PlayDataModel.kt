package kurd.reco.api.model

data class PlayDataModel(
    val url: String,
    val title: String?,
    val drm: DrmDataModel?
)

data class DrmDataModel(
    val licenseUrl: String,
    val headers: Map<String, String>?
)