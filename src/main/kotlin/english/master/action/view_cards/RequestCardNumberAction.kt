package english.master.action.view_cards

import english.master.action.Action
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class RequestCardNumberAction(next: Action? = null) : Action(next) {

    override fun process(update: UpdateWrapper): Any {

        return SendMessage
            .builder()
            .chatId(update.chatId)
            .text("Provide number of cards you want to review")
            .build()
    }
}