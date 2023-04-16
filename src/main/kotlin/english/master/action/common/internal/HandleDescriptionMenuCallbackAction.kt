package english.master.action.common.internal

import english.master.action.Action
import english.master.domain.MessageList
import english.master.domain.UpdateWrapper
import english.master.util.Button
import english.master.util.CacheService
import english.master.util.KeyboardHelper
import english.master.util.KeyboardNavigationData
import english.master.util.MessageUtils
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

class HandleDescriptionMenuCallbackAction : Action() {
    private val MENU_NAVIGATION =
        "\uD83E\uDD13 Use this menu to generate description in the card. When you're done send \"Next\""

    override fun process(update: UpdateWrapper): Any {

        val callback = AnswerCallbackQuery
            .builder()
            .callbackQueryId(update.callbackQuery!!.id)
            .build()

        val data = update.callbackQuery!!.data
        if (data.startsWith("add") || data.startsWith("remove")) {
            val list = addOrRemoveDescription(update)
            return MessageList(list)
        }
        if (data.startsWith("next") || data.startsWith("back")) {
            val list = swapDescription(update)
            return MessageList(list)
        }
        return MessageList(listOf(callback))
    }

    private fun swapDescription(update: UpdateWrapper): List<EditMessageText> {
        val index = update.callbackQuery!!.data.split("#")[1].toInt()
        val messageId = CacheService.getMessageId(update.userId, "MENU")
        val nextDefinition = definition(update, index)
        val buttonName = if (isDefinitionChosen(update, index)) "Remove" else "Add"

        val editMenuKeyboard = EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(update.messageId)
            .parseMode("HTML")
            .text(MENU_NAVIGATION)
            .replyMarkup(
                KeyboardHelper.buildNavigationKeyboard(
                    KeyboardNavigationData(
                        itemsSize = CacheService.getDefinitions(update.userId)!!.definitions.size,
                        index = index,
                        middleButtons = listOf(Button(buttonName, "${buttonName.lowercase()}#${index}"))
                    )
                )
            )
            .build()

        val editDescription = EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(messageId)
            .parseMode("HTML")
            .text(nextDefinition)
            .build()

        return listOf(editMenuKeyboard, editDescription)
    }

    private fun addOrRemoveDescription(update: UpdateWrapper): List<Any> {
        val index = update.callbackQuery!!.data.split("#")[1].toInt()
        val messageId = CacheService.getMessageId(update.userId, "CARD")
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        val definitions = CacheService.getDefinitions(update.userId)!!.definitions
        val midButtonMame = if (chosenDefinitions.contains(definitions[index])) {
            chosenDefinitions.remove(definitions[index])
            "Add"
        } else {
            chosenDefinitions.add(definitions[index])
            "Remove"
        }

        val card = CacheService.getCard(update.userId)
        val photo = MessageUtils.generateInputMediaPhoto(card.copy(description = descriptionOfChosenCards(update)))

        val cardMsg = EditMessageMedia
            .builder()
            .chatId(update.chatId)
            .messageId(messageId)
            .media(photo)
            .build()

        val menuNavigationMsg = EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(CacheService.getMessageId(update.userId, "MENU_NAVIGATION"))
            .parseMode("HTML")
            .text(MENU_NAVIGATION)
            .replyMarkup(
                KeyboardHelper.buildNavigationKeyboard(
                    KeyboardNavigationData(
                        itemsSize = CacheService.getDefinitions(update.userId)!!.definitions.size,
                        index = index,
                        middleButtons = listOf(Button(midButtonMame, "${midButtonMame.lowercase()}#${index}"))
                    )
                )
            )
            .build()

        val menuMsg = EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(CacheService.getMessageId(update.userId, "MENU"))
            .parseMode("HTML")
            .text(definition(update, index))
            .build()

        return listOf(cardMsg, menuMsg, menuNavigationMsg)
    }


    private fun isDefinitionChosen(update: UpdateWrapper, index: Int): Boolean {
        val definitions = CacheService.getDefinitions(update.userId)!!.definitions
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        return chosenDefinitions.contains(definitions[index])
    }

    private fun descriptionOfChosenCards(update: UpdateWrapper): String {
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        var description = ""
        chosenDefinitions.forEachIndexed { index, meaning ->
            run {
                description += "\n\n${index + 1}) ${format(meaning.definition)}\n<i>[Example]:</i> ${format(meaning.example)}"
            }
        }
        return description
    }

    private fun definition(update: UpdateWrapper, index: Int = 0): String {
        val mark = if (isDefinitionChosen(update, index)) "✅" else "☑"
        val definitions = CacheService.getDefinitions(update.userId)!!.definitions
        val example = format(definitions[index].example)
        var def = "$mark ${format(definitions[index].definition)}"
        if (example.isNotBlank()) def += "\n<i>[Example]:</i> $example"
        return def
    }

    private fun format(str: String): String {
        return str.replace("[", "")
            .replace("]", "")
    }
}