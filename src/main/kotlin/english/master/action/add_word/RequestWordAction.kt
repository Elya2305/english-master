/*
package english.master.action.add_word

import english.master.action.Action
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Deprecated("Use /add_word_with_choice")
class RequestWordAction(next: Action? = null) : Action(next) {

    override fun process(update: Update): SendMessage {

        return SendMessage
            .builder()
            .chatId(update.message.chatId)
            .text("Yay, it's time for new words! Please provide the word :)")
            .build()
    }
}
*/
