package english.master.action.common.internal

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper
import english.master.util.CacheService

class SaveChosenDescriptionAction: Action() {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        val card = CacheService.getCard(update.userId)
        CacheService.putCard(update.userId, card.copy(description = descriptionOfChosenCards(update)))
        cardRepo.update(card.copy(description = descriptionOfChosenCards(update)))
        return SilentMessage()
    }

    private fun descriptionOfChosenCards(update: UpdateWrapper): String {
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        var description = ""
        chosenDefinitions.forEachIndexed { index, meaning ->
            run {
                description += "\n\n${index + 1}) ${format(meaning.definition)}\n<i>[Example]:</i> ${format(meaning.example)}"
            }
        }
        return description
    }

    private fun format(str: String): String {
        return str.replace("[", "")
            .replace("]", "")
    }
}