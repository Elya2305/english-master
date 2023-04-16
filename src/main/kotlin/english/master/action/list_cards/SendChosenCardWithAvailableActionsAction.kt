package english.master.action.list_cards

import english.master.action.Action
import english.master.db.repo.CardRepo
import english.master.domain.UpdateWrapper
import english.master.util.Button
import english.master.util.KeyboardHelper
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.ByteArrayInputStream

class SendChosenCardWithAvailableActionsAction : Action() {
    private val cardRepo = CardRepo()

    override fun process(update: UpdateWrapper): Any {
        val index = Integer.parseInt(update.message!!.text).toLong() // todo validate
        val card = cardRepo.findIndexedByCreatedAt(index, update.userId)

        val sendPhoto = SendPhoto.builder()
            .photo(InputFile(ByteArrayInputStream(card.frontImg), card.word))
            .chatId(update.chatId)
            .caption(card.description)
            .parseMode("HTML")
            .replyMarkup(
                KeyboardHelper.buildKeyboard(
                    listOf(
                        Button("Swap", "delete#${card.id}"),
                        Button("Delete", "delete#${card.id}")
                    )
                )
            ) // todo
            .build()

        return sendPhoto
    }
}