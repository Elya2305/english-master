package english.master.util

import english.master.processors.*

object Commands {
    private fun commandProcessors() = mapOf(
        "/look_up" to Command("Look up word definition", LookUpWordProcessor()),
        "/translate" to Command("Translate", TranslateWordProcessor()),
        "/new_card" to Command("Create a new card", NewCardProcessor()),
        "/random_cards" to Command("Show random cards", ShowCardsProcessor()),
        "/list_cards" to Command("Choose card to do some actions [delete]", ListCardsProcessor()),
        "/start" to Command("Start", StartProcessor(), visibleInMenu = false),
        "/menu" to Command("Menu", MenuProcessor(), visibleInMenu = false),
    )

    fun getProcessor(command: String): FlowProcessor {
        return commandProcessors()[command]?.processor ?: throw RuntimeException("Can't find processor for $command")
    }

    fun getCommandDescriptionMapping(): List<Pair<String, String>> {
        return commandProcessors()
            .filter { it.value.visibleInMenu }
            .map { Pair(it.key, it.value.description) }
    }
}

data class Command(
    val description: String,
    val processor: FlowProcessor,
    val visibleInMenu: Boolean = true,
)
