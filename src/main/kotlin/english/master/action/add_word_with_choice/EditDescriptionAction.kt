package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.ReservedWords.NEXT
import english.master.domain.ReservedWords.OK
import english.master.domain.UpdateWrapper
import english.master.domain.message.MemorizableMessage
import english.master.domain.message.MessageList
import english.master.util.CacheService
import english.master.util.MessageUtils.generateInputMediaPhoto
import english.master.util.equalsIgnoreCase
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import java.io.ByteArrayInputStream

class EditDescriptionAction : Action(repeat = true) {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        val msg = update.text!!
        if (NEXT.equalsIgnoreCase(msg)) {
            return showNewCard(update)
        }
        if (OK.equalsIgnoreCase(msg)) {
            repeat = false
            return sendMessage(update, "Word added to your dictionary")
        }

        val card = CacheService.getCard(update.userId)
        cardRepo.update(card.copy(description = msg))

        return EditMessageMedia
            .builder()
            .chatId(update.chatId)
            .messageId(CacheService.getMessageId(update.userId))
            .media(generateInputMediaPhoto(card.copy(description = msg)))
            .build()
    }

    private fun showNewCard(update: UpdateWrapper): MessageList {
        val card = CacheService.getCard(update.userId)
        return MessageList(
            listOf(
                MemorizableMessage(
                    SendPhoto.builder()
                        .photo(InputFile(ByteArrayInputStream(card.backImg), card.word))
                        .chatId(update.chatId)
                        .caption(card.description)
                        .parseMode("HTML")
                        .build()
                ),
                SendMessage
                    .builder()
                    .chatId(update.chatId)
                    .replyMarkup(okKeyboard())
                    .text("Nice. Here's your card. If you want to edit description send a new one, otherwise send \"OK\"")
                    .build()
            )
        )
    }

    private fun okKeyboard(): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup.builder()
            .oneTimeKeyboard(true)
            .resizeKeyboard(true)
            .keyboardRow(KeyboardRow(listOf(KeyboardButton("OK"))))
            .build()
    }
}