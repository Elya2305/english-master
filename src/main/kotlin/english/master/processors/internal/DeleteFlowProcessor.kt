package english.master.processors.internal

import english.master.action.list_cards.internal.delete.AreYouSureAction
import english.master.action.list_cards.internal.delete.DeleteAction
import english.master.processors.FlowProcessor

class DeleteFlowProcessor(
    action1: AreYouSureAction = AreYouSureAction(),
    action2: DeleteAction = DeleteAction(),
) : FlowProcessor(action1) {
    init {
        active = action1
        action1.next = action2
    }
}
