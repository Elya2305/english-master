package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.client.UrbanClient
import english.master.client.WordDefinition
import english.master.db.repo.CardRepo
import english.master.domain.ReservedWords.NEXT
import english.master.domain.UpdateWrapper
import english.master.domain.message.MessageList
import english.master.domain.message.SilentMessage
import english.master.service.AddCardMenuService
import english.master.util.CacheService
import english.master.util.MessageUtils
import english.master.util.equalsIgnoreCase
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class GenerateMenuAction : Action(repeat = true) {
    private val dictionaryClient = UrbanClient()
    private val cardRepo = CardRepo()
    private lateinit var menuService: AddCardMenuService

    override fun process(update: UpdateWrapper): Any {
        if (CacheService.getDefinitions(update.userId) == null) {
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
                sendMessage(update, "==============MENU=============="),
                initDefinitionsAndSendMenu(definitions, update),
                SendMessage
                    .builder()
                    .chatId(update.chatId)
                    .replyMarkup(nextKeyboard())
                    .text("Here're all the definitions I found. Use this menu to generate description in the card. When you're done send \"Next\"")
                    .build()
            )
        )
    }

    private fun handleMenuCallback(update: UpdateWrapper): MessageList {
        return MessageList(
            listOf(
                handleMenu(update),
                AnswerCallbackQuery
                    .builder()
                    .callbackQueryId(update.callbackQuery!!.id)
                    .build()
            )
        )
    }

    private fun sendDescriptionAndContinue(update: UpdateWrapper): SilentMessage {
        repeat = false
        waitForResponse = false
        val card = CacheService.getCard(update.userId)
        CacheService.putCard(update.userId, card.copy(description = descriptionOfChosenCards(update)))
        cardRepo.update(card.copy(description = descriptionOfChosenCards(update)))
        return SilentMessage()
    }

    private fun initDefinitionsAndSendMenu(definitions: List<WordDefinition>, update: UpdateWrapper): SendMessage {
        CacheService.putDefinitions(update.userId, definitions)
        CacheService.putChosenDefinitions(update.userId, ArrayList())
        menuService = AddCardMenuService(definitions.size)
        return SendMessage
            .builder()
            .chatId(update.chatId)
            .parseMode("HTML")
            .text(definition(update))
            .replyMarkup(menuService.buildKeyboard())
            .build()
    }

    private fun handleMenu(update: UpdateWrapper): Any {
        val data = update.callbackQuery!!.data
        if (data.startsWith("add")) {
            return addOrRemoveDescription(update)
        }
        if (data.startsWith("next") || data.startsWith("back")) {
            return swapDescription(update)
        }
        return AnswerCallbackQuery
            .builder()
            .callbackQueryId(update.callbackQuery!!.id)
            .build()
    }

    private fun swapDescription(update: UpdateWrapper): Any {
        val index = update.callbackQuery!!.data.split("#")[1].toInt()

        return EditMessageText
            .builder()
            .chatId(update.chatId)
            .messageId(update.messageId)
            .parseMode("HTML")
            .text(definition(update, index))
            .replyMarkup(menuService.buildKeyboard(index))
            .build()
    }

    private fun addOrRemoveDescription(update: UpdateWrapper): Any {
        val index = update.callbackQuery!!.data.split("#")[1].toInt()
        val messageId = CacheService.getMessageId(update.userId)
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        val definitions = CacheService.getDefinitions(update.userId)

        if (chosenDefinitions.contains(definitions!![index])) {
            chosenDefinitions.remove(definitions[index])
        } else {
            chosenDefinitions.add(definitions[index])
        }

        val card = CacheService.getCard(update.userId)
        val photo = MessageUtils.generateInputMediaPhoto(card.copy(description = descriptionOfChosenCards(update)))
        return EditMessageMedia
            .builder()
            .chatId(update.chatId)
            .messageId(messageId)
            .media(photo)
            .build()
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
        val definitions = CacheService.getDefinitions(update.userId)

        return "${index + 1}) ${format(definitions!![index].definition)}\n<i>[Example]:</i> ${
            format(definitions[index].example)
        }"
    }

    private fun format(str: String): String {
        return str.replace("[", "")
            .replace("]", "")
    }


    private fun nextKeyboard(): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup.builder()
            .oneTimeKeyboard(true)
            .resizeKeyboard(true)
            .keyboardRow(KeyboardRow(listOf(KeyboardButton("Next"))))
            .build()
    }
}
