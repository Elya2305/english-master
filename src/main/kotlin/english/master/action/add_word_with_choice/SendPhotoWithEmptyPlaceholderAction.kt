package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.action.Active
import english.master.client.BotClient
import english.master.db.CardRecord
import english.master.domain.MemorizableMessage
import english.master.domain.UpdateWrapper
import english.master.service.CardService
import english.master.service.ImageService
import english.master.util.PngGenerator
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.ByteArrayInputStream

class SendPhotoWithEmptyPlaceholderAction : Action(waitForResponse = false) {
    private val imageService = ImageService()
    private val cardService = CardService()
    private val botClient = BotClient()

    override fun process(update: UpdateWrapper): Any {
        val word = update.text!!

        botClient.sendMessage("Wait a sec, looking into it...", update.chatId)
        val image = imageService.findWorkingImage(word)
        if (image == null) {
            nextToProcess = Active.CURRENT
            waitForResponse = true
            return sendMessage(update, "You sure this word exists? Let's try with another word...")
        }
        generateCardAsync(word, image, update)
        val photo = InputFile(ByteArrayInputStream(image), word)
        val sendPhoto = SendPhoto.builder()
            .photo(photo)
            .chatId(update.chatId)
            .caption("[Placeholder for future description]")
            .parseMode("HTML")
            .build()

        proceedToNext()
        return MemorizableMessage(sendPhoto, "CARD")
    }

    private fun generateCardAsync(word: String, image: ByteArray, update: UpdateWrapper) {
        cardService.saveCardAsync(
            CardRecord(
                userId = update.userId,
                word = word,
                description = "",
                backImg = image,
                frontImg = PngGenerator.generateWordCard(word)
            )
        )
    }
}
