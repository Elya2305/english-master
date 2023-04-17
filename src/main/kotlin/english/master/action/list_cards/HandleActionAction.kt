package english.master.action.list_cards

import english.master.action.Action
import english.master.action.Active.CURRENT
import english.master.domain.UpdateWrapper
import english.master.processors.FlowProcessor
import english.master.processors.JumpTo
import english.master.processors.internal.DeleteFlowProcessor

class HandleActionAction : Action(nextToProcess = CURRENT) {
    private var activeProcessor: FlowProcessor? = null

    override fun process(update: UpdateWrapper): Any {
        if (isSwapAction(update)) {
            return sendMessage(update, "TBD")
        }
        if (isDeleteAction(update)) {
            activeProcessor = DeleteFlowProcessor()
        }
        if (activeProcessor == null) { // todo check
            return JumpTo(ValidateIndexAndSaveCardAction::class)
        }
        if (activeProcessor!!.isOver()) {
            return JumpTo(SendListOfCards::class)
        }
        val result = activeProcessor!!.process(update)
        waitForResponse = activeProcessor!!.hasActive() // todo think about it later
        return result
    }

    private fun isSwapAction(update: UpdateWrapper): Boolean {
        val callback = update.callbackQuery?.data
        return callback?.startsWith("swap") == true
    }

    private fun isDeleteAction(update: UpdateWrapper): Boolean {
        val callback = update.callbackQuery?.data
        return callback?.startsWith("delete") == true
    }
}