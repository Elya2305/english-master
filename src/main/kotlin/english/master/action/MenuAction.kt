package english.master.action

import english.master.domain.UpdateWrapper
import english.master.util.Button
import english.master.util.Commands.getCommandDescriptionMapping
import english.master.util.KeyboardHelper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

class MenuAction : Action() {

    override fun process(update: UpdateWrapper): SendMessage {
        return SendMessage.builder()
            .chatId(update.chatId)
            .parseMode("HTML")
            .text("\uD83E\uDD13 \uD83D\uDCAC")
            .replyMarkup(menuKeyboard())
            .build()
    }

    private fun menuKeyboard(): InlineKeyboardMarkup {
        val buttons = getCommandDescriptionMapping()
            .map { Button(it.second, it.first) }

        return KeyboardHelper.buildKeyboard(
            buttons = buttons,
            buttonPerRow = 1
        )
    }
}
