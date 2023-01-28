package english.master.action.translate

import english.master.action.Action
import english.master.action.Active
import english.master.client.TranslationClient
import english.master.domain.UpdateWrapper

class TranslateAction : Action(nextToProcess = Active.CURRENT) {
    private val translationClient = TranslationClient()

    override fun process(update: UpdateWrapper): Any {
        val word = update.text

        return sendMessage(update, translationClient.translate(word!!))
    }
}
