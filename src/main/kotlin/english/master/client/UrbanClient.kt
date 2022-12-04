package english.master.client

import com.fasterxml.jackson.annotation.JsonProperty
import java.lang.System.getenv


class UrbanClient : AbstractClient() {
    private val apiKey = getenv("URBAN_API_KEY")
    private val apiHost = getenv("URBAN_API_HOST")

    fun getWordDefinitions(word: String): List<WordDefinition> {
        return get(
            "https://api.urbandictionary.com/v0/define?term=${encodeTabs(word)}",
            WordDefinitionList::class.java,
            mapOf(
                "X-RapidAPI-Key" to apiKey,
                "X-RapidAPI-Host" to apiHost
            )
        ).list
    }
}

class WordDefinition(
    @JsonProperty("word") val word: String,
    @JsonProperty("definition") val definition: String,
    @JsonProperty("example") val example: String,
)

class WordDefinitionList(
    @JsonProperty("list") val list: List<WordDefinition>,
)
