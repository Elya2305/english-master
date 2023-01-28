package english.master.action.translate

import english.master.action.Action
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class RequestWordAction : Action() {

    override fun process(update: UpdateWrapper): SendMessage {

        return SendMessage
            .builder()
            .chatId(update.chatId)
            .text("\uD83E\uDD13 Please provide the word")
            .build()
    }
}
