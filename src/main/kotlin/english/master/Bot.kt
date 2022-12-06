package english.master

import english.master.action.StartAction
import english.master.domain.*
import english.master.processors.FlowProcessor
import english.master.processors.LookUpWordProcessor
import english.master.processors.NewWordProcessor
import english.master.processors.ShowCardsProcessor
import english.master.util.CacheService
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

            if (ReservedWords.EXIT == upd.text) {
                removeActiveProcessor(upd)
                exit(upd)
                return
            }

            if (isCommand(upd.text)) {
                CacheService.cleanCache(upd.userId)
            }

            if ("/start" == upd.text) {
                execute(StartAction().process(upd))
                return
            }

            if ("/random_cards" == upd.text) {
                resolveExecute(addActiveProcessor(upd, ShowCardsProcessor()).process(upd), upd)
                return
            }

            if ("/new_card" == upd.text) {
                resolveExecute(addActiveProcessor(upd, NewWordProcessor()).process(upd), upd)
                return
            }

            if ("/look_up" == upd.text) {
                resolveExecute(addActiveProcessor(upd, LookUpWordProcessor()).process(upd), upd)
                return
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

            iDontUnderstandYou(upd)
        } catch (ex: Exception) {
            ex.printStackTrace()
            somethingWentWrong(upd)
        }
    }

    private fun isCommand(text: String?): Boolean {
        val isCommand = text?.startsWith("/")
        return isCommand != null && isCommand
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

    private fun iDontUnderstandYou(update: UpdateWrapper) {
        execute(
            SendMessage
                .builder()
                .chatId(update.chatId)
                .text("I don't understand you \uD83D\uDE15")
                .build()
        )
    }
}