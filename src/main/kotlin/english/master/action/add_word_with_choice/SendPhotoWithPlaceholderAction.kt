package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.action.Active
import english.master.client.BotClient
import english.master.client.GoogleImageClient
import english.master.db.CardRecord
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper
import english.master.domain.MemorizableMessage
import english.master.util.CacheService
import english.master.util.PngGenerator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.ByteArrayInputStream
import java.time.Instant

class SendPhotoWithPlaceholderAction : Action(waitForResponse = false) {
    private val imageClient = GoogleImageClient()
    private val cardRepo = CardRepo()
    private val botClient = BotClient()

    override fun process(update: UpdateWrapper): Any {
        val word = update.text!!
        if (isNotAWord(word)) {
            nextToProcess = Active.CURRENT
            waitForResponse = true
            return sendMessage(update, "It's not a word... Please provide a correct word")
        }

        botClient.sendMessage("Wait a sec, looking into it...", update.chatId)
        val image = findWorkingImage(word)
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
        waitForResponse = false
        nextToProcess = Active.NEXT
        return MemorizableMessage(sendPhoto, "CARD")
    }

    private fun generateCardAsync(word: String, image: ByteArray, update: UpdateWrapper) {
        GlobalScope.launch {
            val card = cardRepo.insert(
                CardRecord(
                    userId = update.userId,
                    word = word,
                    description = "",
                    backImg = image,
                    frontImg = PngGenerator.generateWordCard(word)
                )
            )
            CacheService.putCard(update.userId, card)
        }
    }

    private fun findWorkingImage(word: String): ByteArray? {
        println("${Instant.now()} findWorkingImage start")
        val images = imageClient.getImages(word).results.shuffled()
        val shuffled = images.shuffled()
        for (img in shuffled) {
            val image = imageClient.downloadImage(img.urls.raw)
            if (image != null) {
                println("${Instant.now()} findWorkingImage finish")
                return image
            }
        }
        return null
    }

    private fun isNotAWord(word: String): Boolean {
        return word.isBlank() || !word.replace(" ", "").toCharArray().all { it.isLetter() }
    }
}
