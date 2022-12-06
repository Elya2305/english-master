package english.master.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.IOUtils
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpResponse.BodyHandlers.ofInputStream
import java.net.http.HttpResponse.BodyHandlers.ofString
import java.time.Instant

open class AbstractClient {
    private val mapper = ObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val httpClient = HttpClient.newHttpClient()


    fun <T> get(url: String, clazz: Class<T>, headers: Map<String, String> = emptyMap()): T {
        println("${Instant.now()} GET $url")
        val builder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
        headers.entries.forEach { builder.header(it.key, it.value) }

        val response = httpClient.send(builder.build(), ofString()).body()
        println("${Instant.now()} GET finished")
        return mapper.readValue(response, clazz)
    }

    fun <T> post(url: String, body: Any, clazz: Class<T>): T? {
        println("${Instant.now()} GET $url")
        val builder = HttpRequest.newBuilder()
            .POST(ofString(mapper.writeValueAsString(body)))
            .uri(URI.create(url))
            .header("Content-Type", "application/json")

        val response = httpClient.send(builder.build(), ofString()).body()
        println("${Instant.now()} GET finished")
        return mapper.readValue(response, clazz)
    }

    fun download(url: String): ByteArray {
        println("${Instant.now()} GET $url")
        val builder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))

        val response = httpClient.send(builder.build(), ofInputStream()).body()
        println("${Instant.now()} GET finished")
        return IOUtils.toByteArray(response)
    }

    fun downloadSilently(url: String): ByteArray? {
        return try {
            return download(url)
        } catch (ex: Exception) {
            null
        }
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