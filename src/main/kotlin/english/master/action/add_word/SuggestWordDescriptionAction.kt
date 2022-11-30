/*
package english.master.action.add_word

import english.master.action.Action
import english.master.client.BotClient
import english.master.client.GoogleImageClient
import english.master.client.UrbanClient
import english.master.client.WordDefinition
import english.master.db.CardRecord
import english.master.db.repo.CardRepo
import english.master.domain.message.MessageList
import english.master.util.PngGenerator
import english.master.util.takeMax
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import java.io.ByteArrayInputStream

@Deprecated("Use /add_word_with_choice")
class SuggestWordDescriptionAction(next: Action? = null) : Action(next) {

    private val dictionaryClient = UrbanClient()
    private val imageClient = GoogleImageClient()
    private val botClient = BotClient()
    private val cardRepo = CardRepo()

    override fun process(update: Update): Any {
        val word = update.message.text

        if (isNotAWord(word)) {
            repeat = true
            return sendMessage(update, "It's not a word... Please provide a correct word")
        }

        val definitions = dictionaryClient.getWordDefinitions(word)
        if (definitions.isEmpty()) {
            repeat = true
            return sendMessage(update, "Hm, I didn't find this word. Try another one")
        }

        botClient.sendMessage("Wait a sec, looking into it...", update.message.chatId)
        val image = findWorkingImage(word)
        val description = generateDescription(word, definitions)

        cardRepo.insert(
            CardRecord(
                userId = update.message.from.id,
                word = word,
                description = description,
                backImg = image,
                frontImg = PngGenerator.generateWordCard(word)
            )
        )

        val sendPhoto = SendPhoto.builder()
            .photo(InputFile(ByteArrayInputStream(image), word))
            .chatId(update.message.chatId)
            .caption(description)
            .parseMode("HTML")
            .build()

        repeat = false
        return MessageList(
            listOf(
                sendPhoto,
                sendMessage(update, "If the description is ok send \"OK\", otherwise input your description")
            )
        )
    }

    private fun findWorkingImage(word: String): ByteArray {
        val images = imageClient.getImages(word).results.shuffled()
        val shuffled = images.shuffled()
        for (img in shuffled) {
            val image = imageClient.downloadImage(img.urls.raw)
            if (image != null) {
                return image
            }
        }
        throw RuntimeException("No image")
    }

    private fun okKeyboard(): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup.builder()
            .keyboard(
                listOf(
                    KeyboardRow(
                        listOf(KeyboardButton("OK"))
                    )
                )
            ).build()
    }

    private fun generateDescription(word: String, definitions: List<WordDefinition>): String {
        var description = "<b>${word}</b>"

        definitions.takeMax(2)
            .forEachIndexed { index, meaning ->
                run {
                    description += "\n\n${index + 1}) ${format(meaning.definition)}\n<i>[Example]:</i> ${format(meaning.example)}"
                }
            }
        return description
    }

    private fun format(str: String): String {
        return str.replace("[", "")
            .replace("]", "")
    }

    private fun isNotAWord(word: String): Boolean {
        return word.isBlank() || !word.toCharArray().all { it.isLetter() }
    }
}*/
