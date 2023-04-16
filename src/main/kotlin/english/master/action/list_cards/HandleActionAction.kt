package english.master.action.list_cards

import english.master.action.Action
import english.master.action.list_cards.internal.DeleteFlowProcessor
import english.master.domain.UpdateWrapper

class HandleActionAction : Action() {
    private val deleteFlowProcessor = DeleteFlowProcessor()

    override fun process(update: UpdateWrapper): Any {
        val callback = update.callbackQuery?.message?.text
        if (callback?.startsWith("swap") == true) {
            return sendMessage(update, "TBD")
        }
        if (callback?.startsWith("delete") == true) {
            return deleteFlowProcessor.process(update)
        }
        return sendMessage(update, "Please use provided keyboard")
    }
}