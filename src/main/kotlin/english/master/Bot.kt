package english.master

import english.master.action.MenuAction
import english.master.domain.MemorizableMessage
import english.master.domain.MessageList
import english.master.domain.ReservedWords.EXIT
import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper
import english.master.processors.FlowProcessor
import english.master.util.CacheService
import english.master.util.Commands.getProcessor
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import java.lang.System.getenv

// todo replace print with normal logging
class Bot : TelegramLongPollingBot() {
    private val processors: HashMap<Long, FlowProcessor> = HashMap()

    override fun getBotToken(): String {
        return getenv("BOT_TOKEN")
    }

    override fun getBotUsername(): String {
        return "englishMasterBot"
    }

    override fun onUpdateReceived(update: Update?) {
        if (update == null) return
        val upd = UpdateWrapper(update)
        try {
            println(update)
            println("message: ${update.message?.text}")

            if (EXIT == upd.text) {
                removeActiveProcessor(upd)
                exit(upd)
                return
            }

            if (isCommand(upd)) {
                val command = getCommand(upd)
                CacheService.cleanCache(upd.userId)
                addActiveProcessor(upd, getProcessor(command))
            }

            val processor = getActiveProcessor(upd)
            if (processor != null) {
                do {
                    resolveExecute(processor.process(upd), upd)
                } while (!processor.waitForResponse)
                if (!processor.hasActive()) {
                    removeActiveProcessor(upd)
                }
                return
            }

            execute(MenuAction().process(upd))
        } catch (ex: Exception) {
            ex.printStackTrace()
            somethingWentWrong(upd)
        }
    }

    private fun getCommand(update: UpdateWrapper): String {
        return if (isCommand(update.text)) update.text!! else update.callbackData!!
    }

    private fun isCommand(update: UpdateWrapper): Boolean {
        val textIsCommand = isCommand(update.text)
        val callbackIsCommand = isCommand(update.callbackData)

        return textIsCommand || callbackIsCommand
    }

    private fun isCommand(txt: String?): Boolean {
        return txt?.startsWith("/") ?: false
    }

    private fun getActiveProcessor(update: UpdateWrapper): FlowProcessor? {
        return processors[update.userId]
    }

    private fun removeActiveProcessor(update: UpdateWrapper) {
        processors.remove(update.userId)
    }

    private fun addActiveProcessor(update: UpdateWrapper, processor: FlowProcessor): FlowProcessor {
        processors[update.userId] = processor
        return processor
    }

    private fun resolveExecute(message: Any, update: UpdateWrapper) {
        if (message is SendMessage) {
            execute(message)
        }
        if (message is SendPhoto) {
            execute(message)
        }
        if (message is EditMessageMedia) {
            execute(message)
        }
        if (message is EditMessageText) {
            execute(message)
        }
        if (message is AnswerCallbackQuery) {
            execute(message)
        }
        if (message is DeleteMessage) {
            execute(message)
        }
        if (message is MemorizableMessage) {
            if (message.message is SendPhoto) {
                val sentMessage = execute(message.message)
                CacheService.putMessageId(update.userId, sentMessage.messageId, message.identifier)
            }
            if (message.message is SendMessage) {
                val sentMessage = execute(message.message)
                CacheService.putMessageId(update.userId, sentMessage.messageId, message.identifier)
            }
        }
        if (message is SilentMessage) {
            return
        }
        if (message is MessageList) {
            message.messages.forEach { resolveExecute(it, update) }
        }
    }

    private fun exit(update: UpdateWrapper) {
        execute(
            SendMessage
                .builder()
                .chatId(update.chatId)
                .text("Exited \uD83D\uDC4D")
                .build()
        )
    }

    private fun somethingWentWrong(update: UpdateWrapper) {
        execute(
            SendMessage
                .builder()
                .chatId(update.chatId)
                .text("Something went wrong")
                .build()
        )
    }
}
