package kurd.reco.api

import kurd.reco.api.model.DetailScreenModel
import kurd.reco.api.model.HomeScreenModel
import kurd.reco.api.model.PlayDataModel
import kurd.reco.api.model.SearchModel
import java.text.SimpleDateFormat
import java.util.Date

class ProviderTester(private val provider: RemoteRepo) {
    private val RESET = "\u001B[0m"
    private val BLACK = "\u001B[30m"
    private val RED = "\u001B[31m"
    private val GREEN = "\u001B[32m"
    private val YELLOW = "\u001B[33m"
    private val BLUE = "\u001B[34m"
    private val PURPLE = "\u001B[35m"
    private val CYAN = "\u001B[36m"
    private val WHITE = "\u001B[37m"

    private fun log(message: String, color: String = RESET) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        println("$color[$timestamp] $message$RESET")
    }

    suspend fun testSearch(query: String, verbose: Boolean = false): List<SearchModel> {
        log("\n\n========== testSearch ==========\nStarting search with query: \"$query\"\n", BLUE)
        val responses = provider.search(query)
        log("Response count: ${responses.size}, Query: \"$query\"\n", GREEN)
        if (verbose) {
            log("Responses:\n${responses.joinToString("\n")}\n", CYAN)
        } else {
            log("Responses:\n${responses.map { it.title to it.id }.joinToString("\n")}\n", CYAN)
        }
        return responses
    }

    suspend fun testGetHomeScreenItems(verbose: Boolean = false): Resource<List<HomeScreenModel>> {
        log("\n\n========== testGetHomeScreenItems ==========\nFetching home screen items\n", BLUE)
        val response = provider.getHomeScreenItems()
        log("Response: $response\n", GREEN)
        if (verbose && response is Resource.Success) {
            response.value.forEach { homeScreenModel ->
                log("Home Screen: ${homeScreenModel.title}, Items: ${homeScreenModel.contents.size}\n", CYAN)
            }
        }
        return response
    }

    suspend fun testGetDetailScreenItems(id: Any, isSeries: Boolean): Resource<DetailScreenModel> {
        log("\n\n========== testGetDetailScreenItems ==========\nFetching detail screen items for ID: $id, isSeries: $isSeries\n", BLUE)
        val response = provider.getDetailScreenItems(id, isSeries)
        log("Response: $response\n", GREEN)
        return response
    }

    suspend fun testGetUrl(id: Any): Resource<PlayDataModel> {
        log("\n\n========== testGetUrl ==========\nFetching URL for ID: $id\n", BLUE)
        val response = provider.getUrl(id)
        log("Response: $response\n", GREEN)
        return response
    }

    /**
     * Comprehensive test involving all the other tests
     */
    suspend fun testAll(query: String? = null, verbose: Boolean = false) {
        if (query != null) {
            val searchResults = testSearch(query, verbose)
            if (searchResults.isNotEmpty()) {
                val firstResult = searchResults.first()
                log("Testing get detail screen items for first result with ID: ${firstResult.id}\n", YELLOW)

                testGetDetailScreenItems(firstResult.id, firstResult.isSeries)
                log("Testing get URL for first result with ID: ${firstResult.id}\n", YELLOW)

                testGetUrl(firstResult.id)
            }
        } else {
            val items = testGetHomeScreenItems(verbose = true) as? Resource.Success
            val item = items!!.value.first { homeItem ->
                homeItem.contents.any { !it.isLiveTv }
            }.contents[0]

            log("Testing get detail screen items for first result with ID: ${item.id}\n", YELLOW)
            testGetDetailScreenItems(item.id, item.isSeries)

            log("Testing search for first result with ID: ${item.id}\n", PURPLE)
            testSearch(item.id.toString(), verbose)

            log("Testing get URL for first result with ID: ${item.id}\n", YELLOW)
            testGetUrl(item.id)

        }

        log("\nTesting home screen items\n", YELLOW)
        log("\n========== Done ==========\n", PURPLE)
    }
}
