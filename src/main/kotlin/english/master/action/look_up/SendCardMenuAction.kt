package english.master.action.look_up

import english.master.action.Action
import english.master.action.Active
import english.master.action.common.internal.HandleDescriptionMenuCallbackAction
import english.master.action.common.internal.SaveChosenDescriptionAction
import english.master.action.common.internal.SendMenuWithInstructionsAction
import english.master.domain.ReservedWords.NEXT
import english.master.domain.UpdateWrapper
import english.master.util.equalsIgnoreCase

class SendCardMenuAction : Action(nextToProcess = Active.CURRENT) {
    private val sendMenuWithInstructionsAction = SendMenuWithInstructionsAction()
    private val handleDescriptionMenuCallbackAction = HandleDescriptionMenuCallbackAction()
    private val sendChosenDescriptionAction = SaveChosenDescriptionAction()

    override fun process(update: UpdateWrapper): Any {
        if (isAddToCardRequested(update)) {
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

    private fun isAddToCardRequested(update: UpdateWrapper): Boolean {
        return update.callbackQuery?.data?.startsWith("add_to_card") ?: false
    }
}
