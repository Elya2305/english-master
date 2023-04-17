package english.master.action.list_cards

import english.master.action.Action
import english.master.db.CardRecord
import english.master.domain.UpdateWrapper
import english.master.util.Button
import english.master.util.CacheService
import english.master.util.KeyboardHelper
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import java.io.ByteArrayInputStream

class SendChosenCardWithAvailableActionsAction : Action() {

    override fun process(update: UpdateWrapper): Any {
        val card = CacheService.getCard(update.userId)

        return SendPhoto.builder()
            .photo(InputFile(ByteArrayInputStream(card.backImg), card.word))
            .chatId(update.chatId)
            .caption(card.description)
            .parseMode("HTML")
            .replyMarkup(keyboardWithAvailableActions(card))
            .build()
    }

    private fun keyboardWithAvailableActions(card: CardRecord): InlineKeyboardMarkup {
        return KeyboardHelper.buildKeyboard(
            listOf(
//                Button("Edit", "edit#${card.id}"), // todo
                Button("Delete", "delete#${card.id}")
            )
        )
    }
}
