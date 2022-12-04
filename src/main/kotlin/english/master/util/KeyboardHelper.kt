package english.master.util

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

object KeyboardHelper {

    fun buildKeyboard(data: MenuEntryData): InlineKeyboardMarkup {
        if (!data.midButtonIsPresent) {
            return buildKeyboardWithoutMidButton(data)
        }

        if (data.isFirstEntry()) {
            if (data.isOneEntry()) {
                return InlineKeyboardMarkup.builder()
                    .keyboardRow(listOf(disabledButton(), midButton(data), disabledButton()))
                    .build()
            }
            return InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(disabledButton(), midButton(data), nextButton(data)))
                .build()
        }

        if (data.isLastEntry()) {
            return InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(backButton(data), midButton(data), disabledButton()))
                .build()
        }

        return InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(backButton(data), midButton(data), nextButton(data)))
            .build()
    }

    private fun buildKeyboardWithoutMidButton(data: MenuEntryData): InlineKeyboardMarkup {
        if (data.isFirstEntry()) {
            if (data.isOneEntry()) {
                return InlineKeyboardMarkup.builder()
                    .keyboardRow(listOf(disabledButton(), disabledButton()))
                    .build()
            }
            return InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(disabledButton(), nextButton(data)))
                .build()
        }

        if (data.isLastEntry()) {
            return InlineKeyboardMarkup.builder()
                .keyboardRow(listOf(backButton(data), disabledButton()))
                .build()
        }

        return InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(backButton(data), nextButton(data)))
            .build()
    }


    private fun nextButton(data: MenuEntryData): InlineKeyboardButton {
        return button("➡", "next#${data.index + 1}#${data.identifier}")
    }

    private fun backButton(data: MenuEntryData): InlineKeyboardButton {
        return button("⬅", "back#${data.index - 1}#${data.identifier}")
    }

    private fun midButton(data: MenuEntryData): InlineKeyboardButton {
        return button(data.midButtonName!!, "${data.midButtonAction}#${data.index}#${data.identifier}")
    }

    private fun disabledButton(): InlineKeyboardButton {
        return button(" ", "skip")
    }

    private fun button(text: String, callback: String): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text(text).callbackData(callback)
            .build()
    }
}

data class MenuEntryData(
    val listSize: Int,
    val midButtonIsPresent: Boolean = true,
    val midButtonName: String? = null,
    val midButtonAction: String? = null,
    val index: Int = 0,
    val identifier: String = ""
) {
    fun isFirstEntry(): Boolean {
        return index == 0
    }

    fun isOneEntry(): Boolean {
        return index == listSize - 1
    }

    fun isLastEntry(): Boolean {
        return index == listSize - 1
    }
}
