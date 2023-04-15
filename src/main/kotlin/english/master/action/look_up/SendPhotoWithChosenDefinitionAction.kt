package english.master.action.look_up

import english.master.action.Action
import english.master.db.CardRecord
import english.master.domain.MemorizableMessage
import english.master.domain.UpdateWrapper
import english.master.service.CardService
import english.master.service.ImageService
import english.master.util.CacheService
import english.master.util.PngGenerator
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.ByteArrayInputStream
import java.lang.Integer.parseInt

class SendPhotoWithChosenDefinitionAction : Action(waitForResponse = false) {
    private val imageService = ImageService()
    private val cardService = CardService()

    override fun process(update: UpdateWrapper): Any {
        val index = parseInt(update.callbackQuery!!.data.split("#")[1])
        val wordDefinition = CacheService.getDefinitions(update.userId)

        val image = imageService.findWorkingImage(wordDefinition!!.requestedWord)!!

        val cardRecord = CardRecord(
            userId = update.userId,
            word = wordDefinition.requestedWord,
            backImg = image,
            frontImg = PngGenerator.generateWordCard(wordDefinition.requestedWord)
        )
        cardService.saveCardAsync(cardRecord)

        val chosenDefinitions = CacheService.getChosenDefinitions(update.userId)
        chosenDefinitions.add(wordDefinition.definitions[index])

        val photo = InputFile(ByteArrayInputStream(image), wordDefinition.requestedWord)
        val sendPhoto = SendPhoto.builder()
            .photo(photo)
            .chatId(update.chatId)
            .caption(definition(update, index))
            .parseMode("HTML")
            .build()

        proceedToNext()
        return MemorizableMessage(sendPhoto, "CARD")
    }

    private fun definition(update: UpdateWrapper, index: Int = 0): String {
        val definitions = CacheService.getDefinitions(update.userId)!!.definitions
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