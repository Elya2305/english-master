package english.master.action.view_cards

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class RequestCardNumberAction(next: Action? = null) : Action(next) {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        val totalCards = cardRepo.count(update.chatId)

        return SendMessage
            .builder()
            .chatId(update.chatId)
            .text("Provide number of cards you want to review (max: $totalCards)")
            .build()
    }
}