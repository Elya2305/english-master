package english.master.domain.message

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

data class MemorizableMessage(
    val message: PartialBotApiMethod<Message>,
    val identifier: String = ""
)