package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class RequestWordAction(next: Action? = null) : Action(next) {

    override fun process(update: UpdateWrapper): SendMessage {

        return SendMessage
            .builder()
            .chatId(update.chatId)
            .text("Yay, it's time for new words! Please provide the word \uD83E\uDD13")
            .build()
    }
}
