package english.master.domain

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

class SilentMessage

data class MessageList(
    val messages: List<Any>
)

data class MemorizableMessage(
    val message: PartialBotApiMethod<Message>,
    val identifier: String = ""
)