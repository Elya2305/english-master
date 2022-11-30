/*
package english.master.action.add_word

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.message.MessageList
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.ByteArrayInputStream

@Deprecated("Use /add_word_with_choice")
class EditDescriptionOrSaveAction(next: Action? = null) : Action(next) {
    private val cardRepo = CardRepo()

    override fun process(update: Update): Any {
        val msg = update.message.text
        if (msg.equals("ok", ignoreCase = true)) {
            repeat = false
            return sendMessage(update, "Word added to your dictionary")
        }

        repeat = true
        val card = cardRepo.findLastByUser(update.message.from.id)
        cardRepo.update(card.copy(description = msg))

        return MessageList(
            listOf(
                SendPhoto.builder()
                    .photo(InputFile(ByteArrayInputStream(card.backImg), card.word))
                    .chatId(update.message.chatId)
                    .caption(msg)
                    .parseMode(if (msg.contains("<")) "HTML" else null)
                    .build(),

                sendMessage(update, "If the description is ok send \"OK\", otherwise input your description")
            )
        )
    }
}*/
