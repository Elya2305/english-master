package english.master.action

import english.master.db.UserRecord
import english.master.db.repo.UserRepo
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class StartAction : Action() {
    private val userRepo = UserRepo()

    override fun process(update: UpdateWrapper): SendMessage {
        val user = update.user
        userRepo.insertOrUpdate(
            UserRecord(
                user.id,
                user.userName,
                user.firstName
            )
        )

        return SendMessage
            .builder()
            .chatId(update.chatId)
            .text(
                """
                Hey ${user.firstName}! I'm your new English guru 🤓
                
                You can create word cards with /new_card command. I'll find some definitions and you'll choose the best ones for your card.
                Also I want to warn you that I use urban dictionary underneath... if you know what I mean. 
                
                To review X random generated cards use /random_cards command.
                
                Here're some other commands:
                /look_up - to quickly check definition of a word 
                /translate - to translate a word
                /list_cards - to view a list of cards and make some changes to it. Available actions: delete
                /menu - to list available commands
                
                So let's get started!🚀
            """.trimIndent()
            )
            .replyMarkup(keyboard())
            .build()
    }

    private fun keyboard(): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup.builder()
            .oneTimeKeyboard(true)
            .resizeKeyboard(true)
            .keyboardRow(KeyboardRow(listOf(KeyboardButton("EXIT"), KeyboardButton("NEXT"), KeyboardButton("OK"))))
            .build()
    }
}
