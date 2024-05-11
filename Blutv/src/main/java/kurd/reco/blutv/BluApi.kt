package kurd.reco.blutv

import android.util.Log
import kurd.reco.recoz.Resource
import kurd.reco.recoz.app
import kurd.reco.recoz.data.model.DetailScreenModel
import kurd.reco.recoz.data.model.DrmDataModel
import kurd.reco.recoz.data.model.HomeItemModel
import kurd.reco.recoz.data.model.HomeScreenModel
import kurd.reco.recoz.data.model.PlayDataModel
import kurd.reco.recoz.data.model.SeriesDataModel
import kurd.reco.recoz.data.model.SeriesItem
import kurd.reco.recoz.data.repo.RemoteRepo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


fun parseImage(
    images: List<ImageModel>,
    imageType: String,
    width: Int = 0,
    height: Int = 0
): String {
    val image = images.find { it.type == imageType } ?: images.firstOrNull()
    return "https://blutv-images.mncdn.com/q/t/i/bluv2/300/${width}x${height}/${image?.id}"
}


object ImageType {
    const val Gallery = "gallery"
    const val Logo = "logo"
    const val Portrait = "portrait"
    const val Landscape = "landscape"
}

private val TAG = "BluTv"

class BluApi : RemoteRepo {
    override var seriesList: List<SeriesDataModel>? = null

    override suspend fun getHomeScreenItems(): Resource<List<HomeScreenModel>> {
        val movieList = mutableListOf<HomeScreenModel>()

        try {
            val url =
                "https://adapter.blupoint.io/api/projects/5d2dc68a92f3636930ba6466/mobile/v2/get-category"

            val jsonData =
                "{\"contentTypes\":[\"SerieContainer\",\"MovieContainer\"],\"id\":\"60c34e66866ac31908b698fd\",\"package\":\"SVOD\",\"path\":\"/\",\"profileId\":\"5ff5763a84bdbccb076bc98e\",\"sort\":true}"

            val headers = mapOf(
                "Appauthorization" to "Basic 549a90e9fbead3126851951d:yO2Mo/pWtdtJPhrr+h4HvXI4jaYtDOQ+FCARtVsYzrKU0bK4lqycChAcuG0AvPqxAfgc9PhAJE65/e2MryBG3g==",
                "Appplatform" to "com.blu",
                "Appversion" to "62124567",
                "Authorization" to "Basic 5d36e6c40780020024687002:cE8vwiQrAULRGZ6ZqqXgtztqFgWRU7o6",
                "user-agent" to "okhttp/5.0.0-alpha.2",
                "Accept-Language" to "tr-TR",
                "Accept" to "application/json,application/json",
                "Accept-Charset" to "UTF-8",
                "Content-Type" to "application/json",
                "Connection" to "Keep-Alive",
            )

            val requestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())
            val response = app.post(url, headers = headers, requestBody = requestBody)
            val parsedResponse = response.parsed<Array<CategoryModel>>()

            if (response.isSuccessful) {
                Log.i(TAG, "getHomeScreenItems: isSuccessful")
                parsedResponse.forEach { item ->
                    val title = item.title
                    if (title == "Ads") return@forEach
                    val contents = mutableListOf<HomeItemModel>()
                    item.contents.forEach {
                        val image = parseImage(it.images, ImageType.Portrait, 323, 452)

                        val isSeries = it.contentType == "SerieContainer"
                        contents.add(HomeItemModel(it.url, image, isSeries))
                    }
                    movieList.add(HomeScreenModel(title, contents))
                }
                return Resource.Success(movieList)
            } else {
                return Resource.Failure(response.text)
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            return Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
        }
    }

    override suspend fun getDetailScreenItems(
        id: Any,
        isSeries: Boolean
    ): Resource<DetailScreenModel> {
        try {
            val url = "https://smarttv.blutv.com.tr/actions/content/getcontent"
            val headers = mapOf(
                "Appauthorization" to "Basic 549a90e9fbead3126851951d:yO2Mo/pWtdtJPhrr+h4HvXI4jaYtDOQ+FCARtVsYzrKU0bK4lqycChAcuG0AvPqxAfgc9PhAJE65/e2MryBG3g==",
                "Appplatform" to "com.blu",
                "Appversion" to "62124567",
                "Authorization" to "Basic 5d36e6c40780020024687002:cE8vwiQrAULRGZ6ZqqXgtztqFgWRU7o6",
                "user-agent" to "okhttp/5.0.0-alpha.2",
                "Accept-Language" to "tr-TR",
                "Accept" to "application/json,application/json",
                "Accept-Charset" to "UTF-8",
                "Content-Type" to "application/x-www-form-urlencoded",
                "Connection" to "Keep-Alive",
            )
            val body = mapOf(
                "url" to id.toString(),
                "platform" to "com.blu.smarttvv2",
                "mediatype" to "smil",
                "package" to "SVOD",
                "dvr" to "true"
            )
            val response = app.post(url, headers = headers, data = body).parsed<DetailModel>()
            val parsedResponse = response.data.model

            val title = parsedResponse.Title
            val description = parsedResponse.Description

            val _id = parsedResponse.Id
            val images =
                "https://blutv-images.mncdn.com/q/t/i/bluv2/300/323x452/" + parsedResponse.PosterImage
            val backImage =
                "https://blutv-images.mncdn.com/q/t/i/bluv2/300/1080x683/" + parsedResponse.Image

            if (isSeries) {
                parseSeries(parsedResponse.Seasons)
            }

            return Resource.Success(
                DetailScreenModel(
                    title,
                    description,
                    _id,
                    images,
                    backImage,
                    isSeries
                )
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            return Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
        }
    }

    override suspend fun getUrl(id: Any): Resource<PlayDataModel> {
        try {
            val url = "https://cors-proxy-jade-two.vercel.app/getlive"
            val jsonData = "{\"id\": \"$id\"}"
            val requestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

            val response = app.post(url, requestBody = requestBody, headers = mapOf("Content-Type" to "application/json")).parsed<LiveItemModel>()

            return Resource.Success(
                PlayDataModel(
                    url = response.media.source,
                    title = response.media.title,
                    drm = if (response.media.drm) DrmDataModel("https://wdvn.blutv.com/", null) else null
                )
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            return Resource.Failure(e.localizedMessage ?: e.message ?: "Unknown Error")
        }
    }

    private fun parseSeries(item: List<SeasonItem>?) {
        if (item == null) return

        val seriesList = mutableListOf<SeriesDataModel>()

        for (season in item) {
            val seriesDataModel = SeriesDataModel(
                id = season.Id,
                name = season.Title,
                episodes = season.Episodes.map {
                    SeriesItem(
                        id = it.Id,
                        title = it.Title,
                        poster = "https://blutv-images.mncdn.com/q/t/i/bluv2/300/600x400/" + it.Image,
                        description = it.Description
                    )
                }
            )
            seriesList.add(seriesDataModel)
        }

        this.seriesList = seriesList
    }

}