package english.master.client

import com.fasterxml.jackson.annotation.JsonProperty

class TranslationClient : AbstractClient() {
    private val url = "http://localhost:3000/api/v1/en/uk"

    fun translate(query: String): String {
        return get("${url}/${encode(query)}", Translation::class.java)
            .translation
    }
}

data class Translation(
    @JsonProperty("translation") val translation: String
)