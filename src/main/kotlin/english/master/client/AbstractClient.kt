package english.master.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant

open class AbstractClient {
    private val mapper = ObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val httpClient = HttpClient.newHttpClient()


    fun <T> get(url: String, clazz: Class<T>): T {
        println("${Instant.now()} GET $url")

        val response = httpClient.send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body()

        println("${Instant.now()} GET finished")

        return mapper.readValue(response, clazz)
    }

    // todo
    fun <T> getWithHeaders(url: String, clazz: Class<T>): T {
        println("${Instant.now()} GET $url")

        val response = httpClient.send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .headers(
                    "X-RapidAPI-Key",
                    "e371c7e3fbmsh8a5603b72d03c15p1e7c35jsnae8f16415a68",
                    "X-RapidAPI-Host",
                    "mashape-community-urban-dictionary.p.rapidapi.com"
                )
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body()

        println("${Instant.now()} GET finished")

        return mapper.readValue(response, clazz)
    }

    fun <T> post(url: String, body: Any, clazz: Class<T>): T? {
        println("${Instant.now()} GET $url")

        val response = httpClient.send(
            HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body()

        println("${Instant.now()} GET finished")

        return mapper.readValue(response, clazz)
    }

    fun <T> getSilently(url: String, clazz: Class<T>): T? {
        return try {
            return get(url, clazz)
        } catch (ex: Exception) {
            null
        }
    }

    fun encodeTabs(str: String): String {
        return str.replace(" ", "%20")
    }
}