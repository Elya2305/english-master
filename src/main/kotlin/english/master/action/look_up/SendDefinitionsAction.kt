package english.master.action.look_up

import english.master.action.Action
import english.master.action.Active
import english.master.domain.MemorizableMessage
import english.master.domain.MessageList
import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper
import english.master.util.Button
import english.master.util.CacheService.getDefinitions
import english.master.util.CacheService.getMessageId
import english.master.util.KeyboardHelper.buildNavigationKeyboard
import english.master.util.KeyboardNavigationData
import english.master.util.containsIgnoreCase
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

class SendDefinitionsAction : Action() {

    override fun process(update: UpdateWrapper): Any {
        nextToProcess = Active.CURRENT
        waitForResponse = true

        return when {
            update.callbackQuery != null -> handleMenuCallback(update)
            isNewWord(update) -> processPrevious()
            else -> sendMenu(update)
        }
    }

    private fun isNewWord(update: UpdateWrapper): Boolean {
        return !getDefinitions(update.userId)!!.requestedWord.containsIgnoreCase(update.text!!)
    }

    private fun processPrevious(): SilentMessage {
        waitForResponse = false
        nextToProcess = Active.PREVIOUS
        return SilentMessage()
    }

    private fun sendMenu(update: UpdateWrapper): Any {
        val menu = SendMessage
            .builder()
            .chatId(update.chatId)
            .parseMode("HTML")
            .text(definition(update))
            .replyMarkup(
                buildNavigationKeyboard(
                    KeyboardNavigationData(
                        itemsSize = getDefinitions(update.userId)!!.definitions.size,
                        middleButtons = listOf(Button("Add to cards", "add_to_cards#0"))
                    )
                )
            )
            .build()

        val prevMenuId = getMessageId(update.userId, "MENU")
        if (prevMenuId != null) {
            val deletePrevMenu = DeleteMessage.builder()
                .chatId(update.chatId)
                .messageId(prevMenuId)
                .build()
            return MessageList(listOf(MemorizableMessage(menu, "MENU"), deletePrevMenu))
        }
        return MessageList(listOf(MemorizableMessage(menu, "MENU")))
    }

    private fun handleMenuCallback(update: UpdateWrapper): Any {
        val data = update.callbackQuery!!.data
        if (data.startsWith("next") || data.startsWith("back")) {
            return swapDescription(update)
        }
        if (data.startsWith("add_to_cards")) {
            proceedToNext()
        }
        return AnswerCallbackQuery
            .builder()
            .callbackQueryId(update.callbackQuery!!.id)
            .build()
    }

    private fun swapDescription(update: UpdateWrapper): EditMessageText {
        val index = update.callbackQuery!!.data.split("#")[1].toInt()
        val messageId = getMessageId(update.userId, "MENU")
        val nextDefinition = definition(update, index)
        return EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(messageId)
            .parseMode("HTML")
            .replyMarkup(
                buildNavigationKeyboard(
                    KeyboardNavigationData(
                        itemsSize = getDefinitions(update.userId)!!.definitions.size,
                        middleButtons = listOf(Button("Add to cards", "add_to_cards#0"))
                    )
                )
            )
            .text(nextDefinition)
            .build()
    }

    private fun definition(update: UpdateWrapper, index: Int = 0): String {
        val definitions = getDefinitions(update.userId)!!.definitions
        val example = format(definitions[index].example)
        var def = "${index + 1}) ${format(definitions[index].definition)}"
        if (example.isNotBlank()) def += "\n<i>[Example]:</i> $example"
        return def
    }

    private fun format(str: String): String {
        return str.replace("[", "")
            .replace("]", "")
    }
}