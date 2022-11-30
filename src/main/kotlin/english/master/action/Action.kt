package english.master.action

import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

abstract class Action(
    var next: Action? = null,
    var previous: Action? = null,
    var repeat: Boolean = false,
    var waitForResponse: Boolean = true
) {

    abstract fun process(update: UpdateWrapper): Any


    fun sendMessage(update: UpdateWrapper, msg: String): SendMessage {
        val messageBuilder = SendMessage
            .builder()
            .chatId(update.chatId)
            .text(msg)
        if (msg.contains("<")) { // todo
            messageBuilder
                .parseMode("HTML")
                .build()
        }
        return messageBuilder.build()
    }

    fun next(next: Action?) {
        this.next = next
    }
}