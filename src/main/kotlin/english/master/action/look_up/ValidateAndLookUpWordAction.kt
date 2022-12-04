package english.master.action.look_up

import english.master.action.Action
import english.master.action.Active
import english.master.client.UrbanClient
import english.master.client.WordDefinition
import english.master.domain.SilentMessage
import english.master.domain.UpdateWrapper
import english.master.util.CacheService

class ValidateAndLookUpWordAction : Action(waitForResponse = false) {
    private val dictionaryClient = UrbanClient()

    override fun process(update: UpdateWrapper): Any {
        if (isNotAWord(update)) {
            repeatAttempt()
            return sendMessage(update, "It's not a word... Please provide a correct word")
        }
        val definitions = dictionaryClient.getWordDefinitions(update.text!!)
        if (definitions.isEmpty()) {
            repeatAttempt()
            return sendMessage(update, "Sorry, I can't find this word")
        }
        initDefinitions(update, definitions)
        goOn()
        return SilentMessage()
    }

    private fun initDefinitions(update: UpdateWrapper, definitions: List<WordDefinition>) {
        CacheService.putDefinitions(update.userId, definitions)
        CacheService.putChosenDefinitions(update.userId, ArrayList())
    }

    private fun repeatAttempt() {
        nextToProcess = Active.CURRENT
        waitForResponse = true
    }

    private fun goOn() {
        nextToProcess = Active.NEXT
        waitForResponse = false
    }

    private fun isNotAWord(update: UpdateWrapper): Boolean {
        val word = update.text
        return word == null || word.isBlank() ||
                !word.replace(" ", "").toCharArray().all { it.isLetter() }
    }
}
