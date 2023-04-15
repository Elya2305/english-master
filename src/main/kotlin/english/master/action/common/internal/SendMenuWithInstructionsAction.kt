package english.master.action.common.internal

import english.master.action.Action
import english.master.domain.MemorizableMessage
import english.master.domain.MessageList
import english.master.domain.UpdateWrapper
import english.master.util.CacheService
import english.master.util.KeyboardHelper
import english.master.util.MenuEntryData
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class SendMenuWithInstructionsAction : Action() {
    private val MENU_NAVIGATION = "\uD83E\uDD13 Use this menu to generate description in the card. When you're done send \"Next\""

    override fun process(update: UpdateWrapper): Any {

        return MessageList(
            listOf(
                sendMessage(update, "\uD83E\uDD13 Here're all the definitions I found:"),
                MemorizableMessage(sendDefinition(update), "MENU"),
                MemorizableMessage(initMenuNavigation(update), "MENU_NAVIGATION")
            )
        )
    }

    private fun sendDefinition(update: UpdateWrapper): SendMessage {
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
                        listSize = CacheService.getDefinitions(update.userId)!!.definitions.size,
                        midButtonName = "Add",
                        midButtonAction = "add"
                    )
                )
            )
            .text(MENU_NAVIGATION)
            .build()
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


    private fun isDefinitionChosen(update: UpdateWrapper, index: Int): Boolean {
        val definitions = CacheService.getDefinitions(update.userId)!!.definitions
        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        return chosenDefinitions.contains(definitions[index])
    }
}