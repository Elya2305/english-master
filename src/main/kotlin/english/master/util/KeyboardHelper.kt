package english.master.util

import english.master.util.KeyboardHelper.firstNavigationButton
import english.master.util.KeyboardHelper.lastNavigationButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

object KeyboardHelper {

    fun buildKeyboard(buttons: List<Button>): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboardRow(buttons.map { button(it.text, it.callback) })
            .build()
    }

    fun buildNavigationKeyboard(keyboardData: KeyboardNavigationData): InlineKeyboardMarkup {
        val firstButton = if (isFirstButtonDisabled(keyboardData)) disabledButton() else button(
            keyboardData.firstButton.text,
            keyboardData.firstButton.callback
        )

        val lastButton = if (isLastButtonDisabled(keyboardData)) disabledButton() else button(
            keyboardData.lastButton.text,
            keyboardData.lastButton.callback
        )
        val middleButtons = keyboardData.middleButtons.map { button(it.text, it.callback) }

        return InlineKeyboardMarkup.builder()
            .keyboardRow(listOf(firstButton, *middleButtons.toTypedArray(), lastButton))
            .build()
    }

    fun firstNavigationButton(index: Int, identifier: String? = null): Button {
        return Button("⬅", "back#${index + 1}#${identifier}")
    }

    fun lastNavigationButton(index: Int, identifier: String? = null): Button {
        return Button("➡", "next#${index + 1}#${identifier}")
    }

    private fun isFirstButtonDisabled(keyboardData: KeyboardNavigationData): Boolean {
        val isFirstEntry = keyboardData.index == 0
        val isOneEntry = keyboardData.itemsSize == 1
        val isZeroEntry = keyboardData.itemsSize == 0

        return isFirstEntry || isOneEntry || isZeroEntry
    }

    private fun isLastButtonDisabled(keyboardData: KeyboardNavigationData): Boolean {
        val isLastEntry = keyboardData.index == keyboardData.itemsSize - 1
        val isOneEntry = keyboardData.itemsSize == 1
        val isZeroEntry = keyboardData.itemsSize == 0

        return isLastEntry || isOneEntry || isZeroEntry
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

data class KeyboardNavigationData(
    val itemsSize: Int,
    val index: Int = 0,
    val firstButton: Button = firstNavigationButton(index),
    val lastButton: Button = lastNavigationButton(index),
    val middleButtons: List<Button> = listOf(),
)

data class Button(
    val text: String,
    val callback: String,
)
