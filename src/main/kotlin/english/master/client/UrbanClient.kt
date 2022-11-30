package english.master.client

import com.fasterxml.jackson.annotation.JsonProperty


class UrbanClient : AbstractClient() {

    fun getWordDefinitions(word: String): List<WordDefinition> {
        return getWithHeaders(
            "https://api.urbandictionary.com/v0/define?term=${encodeTabs(word)}",
            WordDefinitionList::class.java
        ).list
    }
}

// todo refactor

class WordDefinition(
    @JsonProperty("word") val word: String,
    @JsonProperty("definition") val definition: String,
    @JsonProperty("example") val example: String,
)
class WordDefinitionList(
    @JsonProperty("list") val list: List<WordDefinition>,
)
