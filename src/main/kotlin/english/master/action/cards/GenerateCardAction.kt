package english.master.action.cards

import english.master.action.Action
import english.master.action.Active
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper
import english.master.util.CacheService
import english.master.util.KeyboardHelper
import english.master.util.MenuEntryData
import english.master.util.MessageUtils.generateInputMediaPhoto
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.ByteArrayInputStream

class GenerateCardAction : Action(nextToProcess = Active.CURRENT) {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        if (update.callbackQuery != null) {
            return handleCallback(update)
        }
        if (CacheService.getCards(update.userId) == null) {
            return generateCardSet(update)
        }
        return sendMessage(update, "Please use menu")
    }

    private fun generateCardSet(update: UpdateWrapper): Any {
        val number = update.text!!
        if (isNotANumber(number)) {
            return sendMessage(update, "It's not a number... Please provide a correct number")
        }
        val total = cardRepo.count(update.chatId)
        if (total < number.toInt()) {
            return sendMessage(update, "You have only $total cards... Please provide a correct number")
        }
        val cards = cardRepo.findRandomNByUser(update.userId, number.toInt())
        CacheService.putCards(update.userId, cards)
        return SendPhoto.builder()
            .photo(InputFile(ByteArrayInputStream(cards[0].frontImg), cards[0].word))
            .replyMarkup(
                KeyboardHelper.buildKeyboard(
                    MenuEntryData(
                        identifier = "front",
                        listSize = cards.size,
                        midButtonName = "Flip",
                        midButtonAction = "flip"
                    )
                )
            )
            .chatId(update.chatId)
            .build()
    }

    private fun handleCallback(update: UpdateWrapper): Any {
        val data = update.callbackQuery!!.data
        if (data.startsWith("flip")) {
            return handleFlip(update)
        }
        if (data.startsWith("next")) {
            return handleNext(update)
        }
        if (data.startsWith("back")) {
            return handleBack(update)
        }
        if (data == "skip") {
            return handleSkip(update)
        }
        throw RuntimeException()
    }

    private fun handleSkip(update: UpdateWrapper): Any {
        return AnswerCallbackQuery
            .builder()
            .callbackQueryId(update.callbackQuery!!.id)
            .build()
    }

    private fun handleBack(update: UpdateWrapper): EditMessageMedia {
        return swapCard(update)
    }

    private fun handleNext(update: UpdateWrapper): EditMessageMedia {
        return swapCard(update)
    }

    private fun handleFlip(update: UpdateWrapper): EditMessageMedia {
        return swapCard(update, true)
    }

    private fun swapCard(update: UpdateWrapper, flip: Boolean = false): EditMessageMedia {
        val data = update.callbackQuery!!.data
        val currentIndex = data.split("#")[1].toInt()
        val card = CacheService.getCards(update.userId)!![currentIndex]
        val fromFront = flipIdentifier(flip, data)

        val photo = generateInputMediaPhoto(card, fromFront)
        return EditMessageMedia
            .builder()
            .chatId(update.chatId)
            .messageId(update.messageId)
            .media(photo)
            .replyMarkup(
                KeyboardHelper.buildKeyboard(
                    MenuEntryData(
                        index = currentIndex,
                        identifier = if (fromFront) "front" else "back",
                        listSize = CacheService.getCards(update.userId)!!.size,
                        midButtonName = "Flip",
                        midButtonAction = "flip"
                    )
                )
            )
            .build()
    }

    private fun flipIdentifier(flip: Boolean, data: String): Boolean {
        val fromSide = data.split("#")[2]
        val fromFront = fromSide == "front"
        return if (flip) !fromFront else fromFront
    }

    private fun isNotANumber(number: String): Boolean {
        return !number.toCharArray().all { it.isDigit() }
    }
}
