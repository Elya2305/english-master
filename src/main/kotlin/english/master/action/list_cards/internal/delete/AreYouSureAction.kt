package english.master.action.list_cards.internal.delete

import english.master.action.Action
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class AreYouSureAction : Action() {

    override fun process(update: UpdateWrapper): Any {
        return sendMessage(update, "Are you sure you want to delete a card?", keyboard())
    }

    private fun keyboard(): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup.builder()
            .oneTimeKeyboard(true)
            .resizeKeyboard(true)
            .keyboardRow(KeyboardRow(listOf(KeyboardButton("Yes"), KeyboardButton("No"))))
            .build()
    }
}
