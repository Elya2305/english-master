package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.action.Active
import english.master.client.UrbanClient
import english.master.client.WordDefinition
import english.master.db.repo.CardRepo
import english.master.domain.Definitions
import english.master.domain.MemorizableMessage
import english.master.domain.MessageList
import english.master.domain.ReservedWords.NEXT
import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper
import english.master.util.CacheService
import english.master.util.CacheService.getDefinitions
import english.master.util.CacheService.getMessageId
import english.master.util.KeyboardHelper
import english.master.util.MenuEntryData
import english.master.util.MessageUtils
import english.master.util.equalsIgnoreCase
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

class GenerateMenuAction : Action(nextToProcess = Active.CURRENT) {
    private val dictionaryClient = UrbanClient()
    private val cardRepo = CardRepo()
    private val MENU_NAVIGATION =
        "\uD83E\uDD13 Use this menu to generate description in the card. When you're done send \"Next\""

    override fun process(update: UpdateWrapper): Any {
        if (getDefinitions(update.userId) == null) {
            return sendMenuWithInstructions(update)
        }
        if (update.callbackQuery != null) {
            return handleMenuCallback(update)
        }
        if (NEXT.equalsIgnoreCase(update.text)) {
            return sendDescriptionAndContinue(update)
        }
        return sendMessage(update, "Please use menu")
    }

    private fun sendMenuWithInstructions(update: UpdateWrapper): Any {
        val definitions = dictionaryClient.getWordDefinitions(update.text!!)
        if (definitions.isEmpty()) {
            return sendMessage(update, "Sorry, I can't find this word")
        }
        return MessageList(
            listOf(
                sendMessage(update, "\uD83E\uDD13 Here're all the definitions I found:"),
                MemorizableMessage(initDefinitionsAndSendMenu(definitions, update), "MENU"),
                MemorizableMessage(initMenuNavigation(update), "MENU_NAVIGATION")
            )
        )
    }

    private fun sendDescriptionAndContinue(update: UpdateWrapper): SilentMessage {
        nextToProcess = Active.NEXT
        waitForResponse = false
        val card = CacheService.getCard(update.userId)
        CacheService.putCard(update.userId, card.copy(description = descriptionOfChosenCards(update)))
        cardRepo.update(card.copy(description = descriptionOfChosenCards(update)))
        return SilentMessage()
    }

    private fun initDefinitionsAndSendMenu(definitions: List<WordDefinition>, update: UpdateWrapper): SendMessage {
        CacheService.putDefinitions(update.userId, Definitions(update.text!!, definitions))
        CacheService.putChosenDefinitions(update.userId, ArrayList())
        return SendMessage
            .builder()
            .chatId(update.chatId)
            .parseMode("HTML")
            .text(definition(update))
            .build()
    }

    private fun initMenuNavigation(update: UpdateWrapper): SendMessage {
        return SendMessage
            .builder()
            .chatId(update.chatId)
            .replyMarkup(
                KeyboardHelper.buildKeyboard(
                    MenuEntryData(
                        listSize = getDefinitions(update.userId)!!.definitions.size,
                        midButtonName = "Add",
                        midButtonAction = "add"
                    )
                )
            )
            .text(MENU_NAVIGATION)
            .build()
    }

    private fun handleMenuCallback(update: UpdateWrapper): MessageList {
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
        val messageId = getMessageId(update.userId, "MENU")
        val nextDefinition = definition(update, index)
        val buttonName = if (isDefinitionChosen(update, index)) "Remove" else "Add"

        val editMenuKeyboard = EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(update.messageId)
            .parseMode("HTML")
            .text(MENU_NAVIGATION)
            .replyMarkup(
                KeyboardHelper.buildKeyboard(
                    MenuEntryData(
                        index = index,
                        listSize = getDefinitions(update.userId)!!.definitions.size,
                        midButtonName = buttonName,
                        midButtonAction = buttonName.lowercase()
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
        val messageId = getMessageId(update.userId, "CARD")
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        val definitions = getDefinitions(update.userId)!!.definitions
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
            .messageId(getMessageId(update.userId, "MENU_NAVIGATION"))
            .parseMode("HTML")
            .text(MENU_NAVIGATION)
            .replyMarkup(
                KeyboardHelper.buildKeyboard(
                    MenuEntryData(
                        index = index,
                        listSize = getDefinitions(update.userId)!!.definitions.size,
                        midButtonName = midButtonMame,
                        midButtonAction = midButtonMame.lowercase()
                    )
                )
            )
            .build()

        val menuMsg = EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(getMessageId(update.userId, "MENU"))
            .parseMode("HTML")
            .text(definition(update, index))
            .build()

        return listOf(cardMsg, menuMsg, menuNavigationMsg)
    }

    private fun isDefinitionChosen(update: UpdateWrapper, index: Int): Boolean {
        val definitions = getDefinitions(update.userId)!!.definitions
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
        val definitions = getDefinitions(update.userId)!!.definitions
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
