package english.master.domain

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class UpdateWrapper(val update: Update) {
    val chatId: Long
        get() {
            var chatId = update.message?.chatId
            if (chatId == null) {
                chatId = update.callbackQuery!!.message.chatId
            }
            return chatId!!
        }
    val messageId: Int
        get() {
            var messageId = update.message?.messageId
            if (messageId == null) {
                messageId = update.callbackQuery!!.message.messageId
            }
            return messageId!!
        }

    val userId: Long
        get() = user.id

    val user: User
        get() {
            var user = update.message?.from
            if (user == null) {
                user = update.callbackQuery!!.from
            }
            return user!!
        }

    val callbackQuery: CallbackQuery?
        get() = update.callbackQuery

    val message: Message?
        get() = update.message

    val text: String?
        get() {
            var text = update.message?.text
            if (text == null) {
                text = update.callbackQuery.message.text
            }
            return text
        }
}