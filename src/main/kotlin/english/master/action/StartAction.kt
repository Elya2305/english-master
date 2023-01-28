package english.master.action

import english.master.db.UserRecord
import english.master.db.repo.UserRepo
import english.master.domain.UpdateWrapper
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class StartAction {
    private val userRepo = UserRepo()

    fun process(update: UpdateWrapper): SendMessage {
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
                Hey ${user.firstName}! I'm your new English guru. I can do not much stuff yet, but I'm improving my skills!
                
                So, for now you can create word cards with /new_card command. I'll find some definitions and you'll choose the best ones for your card.
                Also I want to warn you that I use urban dictionary underneath... if you know what I mean. 
                
                You can review the generated cards with /random_cards command. This function is pretty dumb for now, it'll just take random n cards. But in the future I'll watch your progress and generate more relevant cards 😎
                
                You can also quickly check definition of a word using /look_up. Note that it'll not generate a card
                
                If you want to translate a word please use /translate command
                
                ATTENTION! SPOILER ALERT!!!
                What's coming next:
                -> Card deletion
                -> "Smart" cards' set generation (I'll need some time to learn this one though)
                -> Dictionary setting
                -> Various quizzes that will help you to learn your words (suggestions are welcome)
                -> Suggestions are welcome
                
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
