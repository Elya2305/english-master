package english.master.util

import english.master.db.CardRecord
import org.apache.commons.io.FileUtils
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import java.io.File

object MessageUtils {

    fun generateInputMediaPhoto(card: CardRecord, frontImg: Boolean = false): InputMediaPhoto {
        val photo = InputMediaPhoto()
        val file = File(card.word)
        FileUtils.writeByteArrayToFile(file, if (frontImg) card.frontImg else card.backImg)
        photo.setMedia(file, file.name)
        photo.caption = if (frontImg) "" else card.description
        photo.parseMode = if (card.description.contains("<")) "HTML" else null
        return photo
    }
}