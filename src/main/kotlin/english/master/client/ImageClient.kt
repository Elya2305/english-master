package english.master.client

import com.fasterxml.jackson.annotation.JsonProperty

class GoogleImageClient : AbstractClient() {
    private val apiKey = "wX1SlTpqfXDO5YQtBZnd_GUrSvddNjqKQrc5bxKV7To"

    fun getImages(word: String): ImageList {
        return getSilently(
            "https://api.unsplash.com/search/photos?client_id=$apiKey&query=${encodeTabs(word)}",
            ImageList::class.java
        )!!
    }

    fun downloadImage(url: String): ByteArray? {
        return downloadSilently(url.split("?")[0] + "?fit=crop&w=300&h=300&dpr=10")
    }
}

data class Image(
    @JsonProperty("urls") val urls: Urls
)

data class ImageList(
    @JsonProperty("results") val results: List<Image>
)

data class Urls(
    @JsonProperty("raw") val raw: String,
    @JsonProperty("small") val small: String,
    @JsonProperty("regular") val regular: String,
)