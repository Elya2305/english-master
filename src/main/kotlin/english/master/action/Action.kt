package english.master.action

import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

abstract class Action(
    var next: Action? = null,
    var previous: Action? = null,
    var waitForResponse: Boolean = true,
    var nextToProcess: Active = Active.NEXT,
) {
    abstract fun process(update: UpdateWrapper): Any

    fun sendMessage(update: UpdateWrapper, msg: String, keyboard: ReplyKeyboardMarkup? = null): SendMessage {
        val messageBuilder = SendMessage
            .builder()
            .chatId(update.chatId)
            .text(msg)
        if (msg.contains("<")) { // todo for testing. will be replaced with normal regexp
            messageBuilder
                .parseMode("HTML")
                .build()
        }
        if (keyboard != null) {
            messageBuilder.replyMarkup(keyboard)
        }
        return messageBuilder.build()
    }

    fun next(next: Action?) {
        this.next = next
    }

    fun proceedToNext(): Any {
        nextToProcess = Active.NEXT
        waitForResponse = false
        return SilentMessage()
    }

    fun proceedToPrevious(): Any {
        nextToProcess = Active.PREVIOUS
        waitForResponse = false
        return SilentMessage()
    }

    fun repeatCurrent() {
        nextToProcess = Active.CURRENT
        waitForResponse = true
    }
}

enum class Active {
    CURRENT, PREVIOUS, NEXT
}