package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper

class ValidateWordAction : Action(waitForResponse = false) {
    override fun process(update: UpdateWrapper): Any {
        val word = update.text!!
        if (isNotAWord(word)) {
            repeatCurrent()
            return sendMessage(update, "It's not a word... Please provide a correct word")
        }
        proceedToNext()
        return SilentMessage()
    }

    private fun isNotAWord(word: String): Boolean {
        return word.isBlank() || !word.replace(" ", "").toCharArray().all { it.isLetter() }
    }
}