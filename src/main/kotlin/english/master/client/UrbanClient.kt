package english.master.client

import com.fasterxml.jackson.annotation.JsonProperty


class UrbanClient : AbstractClient() {
    private val apiKey = "e371c7e3fbmsh8a5603b72d03c15p1e7c35jsnae8f16415a68"
    private val apiHost = "mashape-community-urban-dictionary.p.rapidapi.com"

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
