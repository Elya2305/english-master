package english.master.action.add_word_with_choice

import english.master.action.Action
import english.master.action.Active
import english.master.action.common.internal.HandleDescriptionMenuCallbackAction
import english.master.action.common.internal.SaveChosenDescriptionAction
import english.master.action.common.internal.SendMenuWithInstructionsAction
import english.master.client.UrbanClient
import english.master.domain.Definitions
import english.master.domain.ReservedWords.NEXT
import english.master.domain.UpdateWrapper
import english.master.util.CacheService
import english.master.util.CacheService.getDefinitions
import english.master.util.equalsIgnoreCase

class SendCardMenuAction : Action(nextToProcess = Active.CURRENT) {

    private val sendMenuWithInstructionsAction = SendMenuWithInstructionsAction()
    private val handleDescriptionMenuCallbackAction = HandleDescriptionMenuCallbackAction()
    private val sendChosenDescriptionAction = SaveChosenDescriptionAction()
    private val dictionaryClient = UrbanClient()

    override fun process(update: UpdateWrapper): Any {
        if (getDefinitions(update.userId) == null) {
            val definitions = dictionaryClient.getWordDefinitions(update.text!!)
            if (definitions.isEmpty()) {
                return sendMessage(update, "Sorry, I can't find this word")
            }
            CacheService.putDefinitions(update.userId, Definitions(update.text!!, definitions))
            CacheService.putChosenDefinitions(update.userId, ArrayList())
            return sendMenuWithInstructionsAction.process(update)
        }
        if (update.callbackQuery != null) {
            return handleDescriptionMenuCallbackAction.process(update)
        }
        if (NEXT.equalsIgnoreCase(update.text)) {
            proceedToNext()
            return sendChosenDescriptionAction.process(update)
        }
        return sendMessage(update, "Please use menu")
    }
}
