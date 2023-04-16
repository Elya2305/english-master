package english.master.action.list_cards

import english.master.action.Action
import english.master.db.CardRecord
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper

class SendListOfCards : Action(waitForResponse = false) {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        val allCards = cardRepo.allByCreatedAt(update.userId)

        return sendMessage(update, formatCards(allCards))
    }

    private fun formatCards(cards: List<CardRecord>): String {
        var cardsString = ""
        cards.forEachIndexed { index, card ->
            cardsString += "\n ${index + 1} ${card.word}"
        }
        return cardsString
    }
}