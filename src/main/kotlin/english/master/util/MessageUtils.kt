package english.master.util

import english.master.db.CardRecord
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import java.io.ByteArrayInputStream
import java.io.File

object MessageUtils {

    fun generateInputMediaPhoto(card: CardRecord, frontImg: Boolean = false): InputMediaPhoto {
        val photo = InputMediaPhoto()
        val file = File(card.word)
        val img = if (frontImg) card.frontImg else card.backImg
        photo.setMedia(ByteArrayInputStream(img), file.name)
        photo.caption = if (frontImg) "" else card.description
        photo.parseMode = if (card.description.contains("<")) "HTML" else null
        return photo
    }
}