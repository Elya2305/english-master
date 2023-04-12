package english.master.domain

import english.master.client.WordDefinition

data class Definitions(
    val requestedWord: String,
    val definitions: List<WordDefinition>,
)