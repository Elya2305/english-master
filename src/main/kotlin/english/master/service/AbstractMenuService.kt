package english.master.service

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

abstract class AbstractMenuService(private val listSize: Int) {

    fun buildKeyboard(index: Int = 0, extraIdentifier: String = "", middleName: String = ""): InlineKeyboardMarkup {
        val identifier = if (extraIdentifier.isNotBlank()) "#$extraIdentifier" else ""

        val mid = button(middleButtonName(), "${middleButtonAction()}#${index}$identifier")
        val next = button("->", "next#${index + 1}$identifier")
        val back = button("<-", "back#${index - 1}$identifier")
        val disabled = button(" ", "skip")

        if (index == 0) {
            if (index == listSize - 1) {
                return InlineKeyboardMarkup.builder()
                    .keyboardRow(listOf(disabled, mid, disabled))
                    .build()
            }
            return InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(disabled, mid, next))
                .build()
        }

        if (index == listSize - 1) {
            return InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(back, mid, disabled))
                .build()
        }

        return InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(back, mid, next))
            .build()
    }

    private fun button(text: String, callback: String): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text(text).callbackData(callback)
            .build()
    }

    abstract fun middleButtonName(): String

    abstract fun middleButtonAction(): String

}